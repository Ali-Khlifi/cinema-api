-- =====================================================
-- Add has_coffee flag to cinema
-- =====================================================
-- Description: Indicates whether a cinema has a coffee shop
-- Author: Cinema Management System
-- Date: 2026-07-26

ALTER TABLE cinema
    ADD COLUMN IF NOT EXISTS has_coffee BOOLEAN NOT NULL DEFAULT FALSE;

COMMENT ON COLUMN cinema.has_coffee IS 'Indicates if the cinema has a coffee shop';

-- Seed existing cinemas (mix of true/false for realistic data)
UPDATE cinema SET has_coffee = TRUE
WHERE name IN (
    'UGC Cité Internationale',
    'UGC Cité Confluence',
    'UGC Cité Part-dieu',
    'Cinema Parisien'
);

UPDATE cinema SET has_coffee = FALSE
WHERE name IN (
    'Cinema Lyonnais',
    'Cinema Marseillais',
    'Cinema Toulousain',
    'UGC Astoria'
);
