package dev.jlynx.openopusjava.internal.util;

import dev.jlynx.openopusjava.internal.Internal;

/**
 * Encodes spaces in a string by replacing each space with its UTF-8 URL encoding.
 * <p>
 * This class can be used to encode spaces in a URL path before the '?' query parameter separator.
 * </p>
 */
@Internal
public class SpaceEncoder {

    /**
     * Replaces spaces in the given string with its UTF-8 URL encoding: "%20".
     *
     * @param str the input string
     * @return a new string with spaces replaced by "%20"
     */
    public String encode(String str) {
        return str.replaceAll(" ", "%20");
    }
}
