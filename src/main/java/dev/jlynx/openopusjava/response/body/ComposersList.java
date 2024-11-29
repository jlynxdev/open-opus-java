package dev.jlynx.openopusjava.response.body;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.jlynx.openopusjava.response.subtype.Composer;
import dev.jlynx.openopusjava.response.subtype.OpenOpusResponse;

import java.util.List;
import java.util.Objects;

/**
 * This object maps to all the "List composers" or "Search composers" responses of the Open Opus API.
 */
public class ComposersList extends OpenOpusResponse {

    private final List<Composer> composers;

    @JsonCreator
    public ComposersList(
            @JsonProperty("status")
            OpenOpusResponseStatus status,
            @JsonProperty("request")
            OpenOpusRequestMetadata request,
            @JsonProperty("composers")
            List<Composer> composers
    ) {
        super(status, request);
        this.composers = composers;
    }

    public List<Composer> getComposers() {
        return composers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ComposersList that = (ComposersList) o;
        return Objects.equals(composers, that.composers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), composers);
    }

    @Override
    public String toString() {
        return "ComposersList{" + "\n" +
                "status=" + status + ",\n" +
                "request=" + request + ",\n" +
                "composers=" + composers + "\n" +
                '}';
    }
}
