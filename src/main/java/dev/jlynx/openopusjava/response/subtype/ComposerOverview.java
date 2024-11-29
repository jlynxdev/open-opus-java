package dev.jlynx.openopusjava.response.subtype;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ComposerOverview(int id, String lastName, String fullName, Epoch epoch) {

    @JsonCreator
    public ComposerOverview(
            @JsonProperty("id")
            int id,
            @JsonProperty("name")
            String lastName,
            @JsonProperty("complete_name")
            String fullName,
            @JsonProperty("epoch")
            Epoch epoch
    ) {
        this.id = id;
        this.lastName = lastName;
        this.fullName = fullName;
        this.epoch = epoch;
    }
}
