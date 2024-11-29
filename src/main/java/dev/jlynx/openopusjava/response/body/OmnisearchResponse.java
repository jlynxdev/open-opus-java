package dev.jlynx.openopusjava.response.body;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.jlynx.openopusjava.response.subtype.OpenOpusResponse;
import dev.jlynx.openopusjava.response.subtype.SearchEntry;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class OmnisearchResponse extends OpenOpusResponse {

    private final List<SearchEntry> results;
    private final Integer next;

    @JsonCreator
    public OmnisearchResponse(
            @JsonProperty("status")
            OpenOpusResponseStatus status,
            @JsonProperty("request")
            OpenOpusRequestMetadata request,
            @JsonProperty("results")
            List<SearchEntry> results,
            @JsonProperty("next")
            Integer next) {
        super(status, request);
        this.results = results;
        this.next = next;
    }

    public List<SearchEntry> getResults() {
        return results;
    }

    public Optional<Integer> getNext() {
        return Optional.ofNullable(next);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OmnisearchResponse that = (OmnisearchResponse) o;
        return Objects.equals(results, that.results) && Objects.equals(next, that.next);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), results, next);
    }

    @Override
    public String toString() {
        return "OmnisearchResponse{" + "\n" +
                "status=" + status + ",\n" +
                "request=" + request + ",\n" +
                "results=" + results + ",\n" +
                "next=" + next + "\n" +
                '}';
    }
}
