package dev.jlynx.openopusjava.response.body;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.jlynx.openopusjava.response.subtype.OpenOpusResponse;
import dev.jlynx.openopusjava.response.subtype.RandomWork;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RandomWorks extends OpenOpusResponse {

    private final List<RandomWork> works;

    @JsonCreator
    public RandomWorks(
            @JsonProperty("status")
            OpenOpusResponseStatus status,
            @JsonProperty("request")
            OpenOpusRequestMetadata request,
            @JsonProperty("works")
            List<RandomWork> works
    ) {
        super(status, request);
        this.works = works;
    }

    public List<RandomWork> getWorks() {
        return works;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RandomWorks that = (RandomWorks) o;
        return Objects.equals(works, that.works);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), works);
    }

    @Override
    public String toString() {
        return "RandomWorks{" + "\n" +
                "works=" + works + ",\n" +
                "status=" + status + ",\n" +
                "request=" + request + "\n" +
                '}';
    }
}
