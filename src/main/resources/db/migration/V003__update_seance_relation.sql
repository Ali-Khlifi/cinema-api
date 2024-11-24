ALTER TABLE public.seance
ADD COLUMN IF NOT EXISTS film_projection_id BIGINT;

-- Créer la relation entre `Seance` et `FilmProjection`
ALTER TABLE public.seance
ADD CONSTRAINT fk_seance_film_projection
FOREIGN KEY (film_projection_id)
REFERENCES public.film_projection
ON DELETE CASCADE;

-- Supprimer la contrainte de clé étrangère si elle existe
ALTER TABLE public.film_projection
DROP CONSTRAINT IF EXISTS fk_film_projection_seance;

-- Supprimer la colonne `seance_id` de la table `film_projection`
ALTER TABLE public.film_projection
DROP COLUMN IF EXISTS seance_id;
