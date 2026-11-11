# Conception — Affiches de films sur S3 (P0.1)

Document de référence pour le stockage des affiches. Il décrit les décisions retenues et le fonctionnement vérifié en local le 6 novembre 2026. Il ne remplace pas le code.

## But

Une affiche n’est plus stockée dans la base. Postgres ne garde que la clé de l’objet S3. Le navigateur télécharge l’image directement chez AWS, avec une adresse temporaire.

L’ancienne colonne `media` (bytea) et la route `GET /api/films/{id}/media` restent en place le temps du basculement.

## Décisions

| Sujet | Choix |
|---|---|
| Bucket | Privé. Aucune URL publique permanente. |
| Environnements | `cinema-posters-dev` en local, `cinema-posters-prod` pour le site. Même code. |
| Région | `eu-north-1` (Stockholm). Le plan gratuit du compte n’ouvre pas Paris. |
| Identité | Un utilisateur IAM par usage. Celui de dev ne peut agir que sur `posters/` du bucket de dev. |
| Secrets | Variables d’environnement. Jamais dans Git. En local, fichier `.env` ignoré, lu par Docker Compose. |
| Lecture | URL signée, 15 minutes par défaut. |
| Écriture | L’API envoie le fichier. Le navigateur ne parle à S3 qu’en lecture. |

Le chiffrement du bucket est SSE-S3. L’accès public est bloqué. Il n’y a pas de versioning. Une alarme de budget à 1 USD prévient d’une dépense anormale.

## Donnée

Colonne `film.poster_key`, texte, 512 caractères, nullable.

Forme de la clé : `posters/{filmId}/{uuid}.jpg` (ou `.png`, `.webp`).

Un film sans affiche a cette colonne à `NULL`. Plusieurs séances du même film partagent la même clé.

## Écriture

`POST /api/films/{id}/poster`, formulaire multipart, champ `file`.

L’appelant doit être connecté. Le rôle administrateur n’existe pas encore : il arrivera avec P1.1. Jusque-là, tout utilisateur authentifié peut envoyer une affiche, comme pour l’ancienne route `media`.

Contrôles, dans cet ordre :

1. Le fichier n’est pas vide et ne dépasse pas 5 Mio.
2. Le contenu est un JPEG, un PNG ou un WebP. Le type annoncé par le client est ignoré. La détection se fait sur les premiers octets.
3. Le bucket et la région sont configurés. Sinon la réponse est 503 et la base n’est pas modifiée.
4. Le film existe.

L’objet est envoyé dans S3, puis la clé est enregistrée. Si l’enregistrement en base échoue, le nouvel objet est supprimé. L’ancienne clé n’est supprimée que si elle commence par `posters/` et qu’elle ne contient pas `..`. La colonne `media` n’est pas écrite.

## Lecture

Les réponses qui contiennent un film, y compris une séance, ajoutent `posterUrl`. La clé `poster_key` ne sort pas de l’API.

`posterUrl` est une adresse S3 signée pour un GET. Durée par défaut : 900 secondes. Une valeur hors de 60 à 3600 secondes empêche le démarrage en production.

`posterUrl` est `null` lorsque le film n’a pas d’affiche, lorsque S3 n’est pas configuré, lorsque la clé sort du préfixe `posters/`, ou lorsque la signature échoue. Dans ces cas, la liste des films répond quand même.

Le navigateur charge ensuite l’image chez S3. Le CORS du bucket de dev autorise `GET` et `HEAD` depuis `http://localhost:4200`. Celui du bucket de prod autorise `https://cinema.alikhlifi.ovh`.

## Affichage

La page `/cinemas` ne montre que les cinémas. L’affiche apparaît sur `/salles/{cinemaId}`, dans la carte de séance.

1. Si `posterUrl` est présente, elle devient la source de l’image.
2. Sinon, l’application demande l’ancienne route `/media`.
3. Si les deux manquent, le cadre affiche « Affiche indisponible ».

## Configuration

| Variable | Rôle | Défaut |
|---|---|---|
| `CINEMA_S3_BUCKET` | Nom du bucket | vide |
| `CINEMA_S3_REGION` | Région | vide |
| `CINEMA_S3_KEY_PREFIX` | Dossier des objets | `posters/` |
| `CINEMA_S3_MAX_BYTES` | Taille max | `5242880` |
| `CINEMA_S3_PRESIGN_SECONDS` | Durée de l’URL | `900` |
| `AWS_ACCESS_KEY_ID` | Clé d’accès | chaîne AWS |
| `AWS_SECRET_ACCESS_KEY` | Clé secrète | chaîne AWS |

En local, ces variables sont dans `cinema-api/.env` et passées au seul conteneur `cinemas-app`. Sans bucket, l’API démarre et `posterUrl` reste vide.

En production, le profil `prod` refuse de démarrer s’il manque le bucket, la région, un préfixe sain, une durée autorisée, ou des identifiants AWS. Le bucket de prod n’est pas encore branché sur le VPS.

## Santé

L’API publie Postgres et le bucket au même endroit :

`http://localhost:8082/api/actuator/health`

La réponse contient `db` et `s3`. `s3` est `UP` quand le bucket répond, `UNKNOWN` quand aucun bucket n’est configuré, `DOWN` quand le bucket configuré est injoignable. Si `db` ou `s3` est `DOWN`, l’adresse répond 503.

Le contrôle appelle `HeadBucket`. L’utilisateur IAM doit avoir `s3:ListBucket` sur le bucket, en plus de `GetObject`, `PutObject` et `DeleteObject` sur `posters/*`.

En production, la même route est `http://localhost:8080/api/actuator/health` dans le conteneur, et `https://cinema.alikhlifi.ovh/api/actuator/health` derrière Nginx. Le healthcheck Docker de l’API appelle déjà cette route.

## Vérification locale

Faite le 6 novembre 2026 :

- envoi d’une affiche pour le film 1 vers `cinema-posters-dev` ;
- `GET /api/filmProjection/byRoom?roomId=1` renvoie une `posterUrl` ;
- `http://localhost:4200/salles/1` affiche l’image, réponse S3 200.

Le jeton Keycloak utilisé par Bruno ou par le front expire au bout de 5 minutes. L’URL d’affiche, elle, expire au bout de 15 minutes. Une nouvelle lecture de la séance en calcule une autre.
