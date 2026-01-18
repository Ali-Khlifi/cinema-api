-- Migration V016: Fix NULL release_date in film table
-- Add default release dates for films that have NULL values

UPDATE film 
SET release_date = '2023-01-01'::date 
WHERE release_date IS NULL;

-- Make sure the column is not nullable
ALTER TABLE film ALTER COLUMN release_date SET NOT NULL;

DO $$ 
DECLARE
    row_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO row_count FROM film WHERE release_date IS NOT NULL;
    RAISE NOTICE '✓ Migration V016 completed: Fixed release_date for all films (% records)', row_count;
END $$;


