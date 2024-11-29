package dev.jlynx.openopusjava;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jlynx.openopusjava.exception.OpenOpusException;
import dev.jlynx.openopusjava.internal.util.SpaceEncoder;
import dev.jlynx.openopusjava.internal.util.StringSanitizer;
import dev.jlynx.openopusjava.request.RandomWorksCriteria;
import dev.jlynx.openopusjava.response.body.*;
import dev.jlynx.openopusjava.response.subtype.Epoch;
import dev.jlynx.openopusjava.response.subtype.Genre;
import dev.jlynx.openopusjava.internal.util.UrlSearchParams;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * The main entry point to the asynchronous Open Opus API client.
 *
 * <p> Example usage:
 * <pre>{@code
 * OpenOpusClient client = new OpenOpusClient(OpenOpusClientOptions.withDefaults());
 * CompletableFuture<HttpResponse<ComposersList>> composers = client.listComposers('b');
 * }</pre>
 */
public class OpenOpusClient implements AutoCloseable {

    private static final String BASE_URL = "https://api.openopus.org";
    private static final String BASE_URL_DYN = "https://dynapi.openopus.org";

    private final OpenOpusHttpClientProxy http;
    private final SpaceEncoder spaceEncoder;
    private final StringSanitizer sanitizer;
    private final OpenOpusClientOptions options;

    // todo: make this constructor call this(OpenOpusClientOptions.withDefaults()); ?
    public OpenOpusClient() {
        options = OpenOpusClientOptions.withDefaults();
        http = new OpenOpusHttpClientProxy(options);
        spaceEncoder = new SpaceEncoder();
        sanitizer = new StringSanitizer();
    }

    public OpenOpusClient(OpenOpusClientOptions options) {
        this.options = options;
        http = new OpenOpusHttpClientProxy(options);
        spaceEncoder = new SpaceEncoder();
        sanitizer = new StringSanitizer();
    }

    OpenOpusClient(HttpClient httpClient, SpaceEncoder spaceEncoder, StringSanitizer sanitizer) {
        options = OpenOpusClientOptions.withDefaults();
        this.http = new OpenOpusHttpClientProxy(options, httpClient);
        this.spaceEncoder = spaceEncoder;
        this.sanitizer = sanitizer;
    }

    /**
     * Closes the underlying {@link HttpClient}.
     */
    @Override
    public void close() {
        http.closeClient();
    }

    /**
     * Asynchronously retrieves a list of composers whose surnames start with the specified letter.
     * <p>
     * This method sends a GET request to the Open Opus API to fetch composers based on
     * the first letter. The response body is parsed into a {@code ComposersList} object.
     *
     * @param letter the first letter of the composers' names to filter by; must be an alphabetical letter
     * @return a {@link CompletableFuture} that, when completed, contains an {@link HttpResponse} with
     *         the {@link ComposersList} of composers
     * @throws IllegalArgumentException if the {@code letter} parameter is not a letter
     * @throws java.util.concurrent.CompletionException if the Open Opus API returns a status error
     */
    public CompletableFuture<HttpResponse<ComposersList>> listComposers(char letter) {
        if (!Character.isLetter(letter)) {
            throw new IllegalArgumentException("The 'letter' parameter should be a letter");
        }
        String uri = "/composer/list/name/" + letter + ".json";
        return http.sendAsyncGetOpenOpus(uri, ComposersList.class);
    }

    /**
     * Asynchronously retrieves a list of popular composers from the Open Opus API.
     * <p>
     * The response body is parsed into a {@code ComposersList} object.
     *
     * @return a {@link CompletableFuture} that, when completed, contains an {@link HttpResponse} with
     *         the {@link ComposersList} of composers
     * @throws java.util.concurrent.CompletionException if the Open Opus API returns a status error
     */
    public CompletableFuture<HttpResponse<ComposersList>> getPopularComposers() {
        return http.sendAsyncGetOpenOpus("/composer/list/pop.json", ComposersList.class);
    }

    /**
     * Asynchronously retrieves a list of recommended (or "essential") composers from the Open Opus API.
     * <p>
     * The response body is parsed into a {@code ComposersList} object.
     *
     * @return a {@link CompletableFuture} that, when completed, contains an {@link HttpResponse} with
     *         the {@link ComposersList} of composers
     * @throws java.util.concurrent.CompletionException if the Open Opus API returns a status error
     */
    public CompletableFuture<HttpResponse<ComposersList>> getEssentialComposers() {
        return http.sendAsyncGetOpenOpus("/composer/list/rec.json", ComposersList.class);
    }

    /**
     * Asynchronously retrieves a list of composers who were active in the given musical epoch.
     * <p>
     * This method sends a GET request to the Open Opus API to fetch composers based on
     * the first letter. The response body is parsed into a {@code ComposersList} object.
     * </p>
     * @param epoch the musical epoch to filter composers by
     * @return a {@link CompletableFuture} that, when completed, contains an {@link HttpResponse} with
     *         the {@link ComposersList} of composers
     * @throws java.util.concurrent.CompletionException if the Open Opus API returns a status error
     */
    public CompletableFuture<HttpResponse<ComposersList>> listComposers(Epoch epoch) {
        String uri = "/composer/list/epoch/" + spaceEncoder.encode(epoch.getValue()) + ".json";
        return http.sendAsyncGetOpenOpus(uri, ComposersList.class);
    }

    /**
     * Asynchronously searches for composers based on a given name or keyword and returns a list of matching composers.
     * <p>
     * This method takes a search string as input, sanitizes it to remove extraneous whitespace and non-alphabetical
     * characters, and constructs a GET request to query the Open Opus API. The resulting {@link HttpResponse}
     * contains a {@link ComposersList}, which is parsed from JSON format.
     * </p>
     *
     * @param searchString the name or keyword to search for composers; it will be sanitized to ensure a valid query format.
     * @return a {@link CompletableFuture} that completes with an {@link HttpResponse} containing the {@link ComposersList}
     *         of matching composers.
     * @throws IllegalArgumentException if the searchString is null or empty after sanitization.
     * @throws java.util.concurrent.CompletionException if the Open Opus API returns a status error
     */
    public CompletableFuture<HttpResponse<ComposersList>> searchComposers(String searchString) {
        searchString = sanitizer.sanitize(searchString);
        if (searchString.isEmpty()) {
            throw new IllegalArgumentException("The 'searchString' is null or empty after sanitization.");
        }
        searchString = spaceEncoder.encode(searchString);
        String uri = "/composer/list/search/" + searchString + ".json";
        return http.sendAsyncGetOpenOpus(uri, ComposersList.class);
    }

    /**
     * Asynchronously retrieves a list of composers from the OpenOpus API based on their unique IDs.
     * <p>
     * This method constructs a URI with the specified composer IDs and makes an asynchronous HTTP GET request
     * to fetch the list of composers. If any ID in the provided list is less than 1, an {@code IllegalArgumentException}
     * is thrown.
     * </p>
     *
     * @param ids A {@code List} of {@code Integer} values representing the unique IDs of composers.
     *            Each ID must be greater than zero.
     * @return A {@code CompletableFuture} containing an {@code HttpResponse} with a {@code ComposersList} as the response body.
     * @throws IllegalArgumentException if any composer ID in the {@code ids} list is less than 1
     * @throws java.util.concurrent.CompletionException if the Open Opus API returns a status error
     */
    public CompletableFuture<HttpResponse<ComposersList>> listComposers(List<Integer> ids) {
        if (ids.stream().anyMatch(i -> i < 1)) {
            throw new IllegalArgumentException("Composer id cannot be less than one.");
        }
        String urlIds = ids.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
        String uri = "/composer/list/ids/" + urlIds + ".json";
        return http.sendAsyncGetOpenOpus(uri, ComposersList.class);
    }

    /**
     * Asynchronously retrieves a list of musical genres ({@link Genre} objects) associated with a specific composer from the OpenOpus API.
     * <p>
     * This method constructs a URI using the given composer ID and makes an asynchronous HTTP GET request
     * to fetch the list of genres related to the composer. If the provided composer ID is less than 1,
     * an {@code IllegalArgumentException} is thrown.
     * </p>
     *
     * @param composerId A number representing the unique ID of the composer.
     *                   The ID must be greater than zero.
     * @return A {@code CompletableFuture} containing an {@code HttpResponse} with a {@code GenresList} as the response body.
     * @throws IllegalArgumentException if the {@code composerId} is less than 1
     * @throws java.util.concurrent.CompletionException if the Open Opus API returns a status error
     *
     * @see Genre
     */
    public CompletableFuture<HttpResponse<GenresList>> listGenres(int composerId) {
        if (composerId < 1) {
            throw new IllegalArgumentException("Composer id cannot be less than one.");
        }
        String uri = "/genre/list/composer/" + composerId + ".json";
        return http.sendAsyncGetOpenOpus(uri, GenresList.class);
    }

    /**
     * Asynchronously retrieves a list of musical works by a specific composer from the OpenOpus API.
     * <p>
     * This method constructs a URI using the provided composer ID and makes an asynchronous HTTP GET request
     * to fetch all works related to the composer across all genres. If the provided composer ID is less than 1,
     * an {@code IllegalArgumentException} is thrown.
     * </p>
     *
     * @param composerId A number representing the unique ID of the composer.
     *                   The ID must be greater than zero.
     * @return A {@code CompletableFuture} containing an {@code HttpResponse} with a {@code WorksList} as the response body.
     * @throws IllegalArgumentException if the {@code composerId} is less than 1
     * @throws java.util.concurrent.CompletionException if the Open Opus API returns a status error
     */
    public CompletableFuture<HttpResponse<WorksList>> listWorks(int composerId) {
        if (composerId < 1) {
            throw new IllegalArgumentException("Composer id cannot be less than one.");
        }
        String uri = "/work/list/composer/" + composerId + "/genre/all.json";
        return http.sendAsyncGetOpenOpus(uri, WorksList.class);
    }

    /**
     * Asynchronously retrieves a list of musical works by a specific composer and genre from the OpenOpus API.
     * <p>
     * This method constructs a URI using the provided composer ID and genre, then makes an asynchronous HTTP GET request
     * to fetch the list of works related to the composer within the specified genre. If the provided composer ID is less than 1,
     * an {@code IllegalArgumentException} is thrown.
     * </p>
     *
     * @param composerId A number representing the unique ID of the composer.
     *                   The ID must be greater than zero.
     * @param genre      A {@code Genre} object representing the specific genre to filter the composer's works.
     * @return A {@code CompletableFuture} containing an {@code HttpResponse} with a {@code WorksList} as the response body.
     * @throws IllegalArgumentException if the {@code composerId} is less than 1.
     * @throws java.util.concurrent.CompletionException if the Open Opus API returns a status error
     */
    public CompletableFuture<HttpResponse<WorksList>> listWorks(int composerId, Genre genre) {
        if (composerId < 1) {
            throw new IllegalArgumentException("Composer id cannot be less than one.");
        }
        String uri = String.format("/work/list/composer/%d/genre/%s.json", composerId, genre.getValue());
        return http.sendAsyncGetOpenOpus(uri, WorksList.class);
    }

    /**
     * Asynchronously searches for musical works by a specific composer based on a search string from the OpenOpus API.
     * <p>
     * This method constructs a URI using the provided composer ID and search string, then makes an asynchronous HTTP GET request
     * to fetch works that match the search criteria across all genres. The search string is sanitized before use.
     * If the provided composer ID is less than 1, or if the sanitized search string results in an empty string, an
     * {@code IllegalArgumentException} is thrown.
     * </p>
     *
     * @param searchString A {@code String} representing the search term to match against the composer's works.
     *                     Must not be null or empty after sanitization.
     * @param composerId   A number representing the unique ID of the composer.
     *                     The ID must be greater than zero.
     * @return A {@code CompletableFuture} containing an {@code HttpResponse} with a {@code WorksList} as the response body.
     * @throws IllegalArgumentException if the {@code composerId} is less than 1 or if the sanitized {@code searchString} is empty.
     * @throws java.util.concurrent.CompletionException if the Open Opus API returns a status error
     */
    public CompletableFuture<HttpResponse<WorksList>> searchWorks(String searchString, int composerId) {
        if (composerId < 1) {
            throw new IllegalArgumentException("Composer id cannot be less than one.");
        }
        searchString = sanitizer.sanitize(searchString);
        if (searchString.isEmpty()) {
            throw new IllegalArgumentException("The searchString is null or empty after sanitization..");
        }
        String uri = String.format("/work/list/composer/%d/genre/all/search/%s.json", composerId, spaceEncoder.encode(searchString));
        return http.sendAsyncGetOpenOpus(uri, WorksList.class);
    }

    /**
     * Asynchronously searches for musical works by a specific composer within a specified genre based on a search string from the OpenOpus API.
     * <p>
     * This method constructs a URI using the provided composer ID, genre, and search string, then makes an asynchronous HTTP GET request
     * to fetch works that match the search criteria within the specified genre. The search string is sanitized before use.
     * If the provided composer ID is less than 1, or if the sanitized search string is empty, an {@code IllegalArgumentException} is thrown.
     * </p>
     *
     * @param searchString A {@code String} representing the search term to match against the composer's works.
     *                     Must not be null or empty after sanitization.
     * @param composerId   A number representing the unique ID of the composer.
     *                     The ID must be greater than zero.
     * @param genre        A {@code Genre} object specifying the genre to filter the composer's works.
     * @return A {@code CompletableFuture} containing an {@code HttpResponse} with a {@code WorksList} as the response body.
     * @throws IllegalArgumentException if the {@code composerId} is less than 1 or if the sanitized {@code searchString} is empty.
     * @throws java.util.concurrent.CompletionException if the Open Opus API returns a status error
     */
    public CompletableFuture<HttpResponse<WorksList>> searchWorks(String searchString, int composerId, Genre genre) {
        if (composerId < 1) {
            throw new IllegalArgumentException("Composer id cannot be less than one.");
        }
        searchString = sanitizer.sanitize(searchString);
        if (searchString.isEmpty()) {
            throw new IllegalArgumentException("The searchString is null or empty after sanitization..");
        }
        String uri = String.format("/work/list/composer/%d/genre/%s/search/%s.json", composerId,
                spaceEncoder.encode(genre.getValue()), spaceEncoder.encode(searchString));
        return http.sendAsyncGetOpenOpus(uri, WorksList.class);
    }

    /**
     * Asynchronously retrieves detailed information about a specific musical work from the OpenOpus API.
     * <p>
     * This method constructs a URI using the provided work ID and makes an asynchronous HTTP GET request
     * to fetch detailed information about the specified work. If the provided work ID is less than 1,
     * an {@code IllegalArgumentException} is thrown.
     * </p>
     *
     * @param workId A number representing the unique ID of the musical work.
     *               The ID must be greater than zero.
     * @return A {@code CompletableFuture} containing an {@code HttpResponse} with a {@code WorkDetailResponse} as the response body.
     * @throws IllegalArgumentException if the {@code workId} is less than 1.
     * @throws java.util.concurrent.CompletionException if the Open Opus API returns a status error
     */
    public CompletableFuture<HttpResponse<WorkDetailResponse>> getWorkDetails(int workId) {
        if (workId < 1) {
            throw new IllegalArgumentException("Composer id cannot be less than one.");
        }
        String uri = String.format("/work/detail/%d.json", workId);
        return http.sendAsyncGetOpenOpus(uri, WorkDetailResponse.class);
    }

    /**
     * Asynchronously retrieves a list of musical works from the OpenOpus API based on their unique IDs.
     * <p>
     * This method constructs a URI with the specified work IDs and makes an asynchronous HTTP GET request
     * to fetch details about each work. If any ID in the provided list is less than 1, an {@code IllegalArgumentException}
     * is thrown.
     * </p>
     *
     * @param workIds A {@code List} of numbers representing the unique IDs of musical works.
     *                Each ID must be greater than zero.
     * @return A {@code CompletableFuture} containing an {@code HttpResponse} with a {@code ListWorksByIdResponse} as the response body.
     * @throws IllegalArgumentException if any work ID in the {@code workIds} list is less than 1.
     * @throws java.util.concurrent.CompletionException if the Open Opus API returns a status error
     */
    public CompletableFuture<HttpResponse<ListWorksByIdResponse>> listWorks(List<Integer> workIds) {
        workIds.forEach(id -> {
            if (id < 1) {
                throw new IllegalArgumentException("Work ID cannot be less than one.");
            }
        });
        String urlIds = workIds.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
        String uri = "/work/list/ids/" + urlIds + ".json";
        return http.sendAsyncGetOpenOpus(uri, ListWorksByIdResponse.class);
    }

    /**
     * Asynchronously retrieves a randomized list of musical works from the OpenOpus API based on specified criteria.
     * <p>
     * This method constructs a URI with query parameters based on the provided {@code RandomWorksCriteria} and makes an asynchronous
     * HTTP GET request to fetch a random selection of works that match the criteria. Each criterion in {@code RandomWorksCriteria}
     * is optional, allowing flexible filtering by attributes like popularity, recommendation status, genre, epoch, and specific
     * composers or works to include or exclude.
     * </p>
     *
     * @param criteria A {@code RandomWorksCriteria} object containing optional filters to apply when retrieving random works.
     * @return A {@code CompletableFuture} containing an {@code HttpResponse} with a {@code RandomWorks} as the response body.
     * @throws java.util.concurrent.CompletionException if the Open Opus API returns a status error
     *
     * @see RandomWorksCriteria
     */
    public CompletableFuture<HttpResponse<RandomWorks>> listRandomWorks(RandomWorksCriteria criteria) {
        UrlSearchParams params = new UrlSearchParams();
        if (criteria.getPopularWork().isPresent()) {
            params.addParam("popularwork", criteria.getPopularWork().get() ? "1" : "0");
        }
        if (criteria.getRecommendedWork().isPresent()) {
            params.addParam("recommendedwork", criteria.getRecommendedWork().get() ? "1" : "0");
        }
        if (criteria.getPopularComposer().isPresent()) {
            params.addParam("popularcomposer", criteria.getPopularComposer().get() ? "1" : "0");
        }
        if (criteria.getRecommendedComposer().isPresent()) {
            params.addParam("recommendedcomposer", criteria.getRecommendedComposer().get() ? "1" : "0");
        }
        if (criteria.getGenre().isPresent()) {
            params.addParam("genre", criteria.getGenre().get().getValue());
        }
        if (criteria.getEpoch().isPresent()) {
            params.addParam("epoch", criteria.getEpoch().get().getValue());
        }
        if (criteria.getComposer().isPresent()) {
            String ids = criteria.getComposer().get()
                    .stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            params.addParam("composer", ids);
        }
        if (criteria.getComposerNot().isPresent()) {
            String ids = criteria.getComposerNot().get()
                    .stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            params.addParam("composer_not", ids);
        }
        if (criteria.getWork().isPresent()) {
            String ids = criteria.getWork().get()
                    .stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            params.addParam("work", ids);
        }
        String uri = "/dyn/work/random" + params.asString();
        return http.sendAsyncGetOpenOpus(uri, RandomWorks.class);
    }

    /**
     * Asynchronously performs a comprehensive search across both works and composers in the OpenOpus API based on a search term.
     * <p>
     * This method constructs a URI using the provided search term and offset, then makes an asynchronous HTTP GET request
     * to retrieve matching results across various categories. The search term is sanitized before being included in the URI.
     * If the offset is less than 0, an {@code IllegalArgumentException} is thrown.
     * </p>
     *
     * @param search A {@code String} representing the search term to query across multiple categories.
     *               The search term will be sanitized before use.
     * @param offset A number representing the starting point for paginated results. Must be zero or greater.
     * @return A {@code CompletableFuture} containing an {@code HttpResponse} with an {@code OmnisearchResponse} as the response body.
     * @throws IllegalArgumentException if {@code offset} is less than zero.
     * @throws java.util.concurrent.CompletionException if the Open Opus API returns a status error
     *
     * @see OmnisearchResponse
     */
    public CompletableFuture<HttpResponse<OmnisearchResponse>> search(String search, int offset) {
        if (offset < 0) {
            throw new IllegalArgumentException("'offset' parameter cannot be less than zero.");
        }
        search = sanitizer.sanitize(search);
        if (search.isEmpty()) {
            throw new IllegalArgumentException("The 'search' parameter is empty after sanitization.");
        }
        search = spaceEncoder.encode(search);
        String uri = String.format("/omnisearch/%s/%d.json", search, offset);
        return http.sendAsyncGetOpenOpus(uri, OmnisearchResponse.class);
    }

    /**
     * Asynchronously retrieves the roles of specified performers from the OpenOpus API.
     * <p>
     * This method constructs a URI with the provided list of performer names, serializing the list to JSON format
     * and encoding it for use in the URI query string. Performer names will be sanitized to ensure character validity.
     * It then makes an asynchronous HTTP GET request to fetch the roles associated with each performer. If the list
     * of performers is empty, an {@code IllegalArgumentException} is thrown.
     * </p>
     *
     * @param performers A list of names of performers whose roles are to be retrieved.
     *                   The list must contain at least one performer. The names will be sanitized;
     * @return A {@code CompletableFuture} containing an {@code HttpResponse} with a {@code PerformerRolesResponse} as the response body.
     * @throws IllegalArgumentException if the {@code performers} list is empty.
     * @throws OpenOpusException if there is an error serializing the {@code performers} list to JSON format.
     * @throws java.util.concurrent.CompletionException if the Open Opus API returns a status error
     */
    public CompletableFuture<HttpResponse<PerformerRolesResponse>> listPerformerRoles(List<String> performers) {
        if (performers.isEmpty()) {
            throw new IllegalArgumentException("'performers' list cannot be empty");
        }
        performers = performers.stream()
                .map(sanitizer::sanitize)
                .toList();
        ObjectMapper mapper = new ObjectMapper();
        String performersJson;
        try {
            performersJson = mapper.writeValueAsString(performers);
        } catch (JsonProcessingException e) {
            throw new OpenOpusException(e);
        }
        performersJson = URLEncoder.encode(performersJson, StandardCharsets.UTF_8);
        String uri = "/dyn/performer/list?names=" + performersJson;
        return http.sendAsyncGetOpenOpus(uri, PerformerRolesResponse.class);
    }
}
