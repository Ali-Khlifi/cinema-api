-- =====================================================
-- Migration V011: Update CINEMA table
-- Description: Refactor address structure into detailed fields
-- Author: Cinema API Team
-- Date: 2025-12-21
-- Phase: 3
-- =====================================================

-- Add new detailed address columns
ALTER TABLE cinema ADD COLUMN IF NOT EXISTS city VARCHAR(100);
ALTER TABLE cinema ADD COLUMN IF NOT EXISTS postal_code VARCHAR(10);
ALTER TABLE cinema ADD COLUMN IF NOT EXISTS street_number VARCHAR(20);
ALTER TABLE cinema ADD COLUMN IF NOT EXISTS street_name VARCHAR(255);

-- Migrate existing data (if adresse contains data, move to street_name as fallback)
UPDATE cinema 
SET street_name = adresse 
WHERE adresse IS NOT NULL 
  AND street_name IS NULL;

-- Drop old address column
ALTER TABLE cinema DROP COLUMN IF EXISTS adresse;

-- Add comments for documentation
COMMENT ON TABLE cinema IS 'Cinema table - stores cinema/theater information';
COMMENT ON COLUMN cinema.id IS 'Primary key - unique cinema identifier';
COMMENT ON COLUMN cinema.name IS 'Cinema name';
COMMENT ON COLUMN cinema.city IS 'City name where cinema is located';
COMMENT ON COLUMN cinema.postal_code IS 'Postal/ZIP code';
COMMENT ON COLUMN cinema.street_number IS 'Street number (e.g., 123, 45bis)';
COMMENT ON COLUMN cinema.street_name IS 'Street name (e.g., Avenue des Champs-Élysées)';
COMMENT ON COLUMN cinema.nombre_salles IS 'Number of rooms/halls in the cinema';
COMMENT ON COLUMN cinema.ville_id IS 'Foreign key to ville table';









