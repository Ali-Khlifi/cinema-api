-- dump database

-- Table: category
CREATE TABLE public.category (
                                 id bigint NOT NULL PRIMARY KEY,
                                 name character varying(75)
);

CREATE SEQUENCE public.category_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Table: ville
CREATE TABLE public.ville (
                              id bigint NOT NULL PRIMARY KEY,
                              name character varying(255),
                              longitude double precision,
                              latitude double precision,
                              altitude double precision
);

CREATE SEQUENCE public.ville_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Table: cinema
CREATE TABLE public.cinema (
                               id bigint NOT NULL PRIMARY KEY,
                               name character varying(255),
                               adresse character varying(255),
                               nombre_salles int,
                               ville_id bigint
);

CREATE SEQUENCE public.cinema_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Table: seance
CREATE TABLE public.seance (
                               id bigint NOT NULL PRIMARY KEY,
                               heure_debut timestamp without time zone,
                               heure_fin timestamp without time zone,
                               prix double precision,
                               film_projection_id bigint
);

CREATE SEQUENCE public.seance_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Table: film
CREATE TABLE public.film (
                             id bigint NOT NULL PRIMARY KEY,
                             titre character varying(255),
                             duree double precision,
                             realisateur character varying(255),
                             description text,
                             photo text,
                             date_sortie date,
                             category_id bigint
);

CREATE SEQUENCE public.film_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Table: salle
CREATE TABLE public.salle (
                              id bigint NOT NULL PRIMARY KEY,
                              name character varying(255),
                              nombre_places int,
                              cinema_id bigint
);

CREATE SEQUENCE public.salle_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Table: filmProjection
CREATE TABLE public.film_projection (
                                       id bigint NOT NULL PRIMARY KEY,
                                       date_projection date,
                                       salle_id bigint REFERENCES public.salle(id),
                                       film_id bigint REFERENCES public.film(id)
);

CREATE SEQUENCE public.film_projection_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Table: place
CREATE TABLE public.place (
                              id bigint NOT NULL PRIMARY KEY,
                              numero int,
                              salle_id bigint
);

CREATE SEQUENCE public.place_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Table: ticket
CREATE TABLE public.ticket (
                               id bigint NOT NULL PRIMARY KEY,
                               nom_client character varying(75),
                               code_payement int,
                               reservee boolean,
                               place_id bigint,
                               film_projection_id bigint
);

CREATE SEQUENCE public.ticket_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--id generation sequence

ALTER TABLE ONLY public.category ALTER COLUMN id SET DEFAULT nextval('public.category_id_seq'::regclass);
ALTER TABLE ONLY public.ville ALTER COLUMN id SET DEFAULT nextval('public.ville_id_seq'::regclass);
ALTER TABLE ONLY public.cinema ALTER COLUMN id SET DEFAULT nextval('public.cinema_id_seq'::regclass);
ALTER TABLE ONLY public.salle ALTER COLUMN id SET DEFAULT nextval('public.salle_id_seq'::regclass);
ALTER TABLE ONLY public.place ALTER COLUMN id SET DEFAULT nextval('public.place_id_seq'::regclass);
ALTER TABLE ONLY public.film_projection ALTER COLUMN id SET DEFAULT nextval('public.film_projection_id_seq'::regclass);
ALTER TABLE ONLY public.film ALTER COLUMN id SET DEFAULT nextval('public.film_id_seq'::regclass);
ALTER TABLE ONLY public.seance ALTER COLUMN id SET DEFAULT nextval('public.seance_id_seq'::regclass);
ALTER TABLE ONLY public.ticket ALTER COLUMN id SET DEFAULT nextval('public.ticket_id_seq'::regclass);


--- foreign keys ---

-- Pour la table Cinema et Ville
ALTER TABLE public.cinema ADD CONSTRAINT fk_cinema_ville FOREIGN KEY (ville_id) REFERENCES public.ville(id);

-- Pour la table Salle et Cinema
ALTER TABLE public.salle ADD CONSTRAINT fk_salle_cinema FOREIGN KEY (cinema_id) REFERENCES public.cinema(id);

-- Pour la table Film et Category
ALTER TABLE public.film ADD CONSTRAINT fk_film_category FOREIGN KEY (category_id) REFERENCES public.category(id);

-- Pour la table FilmProjection et Salle
ALTER TABLE public.film_projection ADD CONSTRAINT fk_film_projection_salle FOREIGN KEY (salle_id) REFERENCES public.salle(id);

-- Pour la table FilmProjection et Film
ALTER TABLE public.film_projection ADD CONSTRAINT fk_film_projection_film FOREIGN KEY (film_id) REFERENCES public.film(id);

-- Pour la table FilmProjection et Seance
--ALTER TABLE public.film_projection ADD CONSTRAINT fk_film_projection_seance FOREIGN KEY (seance_id) REFERENCES public.seance(id);

-- Pour la table Place et Salle
ALTER TABLE public.place ADD CONSTRAINT fk_place_salle FOREIGN KEY (salle_id) REFERENCES public.salle(id);

-- Pour la table Ticket et Place
ALTER TABLE public.ticket ADD CONSTRAINT fk_ticket_place FOREIGN KEY (place_id) REFERENCES public.place(id);

-- Pour la table Ticket et FilmProjection
ALTER TABLE public.ticket ADD CONSTRAINT fk_ticket_film_projection FOREIGN KEY (film_projection_id) REFERENCES public.film_projection(id);

-- Pour la table Seance et FilmProjection
ALTER TABLE public.seance ADD CONSTRAINT fk_seance_film_projection FOREIGN KEY (film_projection_id) REFERENCES public.film_projection(id);

