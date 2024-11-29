package dev.jlynx.openopusjava;

import dev.jlynx.openopusjava.exception.OpenOpusErrorException;
import dev.jlynx.openopusjava.internal.util.StringSanitizer;
import dev.jlynx.openopusjava.request.RandomWorksCriteria;
import dev.jlynx.openopusjava.response.body.*;
import dev.jlynx.openopusjava.response.subtype.Epoch;
import dev.jlynx.openopusjava.response.subtype.Genre;
import dev.jlynx.openopusjava.response.subtype.PerformerRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@ExtendWith(MockitoExtension.class)
public class OpenOpusClientIntegrationTest {

//    private static final String BASE_URL = "https://api.openopus.org";
//
//    @Captor
//    private ArgumentCaptor<HttpRequest> httpRequestCaptor;

    private OpenOpusClient underTest;

    @BeforeEach
    void setUp() {
        underTest = new OpenOpusClient();
    }

    @AfterEach
    void tearDown() {
        underTest.close();
    }

    @ParameterizedTest
    @ValueSource(chars = {'/', '?', '&', '3'})
    void listComposersByLetter_ShouldThrow_WhenInvalidChar(char invalidChar) {
        // given
        Exception thrown = null;

        // when
        try {
            underTest.listComposers(invalidChar).join();
        } catch (Exception ex) {
            thrown = ex;
        }

        // then
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }

    @Test
    void listComposersByLetter_ShouldReturnComposersList() {
        // given
        char validLetter = 'Z';

        // when
        HttpResponse<ComposersList> returned = underTest.listComposers(validLetter).join();

        // then
        ComposersList body = returned.body();
        assertTrue(body.getStatus().isSuccess());
        assertNotEquals(0, body.getComposers().size());
    }

    @Test
    void getPopularComposers_ShouldReturnComposersList() {
        // when
        HttpResponse<ComposersList> returned = underTest.getPopularComposers().join();

        // then
        ComposersList body = returned.body();
        assertTrue(body.getStatus().isSuccess());
        assertNotEquals(0, body.getComposers().size());
    }

    @Test
    void getEssentialComposers_ShouldReturnComposersList() {
        // when
        HttpResponse<ComposersList> returned = underTest.getEssentialComposers().join();

        // then
        ComposersList body = returned.body();
        assertTrue(body.getStatus().isSuccess());
        assertNotEquals(0, body.getComposers().size());
    }

    @ParameterizedTest
    @MethodSource("epochProvider")
    void listComposersByEpoch_ShouldReturnComposersList(Epoch epoch) {
        // when
        HttpResponse<ComposersList> returned = underTest.listComposers(epoch).join();

        // then
        ComposersList body = returned.body();
        assertTrue(body.getStatus().isSuccess());
        assertNotEquals(0, body.getComposers().size());
    }

    private static Epoch[] epochProvider() {
        return Epoch.values();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "53/.@#?'&"})
    void searchComposersByName_ShouldThrow_WhenSearchStringEmpty(String emptySearch) {
        // given
        Exception thrown = null;

        // when
        try {
            underTest.searchComposers(emptySearch).join();
        } catch (Exception ex) {
            thrown = ex;
        }

        // then
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }

    @ParameterizedTest
    @ValueSource(strings = {"m", "mo", "moz"})
    void searchComposersByName_ShouldThrow_WhenSearchStringTooShort(String tooShortSearch) {
        // given
        Exception thrown = null;

        // when
        try {
            underTest.searchComposers(tooShortSearch).join();
        } catch (Exception ex) {
            thrown = ex;
        }

        // then
        assertInstanceOf(CompletionException.class, thrown);
        assertInstanceOf(OpenOpusErrorException.class, thrown.getCause());
    }

    @Test
    void searchComposersByName_ShouldThrow_WhenNoComposersFound() {
        // given
        String nonexistentName = "vghuailhib";
        Exception thrown = null;

        // when
        try {
            underTest.searchComposers(nonexistentName).join();
        } catch (Exception ex) {
            thrown = ex;
        }

        // then
        assertInstanceOf(CompletionException.class, thrown);
        assertInstanceOf(OpenOpusErrorException.class, thrown.getCause());
    }


    @ParameterizedTest
    @CsvSource({
            "beet, 1",
            "'bach j s', 1",
            "'j s bach', 1"
    })
    void searchComposersByName_ShouldReturnComposersList(String search, int expectedLength) {
        // when
        HttpResponse<ComposersList> returned = underTest.searchComposers(search).join();

        // then
        ComposersList body = returned.body();
        assertTrue(body.getStatus().isSuccess());
        assertEquals(expectedLength, body.getComposers().size());
    }

    @Test
    void listComposersByIds_ShouldThrow_WhenIdLessThanOne() {
        // given
        List<Integer> invalidIds = List.of(34, 0, 123);
        Exception thrown = null;

        // when
        try {
            underTest.listComposers(invalidIds).join();
        } catch (Exception ex) {
            thrown = ex;
        }

        // then
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }

    @Test
    void listComposersByIds_ShouldReturnComposersList() {
        // given
        List<Integer> validIds = List.of(34, 43, 123, 7);

        // when
        HttpResponse<ComposersList> returned = underTest.listComposers(validIds).join();

        // then
        ComposersList body = returned.body();
        assertTrue(body.getStatus().isSuccess());
        assertEquals(4, body.getComposers().size());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void listGenres_ShouldThrow_WhenIdLessThanOne(int invalidId) {
        // given
        Exception thrown = null;

        // when
        try {
            underTest.listGenres(invalidId).join();
        } catch (Exception ex) {
            thrown = ex;
        }

        // then
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }

    @Test
    void listGenres_ShouldReturnGenreList() {
        // given
        int validId = 1;

        // when
        HttpResponse<GenresList> returned = underTest.listGenres(validId).join();

        // then
        GenresList body = returned.body();
        assertTrue(body.getStatus().isSuccess());
        assertNotEquals(0, body.getGenres().size());
        assertNotNull(body.getComposer());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void listWorksByComposerId_ShouldThrow_WhenIdLessThanOne(int invalidId) {
        // given
        Exception thrown = null;

        // when
        try {
            underTest.listWorks(invalidId).join();
        } catch (Exception ex) {
            thrown = ex;
        }

        // then
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }

    @Test
    void listWorksByComposerId_ShouldReturnWorksList() {
        // given
        int validId = 172;

        // when
        HttpResponse<WorksList> returned = underTest.listWorks(validId).join();

        // then
        WorksList body = returned.body();
        assertTrue(body.getStatus().isSuccess());
        assertNotEquals(0, body.getWorks().size());
        assertNotNull(body.getComposer());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void listWorksByComposerIdAndGenre_ShouldThrow_WhenIdLessThanOne(int invalidId) {
        // given
        Exception thrown = null;

        // when
        try {
            underTest.listWorks(invalidId, Genre.ORCHESTRAL).join();
        } catch (Exception ex) {
            thrown = ex;
        }

        // then
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }

    @Test
    void listWorksByComposerIdAndGenre_ShouldReturnWorksListOfGivenGenre() {
        // given
        int validId = 172;
        Genre genre = Genre.ORCHESTRAL;

        // when
        HttpResponse<WorksList> returned = underTest.listWorks(validId, genre).join();

        // then
        WorksList body = returned.body();
        assertTrue(body.getStatus().isSuccess());
        assertNotEquals(0, body.getWorks().size());
        assertNotNull(body.getComposer());
        body.getWorks().forEach(
                work -> assertEquals(genre, work.getGenre())
        );
    }

    @ParameterizedTest
    @CsvSource({
            "aergaehqag, 0",
            "beethoven, -1",
            "'', 45",
            "'  ', 45",
            "'4?&/351', 45"
    })
    void searchWorksByNameAndComposerId_ShouldThrow_WhenSearchStringEmptyOrIdLessThanOne(String search, int composerId) {
        // given
        Exception thrown = null;

        // when
        try {
            underTest.searchWorks(search, composerId).join();
        } catch (Exception ex) {
            thrown = ex;
        }

        // then
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }

    @Test
    void searchWorksByNameAndComposerId_ShouldThrow_WhenNoWorksFound() {
        // given
        String nonexistentWorkTitle = "fivnedgsvuyj";
        int composerId = 172;
        Exception thrown = null;

        // when
        try {
            underTest.searchWorks(nonexistentWorkTitle, composerId).join();
        } catch (Exception ex) {
            thrown = ex;
        }

        // then
        assertInstanceOf(CompletionException.class, thrown);
        assertInstanceOf(OpenOpusErrorException.class, thrown.getCause());
    }

    @Test
    void searchWorksByNameAndComposerId_ShouldReturnWorksList() {
        // given
        String search = "sonata major";
        int composerId = 196;

        // when
        HttpResponse<WorksList> returned = underTest.searchWorks(search, composerId).join();

        // then
        WorksList body = returned.body();
        assertTrue(body.getStatus().isSuccess());
//        assertNotEquals(0, body.getWorks().size());
        assertNotNull(body.getWorks());
        assertNotNull(body.getComposer());
    }

    @ParameterizedTest
    @CsvSource({
            "aergaehqag, 0",
            "beethoven, -1",
            "'', 45",
            "'  ', 45",
            "'4?&/351', 45"
    })
    void searchWorksByNameAndComposerIdAndGenre_ShouldThrow_WhenSearchStringEmptyOrIdLessThanOne(String search, int composerId) {
        // given
        Genre genre = Genre.ORCHESTRAL;
        Exception thrown = null;

        // when
        try {
            underTest.searchWorks(search, composerId, genre).join();
        } catch (Exception ex) {
            thrown = ex;
        }

        // then
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }

    @Test
    void searchWorksByNameAndComposerIdAndGenre_ShouldThrow_WhenNoWorksFound() {
        // given
        String nonexistentWorkTitle = "fivnedgsvuyj";
        int composerId = 172;
        Genre genre = Genre.ORCHESTRAL;
        Exception thrown = null;

        // when
        try {
            underTest.searchWorks(nonexistentWorkTitle, composerId, genre).join();
        } catch (Exception ex) {
            thrown = ex;
        }

        // then
        assertInstanceOf(CompletionException.class, thrown);
        assertInstanceOf(OpenOpusErrorException.class, thrown.getCause());
    }

    @Test
    void searchWorksByNameAndComposerIdAndGenre_ShouldReturnWorksList() {
        // given
        String search = "sonata major";
        int composerId = 196;
        Genre genre = Genre.POPULAR;

        // when
        HttpResponse<WorksList> returned = underTest.searchWorks(search, composerId, genre).join();

        // then
        WorksList body = returned.body();
        assertTrue(body.getStatus().isSuccess());
//        assertNotEquals(0, body.getWorks().size());
        assertNotNull(body.getWorks());
        assertNotNull(body.getComposer());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void getWorkDetails_ShouldThrow_WhenInvalidWorkId(int invalidWorkId) {
        // given
        Exception thrown = null;

        // when
        try {
            underTest.getWorkDetails(invalidWorkId).join();
        } catch (Exception ex) {
            thrown = ex;
        }

        // then
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }

    @Test
    void getWorkDetails_ShouldReturnWorkDetails() {
        // given
        int workId = 8371;

        // when
        HttpResponse<WorkDetailResponse> returned = underTest.getWorkDetails(workId).join();

        // then
        WorkDetailResponse body = returned.body();
        assertTrue(body.getStatus().isSuccess());
        assertEquals(workId, body.getWork().getId());
        assertNotNull(body.getComposer());
    }

    @Test
    void listWorksByIds_ShouldThrow_WhenIdLessThanOne() {
        // given
        List<Integer> invalidIds = List.of(34, 0, 123);
        Exception thrown = null;

        // when
        try {
            underTest.listWorks(invalidIds).join();
        } catch (Exception ex) {
            thrown = ex;
        }

        // then
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }

    @Test
    void listWorksByIds_ShouldReturnWorksList() {
        // given
        List<Integer> validIds = List.of(34, 9655, 123, 7);

        // when
        HttpResponse<ListWorksByIdResponse> returned = underTest.listWorks(validIds).join();

        // then
        ListWorksByIdResponse body = returned.body();
        assertTrue(body.getStatus().isSuccess());
        assertEquals(4, body.getWorks().size());
        body.getWorks().forEach(
                work -> assertTrue(validIds.contains(work.id()))
        );
        assertNotNull(body.getWorksAbstract());
    }

    @Test
    void listRandomWorks_ShouldFilterByGenre() {
        // given
        RandomWorksCriteria criteria = RandomWorksCriteria.builder()
                .setGenre(Genre.CHAMBER)
                .build();

        // when
        HttpResponse<RandomWorks> returned = underTest.listRandomWorks(criteria).join();

        // then
        RandomWorks body = returned.body();
        assertTrue(body.getStatus().isSuccess());
        body.getWorks().forEach(
                work -> assertEquals(Genre.CHAMBER, work.genre())
        );
    }

    @Test
    void listRandomWorks_ShouldFilterByEpoch() {
        // given
        RandomWorksCriteria criteria = RandomWorksCriteria.builder()
                .setEpoch(Epoch.EARLY_ROMANTIC)
                .build();

        // when
        HttpResponse<RandomWorks> returned = underTest.listRandomWorks(criteria).join();

        // then
        RandomWorks body = returned.body();
        assertTrue(body.getStatus().isSuccess());
        body.getWorks().forEach(
                work -> assertEquals(Epoch.EARLY_ROMANTIC, work.composer().epoch())
        );
    }


    @Test
    void listRandomWorks_ShouldFilterByComposers() {
        // given
        List<Integer> composerIds = List.of(32, 89, 12);
        RandomWorksCriteria criteria = RandomWorksCriteria.builder()
                .setComposer(composerIds)
                .build();

        // when
        HttpResponse<RandomWorks> returned = underTest.listRandomWorks(criteria).join();

        // then
        RandomWorks body = returned.body();
        assertTrue(body.getStatus().isSuccess());
        body.getWorks().forEach(
                work -> assertTrue(composerIds.contains(work.composer().id()))
        );
    }

    @Test
    void listRandomWorks_ShouldFilterByWorks() {
        // given
        List<Integer> workIds = List.of(3254, 849, 12);
        RandomWorksCriteria criteria = RandomWorksCriteria.builder()
                .setWork(workIds)
                .build();

        // when
        HttpResponse<RandomWorks> returned = underTest.listRandomWorks(criteria).join();

        // then
        RandomWorks body = returned.body();
        assertTrue(body.getStatus().isSuccess());
        assertEquals(3, body.getWorks().size());
        body.getWorks().forEach(
                work -> assertTrue(workIds.contains(work.id()))
        );
    }

    @ParameterizedTest
    @CsvSource({
            "'', 0",
            "'  ', 0",
            "'53/.@#?'&', 0",
            "beethoven, -1",
            "beethoven, -2"
    })
    void search_ShouldThrow_WhenSearchStringEmptyOrOffsetLessThanZero(String search, int offset) {
        // given
        Exception thrown = null;

        // when
        try {
            underTest.search(search, offset);
        } catch (Exception e) {
            thrown = e;
        }

        // then
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }

    @ParameterizedTest
    @CsvSource({
            "'chopin mazurka', 0",
            "'bee', 25",
            "'mozart sonata', 10"
    })
    void search_ShouldFindWorksAndComposers(String search, int offset) {
        // when
        HttpResponse<OmnisearchResponse> returned = underTest.search(search, offset).join();

        // then
        OmnisearchResponse body = returned.body();
        assertTrue(body.getStatus().isSuccess());
        assertNotEquals(0, body.getResults().size());
    }

    @Test
    void listPerformerRoles_ShouldThrow_WhenPerformerListEmpty() {
        // given
        Exception thrown = null;

        // when
        try {
            underTest.listPerformerRoles(Collections.emptyList());
        } catch (Exception e) {
            thrown = e;
        }

        // then
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }

    @ParameterizedTest
    @MethodSource("performersProvider")
    void listPerformerRoles_ShouldReturnPerformers_WhenDataValid(List<String> performers) {
        // when
        HttpResponse<PerformerRolesResponse> returned = underTest.listPerformerRoles(performers).join();

        // then
        PerformerRolesResponse body = returned.body();
        assertTrue(body.getStatus().isSuccess());
        assertEquals(performers.size(), body.getPerformers().readable().size());
        assertEquals(performers.size(), body.getPerformers().digest().size());
        List<String> sanitizedPerformers = performers.stream()
                .map(p -> new StringSanitizer().sanitize(p))
                .toList();
        assertTrue(body.getPerformers().readable().stream()
                .map(PerformerRole::name)
                .toList()
                .containsAll(sanitizedPerformers)
        );
    }

    private static Stream<List<String>> performersProvider() {
        return Stream.of(
                List.of("   alfred   brendel   "),
                List.of("   alfred   brendel   ", "herbert von karajan"),
                List.of("   alfred   brendel   ", "herbert von karajan", "eouhf896")
        );
    }
}
