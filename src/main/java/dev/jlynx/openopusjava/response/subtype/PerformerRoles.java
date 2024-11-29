package dev.jlynx.openopusjava.response.subtype;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record PerformerRoles(List<PerformerRole> readable, Map<String, String> digest) {

    @JsonCreator
    public PerformerRoles(
            @JsonProperty("readable")
            List<PerformerRole> readable,
            @JsonProperty("digest")
            Map<String, String> digest
    ) {
        this.readable = readable;
        this.digest = digest;
    }
}
