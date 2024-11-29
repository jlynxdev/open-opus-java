package dev.jlynx.openopusjava.internal.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class StringSanitizerTest {

    StringSanitizer underTest;

    @BeforeEach
    void setUp() {
        this.underTest = new StringSanitizer();
    }

    @ParameterizedTest
    @MethodSource("searchStringProvider")
    void sanitizeShouldSanitize(String searchString, String expectedSanitizedString) {
        String returned = underTest.sanitize(searchString);
        assertEquals(expectedSanitizedString, returned);
    }

    private static Stream<Arguments> searchStringProvider() {
        return Stream.of(
                arguments("  beeth  ", "beeth"),
                arguments("mo?art", "moart"),
                arguments("j. s. bach", "j s bach"),
                arguments("/bart\\ok", "bartok"),
                arguments("4ntonio", "ntonio"),
                arguments("5#*/9", ""),
                arguments("  ", ""),
                arguments("somestring", "somestring")
        );
    }
}