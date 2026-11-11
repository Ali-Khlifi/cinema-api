-- TMDB identity and trailer. Nullable: films created by hand stay valid.
-- poster_key is left untouched: an import must not replace an uploaded poster.

ALTER TABLE film
    ADD COLUMN IF NOT EXISTS tmdb_id INTEGER;

ALTER TABLE film
    ADD COLUMN IF NOT EXISTS trailer_youtube_key VARCHAR(32);

ALTER TABLE film
    ADD CONSTRAINT film_tmdb_id_key UNIQUE (tmdb_id);

COMMENT ON COLUMN film.tmdb_id IS 'TMDB movie id. Null when the film was not imported. Unique when set.';
COMMENT ON COLUMN film.trailer_youtube_key IS 'YouTube video id of the trailer. Null when TMDB has no trailer.';
