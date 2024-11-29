package dev.jlynx.openopusjava.response.subtype;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public record WorkSummary(int id, String title, String subtitle, Genre genre, boolean popular, boolean recommended, Composer composer) {

    @JsonCreator
    public WorkSummary(
            @JsonProperty("id")
            int id,
            @JsonProperty("title")
            String title,
            @JsonProperty("subtitle")
            String subtitle,
            @JsonProperty("genre")
            Genre genre,
            @JsonProperty("popular")
            String popular,
            @JsonProperty("recommended")
            String recommended,
            @JsonProperty("composer")
            Composer composer
    ) {
        this(
                id,
                title,
                subtitle,
                genre,
                Objects.equals(popular, "1"),
                Objects.equals(recommended, "1"),
                composer
        );
    }
}
