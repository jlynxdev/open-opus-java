package dev.jlynx.openopusjava.response.subtype;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RandomWork(int id, String title, Genre genre, ComposerOverview composer) {

    @JsonCreator
    public RandomWork(
            @JsonProperty("id")
            int id,
            @JsonProperty("title")
            String title,
            @JsonProperty("genre")
            Genre genre,
            @JsonProperty("composer")
            ComposerOverview composer
    ) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.composer = composer;
    }
}
