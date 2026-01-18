-- =====================================================
-- Migration V012: Merge SEANCE into FILM_PROJECTION
-- Description: Integrate seance time fields into film_projection and add max_projections
-- Author: Cinema API Team
-- Date: 2025-12-21
-- Phase: 4
-- =====================================================

-- Step 1: Add new columns to film_projection
ALTER TABLE film_projection ADD COLUMN IF NOT EXISTS start_time TIME;
ALTER TABLE film_projection ADD COLUMN IF NOT EXISTS end_time TIME;
ALTER TABLE film_projection ADD COLUMN IF NOT EXISTS max_projections INT DEFAULT 0;

-- Step 1b: Remove prix column (price managed at Ticket level with PriceType)
ALTER TABLE film_projection DROP COLUMN IF EXISTS prix;

-- Step 2: Migrate data from seance to film_projection
-- If there's a seance linked to a film_projection, copy the time data
UPDATE film_projection fp
SET 
    start_time = s.heure_debut::TIME,
    end_time = s.heure_fin::TIME
FROM seance s
WHERE s.film_projection_id = fp.id
  AND fp.start_time IS NULL; -- Only update if not already set

-- Step 2b: Set default times for projections without seance (fallback)
-- Use a time offset based on ID to avoid duplicates: 14:00 + (id % 6) hours
-- This creates time slots: 14:00, 15:00, 16:00, 17:00, 18:00, 19:00, etc.
UPDATE film_projection
SET 
    start_time = (INTERVAL '14 hours' + INTERVAL '1 hour' * (id % 6))::TIME,
    end_time = (INTERVAL '16 hours' + INTERVAL '1 hour' * (id % 6))::TIME
WHERE start_time IS NULL OR end_time IS NULL;

-- Step 3: Remove old seance relationship from film_projection
ALTER TABLE film_projection DROP COLUMN IF EXISTS seance_id;

-- Step 4: Drop foreign key constraint from seance table if exists
ALTER TABLE seance DROP CONSTRAINT IF EXISTS fk_seance_film_projection;

-- Step 5: Drop seance table (cascade to remove dependent objects)
DROP TABLE IF EXISTS seance CASCADE;

-- Step 6: Drop seance sequence
DROP SEQUENCE IF EXISTS seance_id_seq;

-- Step 6b: Remove duplicate projections (keep only the one with lowest ID)
DELETE FROM film_projection fp1
USING film_projection fp2
WHERE fp1.id > fp2.id
  AND fp1.film_id = fp2.film_id
  AND fp1.salle_id = fp2.salle_id
  AND fp1.date_projection = fp2.date_projection
  AND fp1.start_time = fp2.start_time
  AND fp1.end_time = fp2.end_time;

-- Step 7: Add unique constraint to prevent duplicate projections
-- A projection is unique by: film + room + date + time
ALTER TABLE film_projection 
ADD CONSTRAINT uk_film_projection_unique 
UNIQUE (film_id, salle_id, date_projection, start_time, end_time);

-- Step 8: Add NOT NULL constraints on critical fields
ALTER TABLE film_projection ALTER COLUMN start_time SET NOT NULL;
ALTER TABLE film_projection ALTER COLUMN end_time SET NOT NULL;

-- Step 9: Add check constraint to ensure end_time > start_time
ALTER TABLE film_projection 
ADD CONSTRAINT chk_film_projection_time_order 
CHECK (end_time > start_time);

-- Step 10: Add comments for documentation
COMMENT ON TABLE film_projection IS 'Film projection schedule - includes time slots from merged seance table. Price is managed at Ticket level with PriceType discounts';
COMMENT ON COLUMN film_projection.id IS 'Primary key - unique film projection identifier';
COMMENT ON COLUMN film_projection.date_projection IS 'Date of the film projection';
COMMENT ON COLUMN film_projection.start_time IS 'Start time of the projection (merged from seance)';
COMMENT ON COLUMN film_projection.end_time IS 'End time of the projection (merged from seance)';
COMMENT ON COLUMN film_projection.max_projections IS 'Maximum number of projections allowed per day for this film (0 = unlimited)';
COMMENT ON COLUMN film_projection.salle_id IS 'Foreign key to salle (room)';
COMMENT ON COLUMN film_projection.film_id IS 'Foreign key to film';

