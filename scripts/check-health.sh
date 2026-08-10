#!/usr/bin/env bash
# Check that all cinema local stack services are running and healthy.
# Does NOT start services — run after: docker compose up -d
#
# Usage:
#   ./scripts/check-health.sh
#   ./scripts/check-health.sh --wait   # retry until healthy or timeout

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

MAX_WAIT_SECONDS=90
POLL_INTERVAL=5
WAIT_MODE=0

# container_name|check_type|check_arg
# check_type:
#   health  -> docker Health.Status must be "healthy"
#   pg      -> pg_isready inside container (arg = "user:db")
#   http    -> server responds with any HTTP status (not connection refused)
SERVICES=(
  "db-cinemas-app|pg|postgres:db_cinema"
  "db-keycloak|pg|keycloak:db_keycloak"
  "keycloak|http|http://localhost:8088"
  "kong|health|"
  "cinemas-app|http|http://localhost:8082/api/actuator/health"
)

log() { printf '[INFO] %s\n' "$*"; }
ok() { printf '[ OK ] %s\n' "$*"; }
fail() { printf '[FAIL] %s\n' "$*" >&2; }
error() { printf '[ERROR] %s\n' "$*" >&2; }

usage() {
  cat <<EOF
Usage: $(basename "$0") [--wait] [--help]

Verifies cinema stack containers are up and responding.

Options:
  --wait   Keep checking until all healthy or timeout (${MAX_WAIT_SECONDS}s)
  --help   Show this help
EOF
}

for arg in "$@"; do
  case "${arg}" in
    --wait) WAIT_MODE=1 ;;
    --help|-h) usage; exit 0 ;;
    *) error "Unknown option: ${arg}"; usage; exit 1 ;;
  esac
done

if ! command -v docker >/dev/null 2>&1; then
  error "docker is not installed or not in PATH"
  exit 1
fi

if ! docker info >/dev/null 2>&1; then
  error "Docker daemon is not running"
  exit 1
fi

is_running() {
  local name="$1"
  [[ "$(docker inspect -f '{{.State.Running}}' "${name}" 2>/dev/null || echo false)" == "true" ]]
}

check_health_status() {
  local name="$1"
  local status
  status="$(docker inspect -f '{{if .State.Health}}{{.State.Health.Status}}{{else}}none{{end}}' "${name}" 2>/dev/null || echo missing)"
  [[ "${status}" == "healthy" ]]
}

check_pg() {
  local name="$1"
  local user_db="$2"
  local user="${user_db%%:*}"
  local db="${user_db##*:}"
  docker exec "${name}" pg_isready -U "${user}" -d "${db}" >/dev/null 2>&1
}

check_http() {
  local url="$1"
  local code
  # Any HTTP response means the process is listening.
  # curl -f would fail on 401/404 even if the app is up.
  if command -v curl >/dev/null 2>&1; then
    code="$(curl -sS -o /dev/null -w '%{http_code}' --max-time 3 "${url}" 2>/dev/null || echo 000)"
  else
    code="$(docker run --rm --network host curlimages/curl:8.5.0 \
      -sS -o /dev/null -w '%{http_code}' --max-time 3 "${url}" 2>/dev/null || echo 000)"
  fi
  [[ "${code}" != "000" ]]
}

check_one() {
  local name="$1"
  local type="$2"
  local arg="${3:-}"

  if ! is_running "${name}"; then
    fail "${name} — container not running"
    return 1
  fi

  case "${type}" in
    health)
      if check_health_status "${name}"; then
        ok "${name} — healthy"
        return 0
      fi
      fail "${name} — not healthy yet"
      return 1
      ;;
    pg)
      if check_pg "${name}" "${arg}"; then
        ok "${name} — postgres ready (${arg})"
        return 0
      fi
      fail "${name} — postgres not ready (${arg})"
      return 1
      ;;
    http)
      if check_http "${arg}"; then
        ok "${name} — HTTP OK (${arg})"
        return 0
      fi
      fail "${name} — HTTP not OK (${arg})"
      return 1
      ;;
    running)
      ok "${name} — running"
      return 0
      ;;
    *)
      fail "${name} — unknown check type: ${type}"
      return 1
      ;;
  esac
}

run_all_checks() {
  local failed=0
  local entry name type arg

  echo "----------------------------------------"
  log "Checking cinema stack health..."
  echo "----------------------------------------"

  for entry in "${SERVICES[@]}"; do
    IFS='|' read -r name type arg <<<"${entry}"
    if ! check_one "${name}" "${type}" "${arg}"; then
      failed=1
    fi
  done

  echo "----------------------------------------"
  return "${failed}"
}

elapsed=0
while true; do
  if run_all_checks; then
    log "All services look healthy."
    exit 0
  fi

  if [[ "${WAIT_MODE}" -eq 0 ]]; then
    error "One or more services are not healthy."
    error "Tip: ./scripts/check-health.sh --wait"
    exit 1
  fi

  if (( elapsed >= MAX_WAIT_SECONDS )); then
    error "Timeout after ${MAX_WAIT_SECONDS}s — stack still unhealthy"
    exit 1
  fi

  log "Retrying in ${POLL_INTERVAL}s... (${elapsed}s elapsed)"
  sleep "${POLL_INTERVAL}"
  elapsed=$((elapsed + POLL_INTERVAL))
done
