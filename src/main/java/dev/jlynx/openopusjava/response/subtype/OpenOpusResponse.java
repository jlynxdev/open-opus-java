package dev.jlynx.openopusjava.response.subtype;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The parent class for all Open Opus API response body objects. Provides the common
 * fields that all Open Opus API endpoints return.
 * <p>
 * The use of this class on its own is discouraged, and it's generally best to use one
 * of its inheritors.
 *
 * @see <a href="https://github.com/openopus-org/openopus_api/blob/master/USAGE.md#the-status-object">Open Opus API documentation</a>
 */
public class OpenOpusResponse {

    protected final OpenOpusResponseStatus status;
    protected final OpenOpusRequestMetadata request;

    @JsonCreator
    public OpenOpusResponse(
            @JsonProperty("status")
            OpenOpusResponseStatus status,
            @JsonProperty("request")
            OpenOpusRequestMetadata request
    ) {
        this.status = status;
        this.request = request;
    }

    public OpenOpusResponseStatus getStatus() {
        return status;
    }

    public Optional<OpenOpusRequestMetadata> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpenOpusResponse that = (OpenOpusResponse) o;
        return Objects.equals(status, that.status) && Objects.equals(request, that.request);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, request);
    }

    @Override
    public String toString() {
        return "OpenOpusResponse{" +
                "status=" + status +
                ", request=" + request +
                '}';
    }

    /**
     * Represents the Open Opus API status object.
     *
     * @see <a href="https://github.com/openopus-org/openopus_api/blob/master/USAGE.md#the-status-object">Open Opus API documentation</a>
     */
    public static class OpenOpusResponseStatus {
        private final String version;
        private final boolean success;
        private final String error;
        private final String source;
        private final int rows;
        private final double processingTime;
        private final String api;

        @JsonCreator
        public OpenOpusResponseStatus(
                @JsonProperty("version")
                String version,
                @JsonProperty("success")
                boolean success,
                @JsonProperty("error")
                String error,
                @JsonProperty("source")
                String source,
                @JsonProperty("rows")
                int rows,
                @JsonProperty("processingtime")
                double processingTime,
                @JsonProperty("api")
                String api
        ) {
            this.version = version;
            this.success = success;
            this.error = error;
            this.source = source;
            this.rows = rows;
            this.processingTime = processingTime;
            this.api = api;
        }

        public String getVersion() {
            return version;
        }

        public boolean isSuccess() {
            return success;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OpenOpusResponseStatus that = (OpenOpusResponseStatus) o;
            return success == that.success &&
                    rows == that.rows &&
                    Double.compare(processingTime, that.processingTime) == 0 &&
                    Objects.equals(version, that.version) &&
                    Objects.equals(error, that.error) &&
                    Objects.equals(source, that.source) &&
                    Objects.equals(api, that.api);
        }

        @Override
        public int hashCode() {
            return Objects.hash(version, success, error, source, rows, processingTime, api);
        }

        @Override
        public String toString() {
            return "OpenOpusResponseStatus{" +
                    "version='" + version + '\'' +
                    ", success=" + success +
                    ", error='" + error + '\'' +
                    ", source='" + source + '\'' +
                    ", rows=" + rows +
                    ", processingTime=" + processingTime +
                    ", api='" + api + '\'' +
                    '}';
        }

        /**
         * Returns an {@code Optional} with the error message string or null if no error has occurred.
         */
        public Optional<String> getError() {
            return Optional.ofNullable(error);
        }

        public String getSource() {
            return source;
        }

        public int getRows() {
            return rows;
        }

        public double getProcessingTime() {
            return processingTime;
        }

        public String getApi() {
            return api;
        }
    }

    public static class OpenOpusRequestMetadata {

        private final String type;
        private final List<String> item;
        private final String search;
        private final Integer offset;

        @JsonCreator
        public OpenOpusRequestMetadata(
                @JsonProperty("type")
                String type,
                @JsonProperty("item")
                Object item,
                @JsonProperty("search")
                String search,
                @JsonProperty("offset")
                String offset
        ) {
            this.type = type;
            if (item == null) {
                this.item = null;
            } else if (item instanceof List<?>) {
                this.item = ((List<?>) item).stream().map(Object::toString).toList();
            } else {
                this.item = List.of(item.toString());
            }
            this.search = search;
            this.offset = offset != null ? Integer.valueOf(offset) : null;
        }

        public String getType() {
            return type;
        }

        public Optional<List<String>> getItem() {
            return Optional.ofNullable(item);
        }

        public Optional<String> getSearch() {
            return Optional.ofNullable(search);
        }

        public Optional<Integer> getOffset() {
            return Optional.ofNullable(offset);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OpenOpusRequestMetadata that = (OpenOpusRequestMetadata) o;
            return Objects.equals(type, that.type) && Objects.equals(item, that.item);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, item);
        }

        @Override
        public String toString() {
            return "OpenOpusRequestMetadata{" +
                    "type='" + type + '\'' +
                    ", item='" + item + '\'' +
                    ", search='" + search + '\'' +
                    ", offset=" + offset +
                    '}';
        }
    }
}
