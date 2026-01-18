-- =====================================================
-- Migration V010: Update VILLE table
-- Description: Remove geographic coordinates (longitude, latitude, altitude)
-- Author: Cinema API Team
-- Date: 2025-12-21
-- Phase: 3
-- =====================================================

-- Drop unnecessary geographic coordinate columns
ALTER TABLE ville DROP COLUMN IF EXISTS longitude;
ALTER TABLE ville DROP COLUMN IF EXISTS latitude;
ALTER TABLE ville DROP COLUMN IF EXISTS altitude;

-- Add comment for documentation
COMMENT ON TABLE ville IS 'City table - stores city information for cinema locations';
COMMENT ON COLUMN ville.id IS 'Primary key - unique city identifier';
COMMENT ON COLUMN ville.name IS 'City name';









