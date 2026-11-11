package fr.uga.gestioncinema.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

class TmdbFilmParserTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void keepsAtMostFiftyIds() {
        List<Long> ids = LongStream.rangeClosed(1, 60).boxed().toList();

        assertThat(TmdbFilmParser.cap(ids)).hasSize(50).first().isEqualTo(1L);
        assertThat(TmdbFilmParser.cap(ids)).last().isEqualTo(50L);
    }

    @Test
    void prefersTheFrenchTrailerAndKeepsTheKnownCategory() throws Exception {
        String json = """
                {
                  "id": 438631,
                  "title": "Dune",
                  "overview": "Paul Atreides",
                  "runtime": 155,
                  "release_date": "2021-09-15",
                  "poster_path": "/dune.jpg",
                  "genres": [{"name": "Science-Fiction"}, {"name": "Drame"}],
                  "credits": {"crew": [
                    {"job": "Writer", "name": "Quelqu'un"},
                    {"job": "Director", "name": "Denis Villeneuve"}
                  ]},
                  "videos": {"results": [
                    {"site": "YouTube", "type": "Trailer", "key": "english", "official": true, "iso_639_1": "en"},
                    {"site": "YouTube", "type": "Trailer", "key": "french", "official": false, "iso_639_1": "fr"},
                    {"site": "YouTube", "type": "Teaser", "key": "teaser", "official": true, "iso_639_1": "fr"}
                  ]}
                }
                """;

        TmdbFilm film = TmdbFilmParser.film(mapper.readTree(json)).orElseThrow();

        assertThat(film.tmdbId()).isEqualTo(438631);
        assertThat(film.title()).isEqualTo("Dune");
        assertThat(film.duration()).isEqualTo(155d);
        assertThat(film.director()).isEqualTo("Denis Villeneuve");
        assertThat(film.releaseDate()).isEqualTo(LocalDate.of(2021, 9, 15));
        assertThat(film.trailerYoutubeKey()).isEqualTo("french");
        assertThat(film.categoryName()).isEqualTo("Drame");
        assertThat(film.posterPath()).isEqualTo("/dune.jpg");
    }

    @Test
    void ignoresAPosterPathThatIsNotAFileName() throws Exception {
        String json = """
                {"id": 1, "title": "Dune", "poster_path": "https://evil.example/a.jpg"}
                """;

        assertThat(TmdbFilmParser.film(mapper.readTree(json)).orElseThrow().posterPath()).isNull();
    }

    @Test
    void skipsAFilmWithoutATitle() throws Exception {
        assertThat(TmdbFilmParser.film(mapper.readTree("{\"id\": 1, \"title\": \"  \"}"))).isEmpty();
    }
}
