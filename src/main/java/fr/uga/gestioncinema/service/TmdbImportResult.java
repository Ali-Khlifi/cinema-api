package fr.uga.gestioncinema.service;

/**
 * How many films the last TMDB import created or updated.
 */
public record TmdbImportResult(int created, int updated) {
}
