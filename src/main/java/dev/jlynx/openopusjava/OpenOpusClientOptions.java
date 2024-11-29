package dev.jlynx.openopusjava;

/**
 * Contains a set of options to adjust the {@link OpenOpusClient}.
 *
 * <p>The object should be instantiated through the builder pattern.
 *
 * <p>Example usage:
 * <pre>{@code
 * OpenOpusClientOptions options = OpenOpusClientOptions.builder()
 *     .maxRetries(5)
 *     .withLoggingEnabled()
 *     .build();
 * }</pre>
 */
public class OpenOpusClientOptions {

    private final boolean logging;
    private final int maxRetries;


    private OpenOpusClientOptions(boolean logging, int maxRetries) {
        this.logging = logging;
        this.maxRetries = maxRetries;
    }

    public boolean isLogging() {
        return logging;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public static OpenOpusClientOptionsBuilder builder() {
        return new OpenOpusClientOptionsBuilder();
    }

    /**
     * Sets default settings for the {@link OpenOpusClient}.
     *
     * <p>The default settings are:
     * <ul>
     *     <li>{@code logging = false}</li>
     *     <li>{@code maxRetries = 3}</li>
     * </ul>
     * @return a new {@code OpenOpusClientOptions} instance with default settings
     */
    public static OpenOpusClientOptions withDefaults() {
        return new OpenOpusClientOptionsBuilder().build();
    }


    public static class OpenOpusClientOptionsBuilder {

        private boolean logging;
        private int maxRetries;

        private OpenOpusClientOptionsBuilder() {
            logging = false;
            maxRetries = 3;
        }

        /**
         * Enables console logging for the {@link OpenOpusClient}.
         * @return this {@code OpenOpusClientOptionsBuilder} object with enabled logging
         */
        public OpenOpusClientOptionsBuilder withLoggingEnabled() {
            logging = true;
            return this;
        }

        /**
         * Sets the maximum number of query retries in case of e.g. network errors.
         *
         * @param maxRetries the upper limit for the number of query retries
         * @return this {@code OpenOpusClientOptionsBuilder} object with {@code maxRetires} set
         */
        public OpenOpusClientOptionsBuilder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        /**
         * Builds a new {@code OpenOpusClientOptions} instance with desired property values set.
         * @return a new {@code OpenOpusClientOptions} instance based on the builder's values
         */
        public OpenOpusClientOptions build() {
            return new OpenOpusClientOptions(logging, maxRetries);
        }
    }
}
