package dev.jlynx.openopusjava.response.body;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.jlynx.openopusjava.response.subtype.ListWorksByIdAbstract;
import dev.jlynx.openopusjava.response.subtype.OpenOpusResponse;
import dev.jlynx.openopusjava.response.subtype.WorkSummary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This object maps to the "List works by ID" response from the Open Opus API.
 */
public class ListWorksByIdResponse extends OpenOpusResponse {

    private final List<WorkSummary> works;
    private final ListWorksByIdAbstract worksAbstract;

    @JsonCreator
    public ListWorksByIdResponse(
            @JsonProperty("status")
            OpenOpusResponseStatus status,
            @JsonProperty("request")
            OpenOpusRequestMetadata request,
            @JsonProperty("works")
            Map<String, WorkSummary> works,
            @JsonProperty("abstract")
            ListWorksByIdAbstract worksAbstract
    ) {
        super(status, request);
        this.works = new ArrayList<>(works.values());
        this.worksAbstract = worksAbstract;
    }

    public List<WorkSummary> getWorks() {
        return works;
    }

    public ListWorksByIdAbstract getWorksAbstract() {
        return worksAbstract;
    }

    @Override
    public String toString() {
        return "ListWorksByIdResponse{" + "\n" +
                "status=" + status + ",\n" +
                "request=" + request + ",\n" +
                "worksAbstract=" + worksAbstract + ",\n" +
                "works=" + works + "\n" +
                '}';
    }
}
