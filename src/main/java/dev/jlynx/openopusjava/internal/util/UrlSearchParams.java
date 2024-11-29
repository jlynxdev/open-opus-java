package dev.jlynx.openopusjava.internal.util;

import dev.jlynx.openopusjava.internal.Internal;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * A utility class for constructing URL query parameters.
 * This class provides methods to add key-value pairs to a query string in
 * a URL-friendly format.
 * <p>
 * The {@code UrlSearchParams} class uses a {@code StringBuilder} to build the
 * parameters and ensures correct encoding of both keys and values using UTF-8.
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 * UrlSearchParams params = new UrlSearchParams();
 * params.addParam("key1", "value1").addParam("key2", "value2");
 * String queryString = params.asString();  // ?key1=value1&key2=value2
 * }</pre>
 * </p>
 */
@Internal
public class UrlSearchParams {

    private final StringBuilder params;
    private boolean empty;

    /**
     * Constructs an empty {@code UrlSearchParams} instance.
     * Initializes the internal {@code StringBuilder} and sets the state to empty.
     */
    public UrlSearchParams() {
        params = new StringBuilder();
        empty = true;
    }

    /**
     * Adds a new parameter to the query string.
     * The parameter key and value are encoded in UTF-8 to ensure URL compatibility.
     * <p>
     * If the {@code UrlSearchParams} instance is empty, the method prepends a
     * question mark (?) to start the query string. Otherwise, an ampersand (&) is
     * used to separate additional parameters.
     * </p>
     *
     * @param key the parameter key to be added
     * @param value the parameter value to be added
     * @return this {@code UrlSearchParams} instance for chaining
     */
    public UrlSearchParams addParam(String key, String value) {
        if (empty) {
            params.append("?");
            empty = false;
        } else {
            params.append("&");
        }
        params.append(URLEncoder.encode(key, StandardCharsets.UTF_8))
                .append("=")
                .append(URLEncoder.encode(value, StandardCharsets.UTF_8));
        return this;
    }

    /**
     * Returns the constructed query string as a {@code String}.
     * <p>
     * Please note that the string includes the leading question mark symbol.
     * </p>
     *
     * @return the query string representation of the parameters
     */
    public String asString() {
        return params.toString();
    }
}
