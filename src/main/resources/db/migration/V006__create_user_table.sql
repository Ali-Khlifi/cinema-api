-- ============================================
-- Migration V006: Create USER table
-- Purpose: User management synced with Keycloak
-- Author: Cinema Team
-- Date: 2025-12-14
-- Risk: LOW (new table, no impact on existing)
-- ============================================

-- Create user table
CREATE TABLE IF NOT EXISTS "user" (
    id BIGSERIAL PRIMARY KEY,
    
    -- Keycloak integration
    keycloak_id VARCHAR(255) UNIQUE NOT NULL,
    
    -- User information
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    date_of_birth DATE,
    
    -- Status
    is_active BOOLEAN DEFAULT true NOT NULL,
    
    -- Audit columns (Auditable interface)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL
);

-- Create indexes for performance
CREATE INDEX idx_user_keycloak_id ON "user"(keycloak_id);
CREATE INDEX idx_user_email ON "user"(email);
CREATE INDEX idx_user_is_active ON "user"(is_active);
CREATE INDEX idx_user_deleted_at ON "user"(deleted_at) WHERE deleted_at IS NULL;

-- Function to auto-update updated_at timestamp
CREATE OR REPLACE FUNCTION update_user_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to automatically update updated_at
CREATE TRIGGER trigger_update_user_updated_at
    BEFORE UPDATE ON "user"
    FOR EACH ROW
    EXECUTE FUNCTION update_user_updated_at();

-- Add table and column comments (documentation)
COMMENT ON TABLE "user" IS 'Registered users synchronized with Keycloak identity provider';
COMMENT ON COLUMN "user".id IS 'Primary key (user_id)';
COMMENT ON COLUMN "user".keycloak_id IS 'Unique identifier from Keycloak (JWT sub claim)';
COMMENT ON COLUMN "user".email IS 'User email address (unique)';
COMMENT ON COLUMN "user".is_active IS 'Account active status (false = disabled account)';
COMMENT ON COLUMN "user".deleted_at IS 'Soft delete timestamp (NULL = not deleted)';

-- Success message
DO $$
BEGIN
    RAISE NOTICE '✓ Migration V006 completed: USER table created successfully';
END $$;

