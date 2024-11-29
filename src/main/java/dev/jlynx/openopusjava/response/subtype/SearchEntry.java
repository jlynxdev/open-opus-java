package dev.jlynx.openopusjava.response.subtype;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record SearchEntry(Composer composer, Work work) {

    @JsonCreator
    public SearchEntry(
            @JsonProperty("composer")
            Composer composer,
            @JsonProperty("work")
            Work work) {
        this.composer = composer;
        this.work = work;
    }
}
