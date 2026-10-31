²-- S3 object key for the film poster. Nullable: films without a poster stay valid.
-- The legacy bytea column "media" remains until posters are served from S3.

ALTER TABLE film
    ADD COLUMN IF NOT EXISTS poster_key VARCHAR(512);

COMMENT ON COLUMN film.poster_key IS 'Private S3 object key of the poster image. Null when no poster is stored.';
