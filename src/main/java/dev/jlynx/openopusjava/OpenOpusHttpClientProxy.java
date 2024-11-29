package dev.jlynx.openopusjava;

import dev.jlynx.openopusjava.exception.OpenOpusErrorException;
import dev.jlynx.openopusjava.internal.json.JsonBodyHandler;
import dev.jlynx.openopusjava.internal.util.SpaceEncoder;
import dev.jlynx.openopusjava.response.subtype.OpenOpusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

class OpenOpusHttpClientProxy {

    private static final Logger log = LoggerFactory.getLogger(OpenOpusHttpClientProxy.class);
    private static final String BASE_URL = "https://api.openopus.org";

    private final HttpClient http;
    private final OpenOpusClientOptions options;

    public OpenOpusHttpClientProxy(OpenOpusClientOptions options) {
        http = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        this.options = options;
    }

    public OpenOpusHttpClientProxy(HttpClient http) {
        this.options = OpenOpusClientOptions.withDefaults();
        this.http = http;
    }

    public OpenOpusHttpClientProxy(OpenOpusClientOptions options, HttpClient http) {
        this.options = options;
        this.http = http;
    }

    public void closeClient() {
        http.close();
    }

    /**
     * Sends an asynchronous GET request to the desired the Open Opus API endpoint.
     * <p>
     * This method uses the Java's built-in {@link HttpClient}. It constructs a request
     * URI by appending a given path to the base URL and encoding it. It then sends the
     * request asynchronously and parses the response into a specified type extending {@link OpenOpusResponse}.
     * If the response status indicates an error, it is processed by {@code handleStatusError}.
     *
     * @param uriPath the API endpoint relative to the base domain {@code "https://api.openopus.org"}
     * @param responseBodyType the expected type of the API's response body
     * @return a {@link CompletableFuture} that, when completed, contains an {@link HttpResponse} with
     *         the {@link OpenOpusResponse} inheriting object
     * @param <T> the type of the response body, extending {@link OpenOpusResponse}
     * @throws java.util.concurrent.CompletionException if the Open Opus API returns a status error
     *
     * @see SpaceEncoder#encode(String)
     */
    public <T extends OpenOpusResponse> CompletableFuture<HttpResponse<T>> sendAsyncGetOpenOpus(String uriPath, Class<T> responseBodyType) {
//        int currentRetry = 0;
        URI uri = URI.create(BASE_URL + uriPath);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        if (options.isLogging()) {
            log.debug("Sending {} request to {}", request.method(), request.uri());
        }
        var future = http.sendAsync(request, new JsonBodyHandler<>(responseBodyType));
        // todo: retries with exponential backoff and perhaps also jitter
        return future.thenApply(res -> (HttpResponse<T>) handleStatusError(res));
    }

    private HttpResponse<? extends OpenOpusResponse> handleStatusError(HttpResponse<? extends OpenOpusResponse> res) {
        if (!res.body().getStatus().isSuccess()) {
            throw new OpenOpusErrorException(res.body().getStatus().getError().orElse(""));
        }
        return res;
    }
}
