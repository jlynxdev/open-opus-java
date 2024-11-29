package dev.jlynx.openopusjava.internal.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.jlynx.openopusjava.internal.Internal;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * A {@code JsonBodyHandler} is an implementation of {@link HttpResponse.BodyHandler}
 * that converts the response body from JSON into a specified Java type.
 * <p>
 * This handler leverages the Jackson library to parse JSON content into Java objects.
 * It is intended to be used with the Java {@code HttpClient} to handle JSON responses
 * by mapping them to the specified {@code targetType}.
 *
 * @param <T> the type of the response body that this handler deserializes from JSON
 */
@Internal
public class JsonBodyHandler<T> implements HttpResponse.BodyHandler<T> {

    private final Class<T> targetType;

    /**
     * Constructs a new {@code JsonBodyHandler} for the specified target type.
     *
     * @param targetType The {@link Class} of the type to which the JSON response
     *                   should be deserialized.
     */
    public JsonBodyHandler(Class<T> targetType) {
        this.targetType = targetType;
    }

    @Override
    public HttpResponse.BodySubscriber<T> apply(HttpResponse.ResponseInfo responseInfo) {
        return asJson(targetType);
    }

    private static <T> HttpResponse.BodySubscriber<T> asJson(Class<T> targetType) {
        HttpResponse.BodySubscriber<String> upstream = HttpResponse
                .BodySubscribers.ofString(StandardCharsets.UTF_8);
        return HttpResponse.BodySubscribers.mapping(
                upstream,
                (String body) -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.registerModule(new JavaTimeModule());
                        return mapper.readValue(body, targetType);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }
}
