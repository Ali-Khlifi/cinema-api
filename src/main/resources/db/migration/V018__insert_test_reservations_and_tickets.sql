-- Migration V018: Insert test reservations and tickets
-- Create some reservations with tickets to test seat availability

-- First, insert a test user if it doesn't exist
INSERT INTO "user" (keycloak_id, email, first_name, last_name)
VALUES ('test-keycloak-id-123', 'test@cinema.com', 'Test', 'User')
ON CONFLICT (keycloak_id) DO NOTHING;

-- Get the test user ID or create reservations in a transaction
DO $$ 
DECLARE
    test_user_id BIGINT;
BEGIN
    SELECT id INTO test_user_id FROM "user" WHERE keycloak_id = 'test-keycloak-id-123';
    
    -- Insert test reservations
    INSERT INTO reservation (
        reference_number, 
        user_id, 
        film_projection_id, 
        status, 
        subtotal, 
        total_amount,
        terms_accepted
    ) VALUES
    ('RES-TEST-001', test_user_id, 1, 'CONFIRMED', 30.00, 30.00, true),
    ('RES-TEST-002', test_user_id, 2, 'CONFIRMED', 20.00, 20.00, true),
    ('RES-TEST-003', test_user_id, 1, 'CONFIRMED', 10.00, 10.00, true)
    ON CONFLICT (reference_number) DO NOTHING;
END $$;

-- Insert test tickets for the reservations
-- We need to find the room_id for each projection first
DO $$ 
DECLARE
    res1_id BIGINT;
    res2_id BIGINT;
    res3_id BIGINT;
    room1_id BIGINT;
    room2_id BIGINT;
    seat_id_var BIGINT;
BEGIN
    -- Get reservation IDs
    SELECT id INTO res1_id FROM reservation WHERE reference_number = 'RES-TEST-001';
    SELECT id INTO res2_id FROM reservation WHERE reference_number = 'RES-TEST-002';
    SELECT id INTO res3_id FROM reservation WHERE reference_number = 'RES-TEST-003';
    
    -- Get room IDs from film projections
    SELECT room_id INTO room1_id FROM film_projection WHERE id = 1;
    SELECT room_id INTO room2_id FROM film_projection WHERE id = 2;
    
    -- Insert tickets for reservation 1 (seats 1, 2, 3 in room1)
    FOR i IN 1..3 LOOP
        SELECT id INTO seat_id_var FROM seat WHERE room_id = room1_id AND number = i LIMIT 1;
        IF seat_id_var IS NOT NULL THEN
            INSERT INTO ticket (reservation_id, seat_id, price_type_id, base_price, final_price, is_used)
            VALUES (res1_id, seat_id_var, 1, 10.00, 10.00, false)
            ON CONFLICT DO NOTHING;
        END IF;
    END LOOP;
    
    -- Insert tickets for reservation 2 (seats 5, 6 in room2)
    FOR i IN 5..6 LOOP
        SELECT id INTO seat_id_var FROM seat WHERE room_id = room2_id AND number = i LIMIT 1;
        IF seat_id_var IS NOT NULL THEN
            INSERT INTO ticket (reservation_id, seat_id, price_type_id, base_price, final_price, is_used)
            VALUES (res2_id, seat_id_var, 1, 10.00, 10.00, false)
            ON CONFLICT DO NOTHING;
        END IF;
    END LOOP;
    
    -- Insert ticket for reservation 3 (seat 10 in room1)
    SELECT id INTO seat_id_var FROM seat WHERE room_id = room1_id AND number = 10 LIMIT 1;
    IF seat_id_var IS NOT NULL THEN
        INSERT INTO ticket (reservation_id, seat_id, price_type_id, base_price, final_price, is_used)
        VALUES (res3_id, seat_id_var, 2, 10.00, 7.50, false)
        ON CONFLICT DO NOTHING;
    END IF;
END $$;

DO $$ 
DECLARE
    reservation_count INTEGER;
    ticket_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO reservation_count FROM reservation WHERE reference_number LIKE 'RES-TEST-%';
    SELECT COUNT(*) INTO ticket_count FROM ticket;
    RAISE NOTICE '✓ Migration V018 completed: Created % test reservations and % tickets', reservation_count, ticket_count;
END $$;

