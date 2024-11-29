package dev.jlynx.openopusjava.response.subtype;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents a musical epoch in the history of classical music.
 */
public enum Epoch {
    MEDIEVAL("Medieval"),
    RENAISSANCE("Renaissance"),
    BAROQUE("Baroque"),
    CLASSICAL("Classical"),
    EARLY_ROMANTIC("Early Romantic"),
    ROMANTIC("Romantic"),
    LATE_ROMANTIC("Late Romantic"),
    TWENTIETH_CENTURY("20th Century"),
    POST_WAR("Post-War"),
    TWENTY_FIRST_CENTURY("21st Century");

    @JsonValue
    final String value;

    Epoch(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
