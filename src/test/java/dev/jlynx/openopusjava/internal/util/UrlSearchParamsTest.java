package dev.jlynx.openopusjava.internal.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class UrlSearchParamsTest {

    private UrlSearchParams underTest;

    @BeforeEach
    void setUp() {
        this.underTest = new UrlSearchParams();
    }

    @ParameterizedTest
    @MethodSource("queryParamProvider")
    void addParam_ShouldProperlyAddParams(HashMap<String, String> params, String expectedString) {
        // WHEN
        for (Map.Entry<String, String> entry : params.entrySet()) {
            underTest.addParam(entry.getKey(), entry.getValue());
        }

        // THEN
        assertEquals(expectedString, underTest.asString());
    }

    public static Stream<Arguments> queryParamProvider() {
        HashMap<String, String> params0 = new HashMap<>();
        String str0 = "";
        HashMap<String, String> params1 = new HashMap<>();
        params1.put("key%?1", "value 1");
        String str1 = "?key%25%3F1=value+1";
        HashMap<String, String> params3 = new HashMap<>();
        params3.put("key1", "value 1");
        params3.put("key2", "value@!2");
        params3.put("key&3", "value3");
        String str3 = "?key1=value+1&key2=value%40%212&key%263=value3";

        return Stream.of(
                arguments(params0, str0),
                arguments(params1, str1),
                arguments(params3, str3)
        );
    }
}