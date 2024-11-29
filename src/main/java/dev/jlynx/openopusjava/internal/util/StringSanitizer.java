package dev.jlynx.openopusjava.internal.util;

import dev.jlynx.openopusjava.internal.Internal;

@Internal
public class StringSanitizer {

    public String sanitize(String searchString) {
        searchString = searchString.strip();
        searchString = searchString.replaceAll("\\s+", " ");
        searchString = searchString.replaceAll("[^a-zA-Z\\s]+", "");
        return searchString;
    }
}
