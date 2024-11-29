package dev.jlynx.openopusjava.response.body;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.jlynx.openopusjava.response.subtype.OpenOpusResponse;
import dev.jlynx.openopusjava.response.subtype.PerformerRoles;

import java.util.Objects;

public class PerformerRolesResponse extends OpenOpusResponse {

    private final PerformerRoles performers;

    @JsonCreator
    public PerformerRolesResponse(
            @JsonProperty("status")
            OpenOpusResponseStatus status,
            @JsonProperty("request")
            OpenOpusRequestMetadata request,
            @JsonProperty("performers")
            PerformerRoles performers) {
        super(status, request);
        this.performers = performers;
    }

    public PerformerRoles getPerformers() {
        return performers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PerformerRolesResponse that = (PerformerRolesResponse) o;
        return Objects.equals(performers, that.performers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), performers);
    }

    @Override
    public String toString() {
        return "PerformerRolesResponse{" + "\n" +
                "status=" + status + ",\n" +
                "request=" + request + ",\n" +
                "performers=" + performers + "\n" +
                '}';
    }
}
