package dev.jlynx.openopusjava.response.body;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.jlynx.openopusjava.response.subtype.Composer;
import dev.jlynx.openopusjava.response.subtype.Genre;
import dev.jlynx.openopusjava.response.subtype.OpenOpusResponse;

import java.util.List;
import java.util.Objects;

/**
 * This object maps to all the "List genres" responses of the Open Opus API.
 */
public class GenresList extends OpenOpusResponse {

    private final Composer composer;
    private final List<Genre> genres;

    @JsonCreator
    public GenresList(
            @JsonProperty("status")
            OpenOpusResponseStatus status,
            @JsonProperty("request")
            OpenOpusRequestMetadata request,
            @JsonProperty("composer")
            Composer composer,
            @JsonProperty("genres")
            List<Genre> genres
    ) {
        super(status, request);
        this.composer = composer;
        this.genres = genres;
    }

    public Composer getComposer() {
        return composer;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GenresList that = (GenresList) o;
        return Objects.equals(composer, that.composer) &&
                Objects.equals(genres, that.genres);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), composer, genres);
    }

    @Override
    public String toString() {
        return "GenresList{" + "\n" +
                "status=" + status + ",\n" +
                "request=" + request + ",\n" +
                "composer=" + composer + ",\n" +
                "genres=" + genres + "\n" +
                '}';
    }
}
