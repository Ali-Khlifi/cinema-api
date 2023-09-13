
-- Insert data into Category
INSERT INTO public.category (name)
VALUES
    ('Action'),
    ('Comédie'),
    ('Drame'),
    ('Horreur'),
    ('Animation'),
    ('Documentaire');

-- Insert data into Ville
INSERT INTO public.ville (name, longitude, latitude, altitude)
VALUES
    ('Paris', 48.8566, 2.3522, 35),
    ('Lyon', 45.7600, 4.8400, 173),
    ('Marseille', 43.2965, 5.3698, 28),
    ('Toulouse', 43.6043, 1.4437, 146);

-- Insert data into cinema
INSERT INTO public.cinema (name, longitude, latitude, altitude, nombre_salles, ville_id)
VALUES
    ('Cinema Parisien', 48.8566, 2.3522, 35, 5, 1),
    ('Cinema Lyonnais', 45.7600, 4.8400, 173, 3, 2),
    ('Cinema Marseillais', 43.2965, 5.3698, 28, 4, 3),
    ('Cinema Toulousain', 43.6043, 1.4437, 146, 6, 4);

-- Insert data into Salle
INSERT INTO public.salle (name, nombre_places, cinema_id)
VALUES
    ('Salle A', 120, 1),
    ('Salle B', 100, 2),
    ('Salle C', 90, 1),
    ('Salle D', 80, 2),
    ('Salle E', 75, 3),
    ('Salle F', 70, 4);

-- Insert data into Seance
INSERT INTO public.seance (heure_debut)
VALUES
    ('2023-05-01 14:00:00'),
    ('2023-05-01 16:30:00'),
    ('2023-05-01 19:00:00'),
    ('2023-05-01 21:00:00');

-- Insert data into Film
INSERT INTO public.film (titre, duree, realisateur, description, photo, date_sortie, category_id)
VALUES
    ('Film d''Action', 120, 'John Doe', 'Un film d''action captivant', 'Punisher.jpg', '2023-05-01', 1),
    ('Film Comique', 90, 'Jane Doe', 'Un film comique pour toute la famille', 'Friends.jpg', '2023-05-01', 2),
    ('Film d''Horreur', 110, 'James Smith', 'Un film qui vous fera frissonner', 'needForSpeed.jpg', '2023-06-01', 4),
    ('Film Documentaire', 130, 'Anna Johnson', 'Un regard sur la nature', 'documentary.jpg', '2023-07-01', 6);

-- Insert data into FilmProjection
INSERT INTO public.film_projection (date_projection, prix, salle_id, film_id, seance_id)
VALUES
    ('2023-05-01', 12.0, 1, 1, 1),
    ('2023-05-01', 10.0, 2, 2, 2),
    ('2023-06-01', 14.0, 3, 3, 3),
    ('2023-07-01', 9.0, 4, 4, 4);

-- Insert data into Place
INSERT INTO public.place (numero, longitude, latitude, altitude, salle_id)
VALUES
    (1, 0, 0, 0, 1),
    (2, 0, 0, 0, 1),
    (3, 0, 0, 0, 2),
    (4, 0, 0, 0, 2),
    (5, 0, 0, 0, 3),
    (6, 0, 0, 0, 3);

-- Insert data into Ticket
INSERT INTO public.ticket (nom_client, prix, code_payement, reservee, place_id, film_projection_id)
VALUES
    ('Client A', 12.0, 12345, true, 1, 1),
    ('Client B', 10.0, 67890, true, 2, 2),
    ('Client C', 14.0, 11121, true, 3, 3),
    ('Client D', 9.0, 13141, true, 4, 4);

