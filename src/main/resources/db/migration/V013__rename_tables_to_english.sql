-- =====================================================
-- Migration V013: Rename all tables and columns to English
-- Description: Uniformize database schema to English naming convention
-- Author: Cinema API Team
-- Date: 2025-12-21
-- Phase: 4.5
-- =====================================================

-- ============================================
-- STEP 1: Rename Tables
-- ============================================

-- Rename ville → city
ALTER TABLE ville RENAME TO city;

-- Rename salle → room
ALTER TABLE salle RENAME TO room;

-- Rename place → seat
ALTER TABLE place RENAME TO seat;

-- ============================================
-- STEP 2: Rename Sequences
-- ============================================

ALTER SEQUENCE ville_id_seq RENAME TO city_id_seq;
ALTER SEQUENCE salle_id_seq RENAME TO room_id_seq;
ALTER SEQUENCE place_id_seq RENAME TO seat_id_seq;

-- ============================================
-- STEP 3: Rename Columns in CITY table
-- ============================================

-- No column renames needed for city (name is already in English)

-- ============================================
-- STEP 4: Rename Columns in CINEMA table
-- ============================================

ALTER TABLE cinema RENAME COLUMN ville_id TO city_id;
ALTER TABLE cinema RENAME COLUMN nombre_salles TO number_of_rooms;

-- ============================================
-- STEP 5: Rename Columns in ROOM table
-- ============================================

ALTER TABLE room RENAME COLUMN nombre_places TO number_of_seats;

-- ============================================
-- STEP 6: Rename Columns in SEAT table
-- ============================================

ALTER TABLE seat RENAME COLUMN numero TO number;
ALTER TABLE seat RENAME COLUMN salle_id TO room_id;

-- ============================================
-- STEP 7: Rename Columns in FILM table
-- ============================================

ALTER TABLE film RENAME COLUMN titre TO title;
ALTER TABLE film RENAME COLUMN duree TO duration;
ALTER TABLE film RENAME COLUMN realisateur TO director;
ALTER TABLE film RENAME COLUMN date_sortie TO release_date;

-- ============================================
-- STEP 8: Rename Columns in FILM_PROJECTION table
-- ============================================

ALTER TABLE film_projection RENAME COLUMN salle_id TO room_id;
ALTER TABLE film_projection RENAME COLUMN date_projection TO projection_date;

-- ============================================
-- STEP 9: Rename Columns in TICKET table
-- ============================================

ALTER TABLE ticket RENAME COLUMN place_id TO seat_id;

-- ============================================
-- STEP 10: Update Foreign Key Constraints
-- ============================================

-- Drop old FK constraints
ALTER TABLE cinema DROP CONSTRAINT IF EXISTS fk_cinema_ville;
ALTER TABLE room DROP CONSTRAINT IF EXISTS fk_salle_cinema;
ALTER TABLE seat DROP CONSTRAINT IF EXISTS fk_place_salle;
ALTER TABLE film_projection DROP CONSTRAINT IF EXISTS fk_film_projection_salle;
ALTER TABLE ticket DROP CONSTRAINT IF EXISTS fk_ticket_place;

-- Recreate FK constraints with updated names
ALTER TABLE cinema 
  ADD CONSTRAINT fk_cinema_city 
  FOREIGN KEY (city_id) REFERENCES city(id);

ALTER TABLE room 
  ADD CONSTRAINT fk_room_cinema 
  FOREIGN KEY (cinema_id) REFERENCES cinema(id);

ALTER TABLE seat 
  ADD CONSTRAINT fk_seat_room 
  FOREIGN KEY (room_id) REFERENCES room(id);

ALTER TABLE film_projection 
  ADD CONSTRAINT fk_film_projection_room 
  FOREIGN KEY (room_id) REFERENCES room(id);

ALTER TABLE ticket 
  ADD CONSTRAINT fk_ticket_seat 
  FOREIGN KEY (seat_id) REFERENCES seat(id);

-- ============================================
-- STEP 11: Update Comments for Documentation
-- ============================================

COMMENT ON TABLE city IS 'City table - stores city information for cinema locations';
COMMENT ON TABLE room IS 'Room/Hall table - screening rooms within a cinema';
COMMENT ON TABLE seat IS 'Seat table - individual seats within a room';

COMMENT ON COLUMN cinema.city_id IS 'Foreign key to city table';
COMMENT ON COLUMN cinema.number_of_rooms IS 'Number of screening rooms in this cinema';

COMMENT ON COLUMN room.number_of_seats IS 'Total number of seats in this room';

COMMENT ON COLUMN seat.number IS 'Seat number within the room';
COMMENT ON COLUMN seat.room_id IS 'Foreign key to room table';

COMMENT ON COLUMN film.title IS 'Film title';
COMMENT ON COLUMN film.duration IS 'Film duration in minutes';
COMMENT ON COLUMN film.director IS 'Film director name';
COMMENT ON COLUMN film.release_date IS 'Film release date';

COMMENT ON COLUMN film_projection.room_id IS 'Foreign key to room table';
COMMENT ON COLUMN film_projection.projection_date IS 'Date of the film projection';

COMMENT ON COLUMN ticket.seat_id IS 'Foreign key to seat table';

-- ============================================
-- Migration completed successfully
-- All tables and columns now use English naming
-- ============================================









