-- Migration V017: Insert test seats for each room
-- Each room gets 50 seats (5 rows x 10 seats per row)

-- Insert seats for Room 1
INSERT INTO seat (number, room_id)
SELECT 
    generate_series AS number,
    1 AS room_id
FROM generate_series(1, 50);

-- Insert seats for Room 2
INSERT INTO seat (number, room_id)
SELECT 
    generate_series AS number,
    2 AS room_id
FROM generate_series(1, 50);

-- Insert seats for Room 3
INSERT INTO seat (number, room_id)
SELECT 
    generate_series AS number,
    3 AS room_id
FROM generate_series(1, 50);

-- Insert seats for Room 4 (if it exists)
INSERT INTO seat (number, room_id)
SELECT 
    generate_series AS number,
    4 AS room_id
FROM generate_series(1, 50)
WHERE EXISTS (SELECT 1 FROM room WHERE id = 4);

DO $$ 
DECLARE
    row_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO row_count FROM seat;
    RAISE NOTICE '✓ Migration V017 completed: Inserted test seats (% total seats)', row_count;
END $$;


