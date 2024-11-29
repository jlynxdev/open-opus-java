package dev.jlynx.openopusjava.response.body;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.jlynx.openopusjava.response.subtype.Composer;
import dev.jlynx.openopusjava.response.subtype.OpenOpusResponse;
import dev.jlynx.openopusjava.response.subtype.Work;

import java.util.List;
import java.util.Objects;

/**
 * This object maps to all the "List works" or "Search works" responses of the Open Opus API.
 */
public class WorksList extends OpenOpusResponse {

    private final Composer composer;
    private final List<Work> works;

    @JsonCreator
    public WorksList(
            @JsonProperty("status")
            OpenOpusResponseStatus status,
            @JsonProperty("request")
            OpenOpusRequestMetadata request,
            @JsonProperty("composer")
            Composer composer,
            @JsonProperty("works")
            List<Work> works
    ) {
        super(status, request);
        this.composer = composer;
        this.works = works;
    }

    public Composer getComposer() {
        return composer;
    }

    public List<Work> getWorks() {
        return works;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        WorksList worksList = (WorksList) o;
        return Objects.equals(composer, worksList.composer) &&
                Objects.equals(works, worksList.works);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), composer, works);
    }

    @Override
    public String toString() {
        return "WorksList{" + "\n" +
                "status=" + status + ",\n" +
                "request=" + request + ",\n" +
                "composer=" + composer + ",\n" +
                "works=" + works + "\n" +
                '}';
    }
}
