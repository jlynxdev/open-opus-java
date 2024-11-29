package dev.jlynx.openopusjava.response.subtype;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record PerformerRole(String name, String role) {

    @JsonCreator
    public PerformerRole(
            @JsonProperty("name")
            String name,
            @JsonProperty("role")
            String role
    ) {
        this.name = name;
        this.role = role;
    }
}
