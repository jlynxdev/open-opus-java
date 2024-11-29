package dev.jlynx.openopusjava.response.subtype;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record SimilarWork(int id, String title, String searchTerm, int similarity) {

    @JsonCreator
    public SimilarWork(
            @JsonProperty("id")
            int id,
            @JsonProperty("title")
            String title,
            @JsonProperty("searchterm")
            String searchTerm,
            @JsonProperty("similarity")
            int similarity
    ) {
        this.id = id;
        this.title = title;
        this.searchTerm = searchTerm;
        this.similarity = similarity;
    }
}
