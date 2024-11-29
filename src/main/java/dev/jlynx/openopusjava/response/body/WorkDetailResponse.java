package dev.jlynx.openopusjava.response.body;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.jlynx.openopusjava.response.subtype.ComposerOverview;
import dev.jlynx.openopusjava.response.subtype.OpenOpusResponse;
import dev.jlynx.openopusjava.response.subtype.SimilarWork;
import dev.jlynx.openopusjava.response.subtype.WorkDetail;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * This object maps to the "Detail work by ID" response from the Open Opus API.
 */
public class WorkDetailResponse extends OpenOpusResponse {

    private final ComposerOverview composer;
    private final WorkDetail work;
    private final List<SimilarWork> similarlyTitled;

    @JsonCreator
    public WorkDetailResponse(
            @JsonProperty("status")
            OpenOpusResponseStatus status,
            @JsonProperty("request")
            OpenOpusRequestMetadata request,
            @JsonProperty("composer")
            ComposerOverview composer,
            @JsonProperty("work")
            WorkDetail work,
            @JsonProperty("similarlytitled")
            List<SimilarWork> similarlyTitled) {
        super(status, request);
        this.composer = composer;
        this.work = work;
        this.similarlyTitled = similarlyTitled;
    }

    public ComposerOverview getComposer() {
        return composer;
    }

    public WorkDetail getWork() {
        return work;
    }

    public Optional<List<SimilarWork>> getSimilarlyTitled() {
        return Optional.ofNullable(similarlyTitled);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        WorkDetailResponse that = (WorkDetailResponse) o;
        return Objects.equals(composer, that.composer) &&
                Objects.equals(work, that.work) &&
                Objects.equals(similarlyTitled, that.similarlyTitled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), composer, work, similarlyTitled);
    }

    @Override
    public String toString() {
        return "WorksList{" + "\n" +
                "status=" + status + ",\n" +
                "request=" + request + ",\n" +
                "composer=" + composer + ",\n" +
                "work=" + work + ",\n" +
                "similarlyTitled=" + similarlyTitled + "\n" +
                '}';
    }
}
