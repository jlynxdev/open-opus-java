package dev.jlynx.openopusjava.response.subtype;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents one of the genres in classical music as well as the "Popular" and "Recommended" tags.
 */
public enum Genre {

    POPULAR,
    RECOMMENDED,
    CHAMBER,
    KEYBOARD,
    ORCHESTRAL,
    VOCAL,
    STAGE;

    /**
     * Returns the title case string version of this enum's name.
     */
    @JsonValue
    public String getValue() {
        String lower = this.name().toLowerCase();
        char firstChar = Character.toUpperCase(lower.charAt(0));
        return firstChar + lower.substring(1);
    }
}
