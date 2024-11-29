package dev.jlynx.openopusjava.response.subtype;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ListWorksByIdAbstract(ComposersAbstract composers, WorksAbstract works) {

    @JsonCreator
    public ListWorksByIdAbstract(
            @JsonProperty("composers")
            ComposersAbstract composers,
            @JsonProperty("works")
            WorksAbstract works
    ) {
        this.composers = composers;
        this.works = works;
    }

    public record ComposersAbstract(List<String> portraitUris, List<String> names, int rows) {

        @JsonCreator
        public ComposersAbstract(
                @JsonProperty("portraits")
                List<String> portraitUris,
                @JsonProperty("names")
                List<String> names,
                @JsonProperty("rows")
                int rows
        ) {
            this.portraitUris = portraitUris;
            this.names = names;
            this.rows = rows;
        }
    }

    public record WorksAbstract(int rows) {

        @JsonCreator
        public WorksAbstract(
                @JsonProperty("rows")
                int rows
        ) {
            this.rows = rows;
        }
    }
}
