package dev.jlynx.openopusjava;

import dev.jlynx.openopusjava.internal.json.JsonBodyHandler;
import dev.jlynx.openopusjava.exception.OpenOpusErrorException;
import dev.jlynx.openopusjava.internal.util.SpaceEncoder;
import dev.jlynx.openopusjava.internal.util.StringSanitizer;
import dev.jlynx.openopusjava.request.RandomWorksCriteria;
import dev.jlynx.openopusjava.response.body.*;
import dev.jlynx.openopusjava.response.subtype.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class OpenOpusClientTest {

    private static final String BASE_URL = "https://api.openopus.org";

    @Mock
    private HttpClient httpClientMock;
    @Spy
    private SpaceEncoder spaceEncoderSpy;
    @Mock
    private StringSanitizer sanitizerMock;

    @Captor
    private ArgumentCaptor<HttpRequest> httpRequestCaptor;

    private OpenOpusClient underTest;


    @BeforeEach
    void setUp() throws ClassNotFoundException {
        this.underTest = new OpenOpusClient(httpClientMock, spaceEncoderSpy, sanitizerMock);
    }

    @AfterEach
    void tearDown() {
        httpClientMock.close();
    }

    @TestFactory
    Stream<DynamicTest> openOpusClient_ShouldThrow_WhenStatusErrorReturned() {
        List<Supplier<CompletableFuture<?>>> testedMethods = new ArrayList<>();
        testedMethods.add(() -> underTest.listComposers('a'));
        testedMethods.add(() -> underTest.getPopularComposers());
        testedMethods.add(() -> underTest.getEssentialComposers());
        testedMethods.add(() -> underTest.listComposers(Epoch.BAROQUE));
        testedMethods.add(() -> underTest.searchComposers("noresultsname"));
        testedMethods.add(() -> underTest.listComposers(List.of(44, 7, 16)));
        testedMethods.add(() -> underTest.listGenres(16));
        testedMethods.add(() -> underTest.listWorks(16));
        testedMethods.add(() -> underTest.listWorks(16, Genre.POPULAR));
        testedMethods.add(() -> underTest.listWorks(16, Genre.ORCHESTRAL));
        testedMethods.add(() -> underTest.searchWorks("Sonata", 16));
        testedMethods.add(() -> underTest.searchWorks("Violin sonata", 16, Genre.CHAMBER));
        testedMethods.add(() -> underTest.getWorkDetails(482));
        testedMethods.add(() -> underTest.listWorks(List.of(16, 15829, 9472)));
        testedMethods.add(() -> underTest.listRandomWorks(RandomWorksCriteria.builder().build()));
        testedMethods.add(() -> underTest.search("title", 0));
        testedMethods.add(() -> underTest.listPerformerRoles(List.of("herbert von karajan", "alfred brendel")));

        return testedMethods.stream()
                .map(testedMethod -> DynamicTest.dynamicTest("openOpusClient_ShouldThrow_WhenStatusErrorReturned", () -> {
                    // GIVEN
                    CompletableFuture<HttpResponse<ComposersList>> expectedRes = errorResponse();
                    doReturn(expectedRes).when(httpClientMock).sendAsync(any(), any());
                    given(sanitizerMock.sanitize(anyString())).willReturn("sanitizedString");
                    Exception thrown = null;

                    // WHEN
                    try {
                        testedMethod.get().join();
                    } catch (Exception ex) {
                        thrown = ex;
                    }

                    // THEN
                    assertNotNull(thrown);
                    assertInstanceOf(OpenOpusErrorException.class, thrown.getCause());
                    assertTrue(expectedRes.join().body().getStatus().getError().isPresent());
                    assertTrue(thrown.getMessage().contains(expectedRes.join().body().getStatus().getError().get()));

                    reset(httpClientMock);
                }));
    }


    @ParameterizedTest
    @ValueSource(chars = {'3', '@', ':', '[', '?', '/', '\\'})
    void getComposersByLetter_ShouldThrow_WhenInvalidCharPassed(char invalidChar) {
        // GIVEN
        Exception thrown = null;

        // WHEN
        try {
            underTest.listComposers(invalidChar);
        } catch (Exception ex) {
            thrown = ex;
        }

        // THEN
        assertNotNull(thrown);
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }

//    @Test
//    void getComposersByLetter_ShouldThrow_WhenStatusErrorReturned() {
//        // GIVEN
//        CompletableFuture<HttpResponse<ComposersList>> expectedResponse = errorResponse();
//        doReturn(expectedResponse).when(httpClientMock).sendAsync(any(), any());
//        Exception thrown = null;
//
//        // WHEN
//        try {
//            underTest.getComposersByLetter('a').join();
//        } catch (Exception ex) {
//            thrown = ex;
//        }
//
//        // THEN
//        assertNotNull(thrown);
//        assertInstanceOf(OpenOpusErrorException.class, thrown.getCause());
//        assertTrue(expectedResponse.join().body().getStatus().getError().isPresent());
//        assertTrue(thrown.getMessage().contains(expectedResponse.join().body().getStatus().getError().get()));
//    }

    @ParameterizedTest
    @ValueSource(chars = {'a', 'A', 'y', 'Y', 'b'})
    void getComposersByLetter_ShouldPassParams_WhenLetterCharPassed(char letter) {
        // GIVEN
        CompletableFuture<HttpResponse<ComposersList>> expectedResponse = composersListResponse();
        doReturn(expectedResponse).when(httpClientMock).sendAsync(any(), any());

        // WHEN
        CompletableFuture<HttpResponse<ComposersList>> returned = underTest.listComposers(letter);

        // THEN
        then(httpClientMock).should().sendAsync(httpRequestCaptor.capture(), any(JsonBodyHandler.class));

        HttpRequest requestCapture = httpRequestCaptor.getValue();
        String expectedUri = BASE_URL + "/composer/list/name/" + letter + ".json";
        assertEquals(expectedUri, requestCapture.uri().toString());
        assertEquals("GET", requestCapture.method());

        assertEquals(expectedResponse.join().body(), returned.join().body());
    }

//    @Test
//    void getComposersPopular_ShouldThrow_WhenStatusErrorReturned() {
//        // GIVEN
//        CompletableFuture<HttpResponse<ComposersList>> expectedResponse = errorResponse();
//        doReturn(expectedResponse).when(httpClientMock).sendAsync(any(), any());
//        Exception thrown = null;
//
//        // WHEN
//        try {
//            underTest.getComposersPopular().join();
//        } catch (Exception ex) {
//            thrown = ex;
//        }
//
//        // THEN
//        assertNotNull(thrown);
//        assertInstanceOf(OpenOpusErrorException.class, thrown.getCause());
//        assertTrue(expectedResponse.join().body().getStatus().getError().isPresent());
//        assertTrue(thrown.getMessage().contains(expectedResponse.join().body().getStatus().getError().get()));
//    }

    @Test
    void getPopular_Composers_ShouldPassParams() {
        // GIVEN
        CompletableFuture<HttpResponse<ComposersList>> expectedResponse = composersListResponse();
        doReturn(expectedResponse).when(httpClientMock).sendAsync(any(), any());

        // WHEN
        CompletableFuture<HttpResponse<ComposersList>> returned = underTest.getPopularComposers();

        // THEN
        then(httpClientMock).should().sendAsync(httpRequestCaptor.capture(), any(JsonBodyHandler.class));

        HttpRequest requestCapture = httpRequestCaptor.getValue();
        String expectedUri = BASE_URL + "/composer/list/pop.json";
        assertEquals(expectedUri, requestCapture.uri().toString());
        assertEquals("GET", requestCapture.method());

        assertEquals(expectedResponse.join().body(), returned.join().body());
    }

//    @Test
//    void getComposersEssential_ShouldThrow_WhenStatusErrorReturned() {
//        // GIVEN
//        CompletableFuture<HttpResponse<ComposersList>> expectedResponse = errorResponse();
//        doReturn(expectedResponse).when(httpClientMock).sendAsync(any(), any());
//        Exception thrown = null;
//
//        // WHEN
//        try {
//            underTest.getComposersEssential().join();
//        } catch (Exception ex) {
//            thrown = ex;
//        }
//
//        // THEN
//        assertNotNull(thrown);
//        assertInstanceOf(OpenOpusErrorException.class, thrown.getCause());
//        assertTrue(expectedResponse.join().body().getStatus().getError().isPresent());
//        assertTrue(thrown.getMessage().contains(expectedResponse.join().body().getStatus().getError().get()));
//    }

    @Test
    void getEssential_Composers_ShouldPassParams() {
        // GIVEN
        CompletableFuture<HttpResponse<ComposersList>> expectedResponse = composersListResponse();
        doReturn(expectedResponse).when(httpClientMock).sendAsync(any(), any());

        // WHEN
        CompletableFuture<HttpResponse<ComposersList>> returned = underTest.getEssentialComposers();

        // THEN
        then(httpClientMock).should().sendAsync(httpRequestCaptor.capture(), any(JsonBodyHandler.class));

        HttpRequest requestCapture = httpRequestCaptor.getValue();
        String expectedUri = BASE_URL + "/composer/list/rec.json";
        assertEquals(expectedUri, requestCapture.uri().toString());
        assertEquals("GET", requestCapture.method());

        assertEquals(expectedResponse.join().body(), returned.join().body());
    }

//    @Test
//    void getComposersByEpoch_ShouldThrow_WhenStatusErrorReturned() {
//        // GIVEN
//        CompletableFuture<HttpResponse<ComposersList>> expectedResponse = errorResponse();
//        doReturn(expectedResponse).when(httpClientMock).sendAsync(any(), any());
//        Exception thrown = null;
//
//        // WHEN
//        try {
//            underTest.getComposersByEpoch(Epoch.BAROQUE).join();
//        } catch (Exception ex) {
//            thrown = ex;
//        }
//
//        // THEN
//        assertNotNull(thrown);
//        assertInstanceOf(OpenOpusErrorException.class, thrown.getCause());
//        assertTrue(expectedResponse.join().body().getStatus().getError().isPresent());
//        assertTrue(thrown.getMessage().contains(expectedResponse.join().body().getStatus().getError().get()));
//    }

    @ParameterizedTest
    @MethodSource("epochProvider")
    void getComposersByEpoch_ShouldPassParams(Epoch epoch) {
        // GIVEN
        CompletableFuture<HttpResponse<ComposersList>> expectedResponse = composersListResponse();
        doReturn(expectedResponse).when(httpClientMock).sendAsync(any(), any());

        // WHEN
        CompletableFuture<HttpResponse<ComposersList>> returned = underTest.listComposers(epoch);

        // THEN
        then(httpClientMock).should().sendAsync(httpRequestCaptor.capture(), any(JsonBodyHandler.class));

        HttpRequest requestCapture = httpRequestCaptor.getValue();
        String expectedUri = BASE_URL + "/composer/list/epoch/" + epoch.getValue() + ".json";
        expectedUri = expectedUri.replaceAll(" ", "%20");
        assertEquals(expectedUri, requestCapture.uri().toString());
        assertEquals("GET", requestCapture.method());

        assertEquals(expectedResponse.join().body(), returned.join().body());
    }

//    @Test
//    void searchComposersByName_ShouldThrow_WhenStatusErrorReturned() {
//        // GIVEN
//        CompletableFuture<HttpResponse<ComposersList>> expectedResponse = errorResponse();
//        doReturn(expectedResponse).when(httpClientMock).sendAsync(any(), any());
//        given(sanitizerMock.sanitize(anyString())).willReturn("noresultsname");
//        Exception thrown = null;
//
//        // WHEN
//        try {
//            underTest.searchComposersByName("noresultsname").join();
//        } catch (Exception ex) {
//            thrown = ex;
//        }
//
//        // THEN
//        assertNotNull(thrown);
//        assertInstanceOf(OpenOpusErrorException.class, thrown.getCause());
//        assertTrue(expectedResponse.join().body().getStatus().getError().isPresent());
//        assertTrue(thrown.getMessage().contains(expectedResponse.join().body().getStatus().getError().get()));
//    }

    @Test
    void searchComposers_ShouldThrow_WhenSearchStringEmptyAfterSanitizing() {
        // GIVEN
        Exception thrown = null;
        given(sanitizerMock.sanitize(any())).willReturn("");

        // WHEN
        try {
            underTest.searchComposers("hello").join();
        } catch (Exception ex) {
            thrown = ex;
        }

        // THEN
        assertNotNull(thrown);
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }

    @Test
    void searchComposers_ShouldSanitizeSearchStringAndPassIt() {
        // GIVEN
        String searchString = "beeth4";
        String sanitizedString = "sanitized";
        CompletableFuture<HttpResponse<ComposersList>> expectedResponse = composersListResponse();
        doReturn(expectedResponse).when(httpClientMock).sendAsync(any(), any());
        given(sanitizerMock.sanitize(anyString())).willReturn(sanitizedString);

        // WHEN
        CompletableFuture<HttpResponse<ComposersList>> returned = underTest.searchComposers(searchString);

        // THEN
        InOrder inOrder = inOrder(sanitizerMock, httpClientMock);
        then(sanitizerMock).should(inOrder).sanitize(searchString);
        then(httpClientMock).should(inOrder).sendAsync(httpRequestCaptor.capture(), any(JsonBodyHandler.class));

        HttpRequest requestCapture = httpRequestCaptor.getValue();
        String expectedUri = BASE_URL + "/composer/list/search/" + sanitizedString + ".json";
        expectedUri = expectedUri.replaceAll(" ", "%20");
        assertEquals(expectedUri, requestCapture.uri().toString());
        assertEquals("GET", requestCapture.method());

        assertEquals(expectedResponse.join().body(), returned.join().body());
    }

//    @Test
//    void listComposersById_ShouldThrow_WhenStatusErrorReturned() {
//        // GIVEN
//        CompletableFuture<HttpResponse<ComposersList>> expectedResponse = errorResponse();
//        doReturn(expectedResponse).when(httpClientMock).sendAsync(any(), any());
//        Exception thrown = null;
//
//        // WHEN
//        try {
//            underTest.listComposersById(List.of(44, 7, 16)).join();
//        } catch (Exception ex) {
//            thrown = ex;
//        }
//
//        // THEN
//        assertNotNull(thrown);
//        assertInstanceOf(OpenOpusErrorException.class, thrown.getCause());
//        assertTrue(expectedResponse.join().body().getStatus().getError().isPresent());
//        assertTrue(thrown.getMessage().contains(expectedResponse.join().body().getStatus().getError().get()));
//    }

    @ParameterizedTest
    @MethodSource("invalidIdListProvider")
    void listComposersById_ShouldThrow_WhenIdsLessThanOne(List<Integer> invalidIds) {
        // GIVEN
        Exception thrown = null;

        // WHEN
        try {
            underTest.listComposers(invalidIds).join();
        } catch (Exception ex) {
            thrown = ex;
        }

        // THEN
        assertNotNull(thrown);
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }


    @Test
    void listComposersById_ShouldPassParams_WhenIdsValid() {
        // GIVEN
        CompletableFuture<HttpResponse<ComposersList>> expectedResponse = composersListResponse();
        doReturn(expectedResponse).when(httpClientMock).sendAsync(any(), any());

        // WHEN
        CompletableFuture<HttpResponse<ComposersList>> returned = underTest.listComposers(List.of(44, 7, 16));

        // THEN
        then(httpClientMock).should().sendAsync(httpRequestCaptor.capture(), any(JsonBodyHandler.class));

        HttpRequest requestCapture = httpRequestCaptor.getValue();
        String expectedUri = BASE_URL + "/composer/list/ids/44,7,16.json";
        assertEquals(expectedUri, requestCapture.uri().toString());
        assertEquals("GET", requestCapture.method());

        assertEquals(expectedResponse.join().body(), returned.join().body());
    }

//    @Test
//    void listGenresByComposer_ShouldThrow_WhenStatusErrorReturned() {
//        // GIVEN
//        CompletableFuture<HttpResponse<ComposersList>> expectedResponse = errorResponse();
//        doReturn(expectedResponse).when(httpClientMock).sendAsync(any(), any());
//        Exception thrown = null;
//
//        // WHEN
//        try {
//            underTest.listGenresByComposer(16).join();
//        } catch (Exception ex) {
//            thrown = ex;
//        }
//
//        // THEN
//        assertNotNull(thrown);
//        assertInstanceOf(OpenOpusErrorException.class, thrown.getCause());
//        assertTrue(expectedResponse.join().body().getStatus().getError().isPresent());
//        assertTrue(thrown.getMessage().contains(expectedResponse.join().body().getStatus().getError().get()));
//    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -2})
    void listGenresByComposer_ShouldThrow_WhenIdsLessThanOne(int invalidId) {
        // GIVEN
        Exception thrown = null;

        // WHEN
        try {
            underTest.listGenres(invalidId).join();
        } catch (Exception ex) {
            thrown = ex;
        }

        // THEN
        assertNotNull(thrown);
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }


    @Test
    void listGenresByComposer_ShouldPassParams_WhenIdValid() {
        // GIVEN
        CompletableFuture<HttpResponse<GenresList>> expectedResponse = genresListResponse();
        doReturn(expectedResponse).when(httpClientMock).sendAsync(any(), any());

        // WHEN
        CompletableFuture<HttpResponse<GenresList>> returned = underTest.listGenres(16);

        // THEN
        then(httpClientMock).should().sendAsync(httpRequestCaptor.capture(), any(JsonBodyHandler.class));

        HttpRequest requestCapture = httpRequestCaptor.getValue();
        String expectedUri = BASE_URL + "/genre/list/composer/16.json";
        assertEquals(expectedUri, requestCapture.uri().toString());
        assertEquals("GET", requestCapture.method());

        assertEquals(expectedResponse.join().body().getClass(), returned.join().body().getClass());
        assertEquals(expectedResponse.join().body(), returned.join().body());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    void listWorksByComposerId_ShouldThrow_WhenComposerIdIsLessThanOne(int invalidComposerId) {
        // GIVEN
        Exception thrown = null;

        // WHEN
        try {
            underTest.listWorks(invalidComposerId).join();
        } catch (Exception ex) {
            thrown = ex;
        }

        // THEN
        assertNotNull(thrown);
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }
    
    @ParameterizedTest
    @ValueSource(ints = {1, 16})
    void listWorksByComposerId_ShouldPassParams_WhenValidComposerIdPassed(int composerId) {
        // GIVEN
        CompletableFuture<HttpResponse<WorksList>> expectedResponse = worksListResponse();
        doReturn(expectedResponse).when(httpClientMock).sendAsync(any(), any());

        // WHEN
        CompletableFuture<HttpResponse<WorksList>> returned = underTest.listWorks(composerId);

        // THEN
        then(httpClientMock).should().sendAsync(httpRequestCaptor.capture(), any(JsonBodyHandler.class));

        HttpRequest requestCapture = httpRequestCaptor.getValue();
        String expectedUri = BASE_URL + "/work/list/composer/%d/genre/all.json".formatted(composerId);
        assertEquals(expectedUri, requestCapture.uri().toString());
        assertEquals("GET", requestCapture.method());

        assertEquals(expectedResponse.join().body(), returned.join().body());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    void listWorksByComposerIdAndGenre_ShouldThrow_WhenComposerIdIsLessThanOne(int invalidComposerId) {
        // GIVEN
        Exception thrown = null;

        // WHEN
        try {
            underTest.listWorks(invalidComposerId, Genre.CHAMBER).join();
        } catch (Exception ex) {
            thrown = ex;
        }

        // THEN
        assertNotNull(thrown);
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }

    @ParameterizedTest
    @MethodSource("validComposerIdAndGenreProvider")
    void listWorksByComposerIdAndGenre_ShouldPassParams_WhenValidComposerIdPassed(int composerId, Genre genre) {
        // GIVEN
        CompletableFuture<HttpResponse<WorksList>> expectedResponse = worksListResponse();
        doReturn(expectedResponse).when(httpClientMock).sendAsync(any(), any());

        // WHEN
        CompletableFuture<HttpResponse<WorksList>> returned = underTest.listWorks(composerId, genre);

        // THEN
        then(httpClientMock).should().sendAsync(httpRequestCaptor.capture(), any(JsonBodyHandler.class));

        HttpRequest requestCapture = httpRequestCaptor.getValue();
        String expectedUri = BASE_URL + "/work/list/composer/%d/genre/%s.json".formatted(composerId, genre.getValue());
        expectedUri = expectedUri.replaceAll(" ", "%20");
        assertEquals(expectedUri, requestCapture.uri().toString());
        assertEquals("GET", requestCapture.method());

        assertEquals(expectedResponse.join().body(), returned.join().body());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    void searchWorksByTitleAndComposerId_ShouldThrow_WhenComposerIdIsLessThanOne(int invalidComposerId) {
        // GIVEN
        Exception thrown = null;

        // WHEN
        try {
            underTest.searchWorks("sonata", invalidComposerId).join();
        } catch (Exception ex) {
            thrown = ex;
        }

        // THEN
        assertNotNull(thrown);
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }

    @Test
    void searchWorksByTitleAndComposerId_ShouldThrow_WhenSearchStringEmptyAfterSanitizing() {
        // GIVEN
        String searchString = "37$32!";
        int composerId = 16;
        Exception thrown = null;
        given(sanitizerMock.sanitize(anyString())).willReturn("");

        // WHEN
        try {
            underTest.searchWorks(searchString, composerId).join();
        } catch (Exception ex) {
            thrown = ex;
        }

        // THEN
        then(sanitizerMock).should().sanitize(searchString);
        assertNotNull(thrown);
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void searchWorksByTitleAndComposerId_ShouldPassParams_whenValid(int composerId) {
        // GIVEN
        String searchString = "searched string";
        String sanitizedString = "sanitized string";
        CompletableFuture<HttpResponse<WorksList>> expectedResponse = worksListResponse();
        doReturn(expectedResponse).when(httpClientMock).sendAsync(any(), any());
        given(sanitizerMock.sanitize(anyString())).willReturn(sanitizedString);

        // WHEN
        CompletableFuture<HttpResponse<WorksList>> returned = underTest.searchWorks(searchString, composerId);

        // THEN
        InOrder inOrder = inOrder(sanitizerMock, httpClientMock);
        then(sanitizerMock).should(inOrder).sanitize(searchString);
        then(httpClientMock).should(inOrder).sendAsync(httpRequestCaptor.capture(), any(JsonBodyHandler.class));

        HttpRequest requestCapture = httpRequestCaptor.getValue();
        String expectedUri = BASE_URL + "/work/list/composer/%d/genre/all/search/%s.json".formatted(composerId, sanitizedString);
        expectedUri = expectedUri.replaceAll(" ", "%20");
        assertEquals(expectedUri, requestCapture.uri().toString());
        assertEquals("GET", requestCapture.method());

        assertEquals(expectedResponse.join().body(), returned.join().body());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    void searchWorksByTitleAndComposerIdAndGenre_ShouldThrow_WhenComposerIdIsLessThanOne(int invalidComposerId) {
        // GIVEN
        Exception thrown = null;

        // WHEN
        try {
            underTest.searchWorks("sonata", invalidComposerId, Genre.ORCHESTRAL).join();
        } catch (Exception ex) {
            thrown = ex;
        }

        // THEN
        assertNotNull(thrown);
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }

    @Test
    void searchWorksByTitleAndComposerIdAndGenre_ShouldThrow_WhenSearchStringEmptyAfterSanitizing() {
        // GIVEN
        String searchString = "37$32!";
        int composerId = 16;
        Exception thrown = null;
        given(sanitizerMock.sanitize(anyString())).willReturn("");

        // WHEN
        try {
            underTest.searchWorks(searchString, composerId, Genre.ORCHESTRAL).join();
        } catch (Exception ex) {
            thrown = ex;
        }

        // THEN
        then(sanitizerMock).should().sanitize(searchString);
        assertNotNull(thrown);
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void searchWorksByTitleAndComposerIdAndGenre_ShouldPassParams_whenValid(int composerId) {
        // GIVEN
        String searchString = "searched string";
        String sanitizedString = "sanitized string";
        Genre genre = Genre.CHAMBER;
        CompletableFuture<HttpResponse<WorksList>> expectedResponse = worksListResponse();
        doReturn(expectedResponse).when(httpClientMock).sendAsync(any(), any());
        given(sanitizerMock.sanitize(anyString())).willReturn(sanitizedString);

        // WHEN
        CompletableFuture<HttpResponse<WorksList>> returned = underTest.searchWorks(searchString, composerId, Genre.CHAMBER);

        // THEN
        InOrder inOrder = inOrder(sanitizerMock, httpClientMock);
        then(sanitizerMock).should(inOrder).sanitize(searchString);
        then(httpClientMock).should(inOrder).sendAsync(httpRequestCaptor.capture(), any(JsonBodyHandler.class));

        HttpRequest requestCapture = httpRequestCaptor.getValue();
        String expectedUri = BASE_URL + "/work/list/composer/%d/genre/%s/search/%s.json"
                .formatted(composerId, genre.getValue(), sanitizedString);
        expectedUri = expectedUri.replaceAll(" ", "%20");
        assertEquals(expectedUri, requestCapture.uri().toString());
        assertEquals("GET", requestCapture.method());

        assertEquals(expectedResponse.join().body(), returned.join().body());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    void getWorkDetails_ShouldThrow_WhenWorkIdIsLessThanOne(int invalidWorkId) {
        // GIVEN
        Exception thrown = null;

        // WHEN
        try {
            underTest.getWorkDetails(invalidWorkId).join();
        } catch (Exception ex) {
            thrown = ex;
        }

        // THEN
        assertNotNull(thrown);
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 16})
    void getWorkDetails_ShouldPassParams_WhenValid(int workId) {
        // GIVEN
        CompletableFuture<HttpResponse<WorkDetailResponse>> expectedResponse = workDetailResponse();
        doReturn(expectedResponse).when(httpClientMock).sendAsync(any(), any());

        // WHEN
        CompletableFuture<HttpResponse<WorkDetailResponse>> returned = underTest.getWorkDetails(workId);

        // THEN
        then(httpClientMock).should().sendAsync(httpRequestCaptor.capture(), any(JsonBodyHandler.class));

        HttpRequest requestCapture = httpRequestCaptor.getValue();
        String expectedUri = BASE_URL + "/work/detail/%d.json".formatted(workId);
        assertEquals(expectedUri, requestCapture.uri().toString());
        assertEquals("GET", requestCapture.method());

        assertEquals(expectedResponse.join().body(), returned.join().body());
    }

    @ParameterizedTest
    @MethodSource("invalidIdListProvider")
    void listWorksById_ShouldThrow_WhenWorkIdIsLessThanOne(List<Integer> invalidIds) {
        // GIVEN
        Exception thrown = null;

        // WHEN
        try {
            underTest.listWorks(invalidIds).join();
        } catch (Exception ex) {
            thrown = ex;
        }

        // THEN
        assertNotNull(thrown);
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }

    @Test
    void listWorksById_ShouldPassParams_WhenValid() {
        // GIVEN
        List<Integer> validWorkIds = List.of(1, 16);
        CompletableFuture<HttpResponse<ListWorksByIdResponse>> expectedResponse = listWorksByIdResponse();
        doReturn(expectedResponse).when(httpClientMock).sendAsync(any(), any());

        // WHEN
        CompletableFuture<HttpResponse<ListWorksByIdResponse>> returned = underTest.listWorks(validWorkIds);

        // THEN
        then(httpClientMock).should().sendAsync(httpRequestCaptor.capture(), any(JsonBodyHandler.class));

        HttpRequest requestCapture = httpRequestCaptor.getValue();
        String expectedUri = BASE_URL + "/work/list/ids/1,16.json";
        assertEquals(expectedUri, requestCapture.uri().toString());
        assertEquals("GET", requestCapture.method());

        assertEquals(expectedResponse.join().body(), returned.join().body());
    }

    @ParameterizedTest
    @MethodSource("criteriaProvider")
    void listRandomWorks_ShouldCallProperUrl(RandomWorksCriteria criteria, String expectedUrlParams) {
        // GIVEN
        CompletableFuture<HttpResponse<RandomWorks>> expectedResponse = randomWorksResponse();
        doReturn(expectedResponse).when(httpClientMock).sendAsync(any(), any());

        // WHEN
        CompletableFuture<HttpResponse<RandomWorks>> returned = underTest.listRandomWorks(criteria);

        // THEN
        then(httpClientMock).should().sendAsync(httpRequestCaptor.capture(), any(JsonBodyHandler.class));

        HttpRequest requestCapture = httpRequestCaptor.getValue();
        String expectedUri = BASE_URL + "/dyn/work/random" + expectedUrlParams;
        assertEquals(expectedUri, requestCapture.uri().toString());
        assertEquals("GET", requestCapture.method());
        assertEquals(expectedResponse.join().body(), returned.join().body());
    }

    @Test
    void search_ShouldThrow_WhenOffsetLessThanZero() {
        // GIVEN
        Exception thrown = null;

        // WHEN
        try {
            underTest.search("something", -1);
        } catch (Exception ex) {
            thrown = ex;
        }

        // THEN
        assertNotNull(thrown);
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }


    @Test
    void search_ShouldCallProperUrl() {
        // GIVEN
        String search = "some 4title";
        int offset = 0;
        String sanitizedSearch = "some%20title";
        given(sanitizerMock.sanitize(search)).willReturn(sanitizedSearch);
        given(httpClientMock.sendAsync(any(), any())).willReturn(CompletableFuture.completedFuture(null));

        // WHEN
        underTest.search(search, offset);

        // THEN
        then(sanitizerMock).should().sanitize(search);
        then(httpClientMock).should(times(1)).sendAsync(httpRequestCaptor.capture(), any(JsonBodyHandler.class));

        String expectedUri = BASE_URL + "/omnisearch/%s/%d.json".formatted(sanitizedSearch, offset);
        assertEquals(expectedUri, httpRequestCaptor.getValue().uri().toString());
        assertEquals("GET", httpRequestCaptor.getValue().method());
    }

    @Test
    void listPerformerRoles_ShouldThrow_WhenNamesListIsEmpty() {
        // GIVEN
        Exception thrown = null;

        // WHEN
        try {
            underTest.listPerformerRoles(Collections.emptyList());
        } catch (Exception ex) {
            thrown = ex;
        }

        // THEN
        assertNotNull(thrown);
        assertInstanceOf(IllegalArgumentException.class, thrown);
    }

    @Test
    void listPerformerRoles_ShouldCallProperUrl() {
        // GIVEN
        List<String> performers = List.of("herbert von karajan", "alfred brendel");
        String sanitizedName = "sanitized name";
        given(httpClientMock.sendAsync(any(), any())).willReturn(CompletableFuture.completedFuture(null));
        given(sanitizerMock.sanitize(anyString())).willReturn(sanitizedName);

        // WHEN
        underTest.listPerformerRoles(performers);

        // THEN
        then(httpClientMock).should(times(1)).sendAsync(httpRequestCaptor.capture(), any(JsonBodyHandler.class));
        String expectedUri = BASE_URL +  "/dyn/performer/list?names=%5B%22sanitized+name%22%2C%22sanitized+name%22%5D";
        assertEquals(expectedUri, httpRequestCaptor.getValue().uri().toString());
        assertEquals("GET", httpRequestCaptor.getValue().method());
    }


    private static CompletableFuture<HttpResponse<ComposersList>> composersListResponse() {
        var status = new OpenOpusResponse.OpenOpusResponseStatus("1.0", true, null, "db", 5, 0.123, "api");
        var metadata = new OpenOpusResponse.OpenOpusRequestMetadata("type", "item", null, null);
        ComposersList body = new ComposersList(status, metadata, new ArrayList<>());
        HttpResponse<ComposersList> mockResponse = mock(HttpResponse.class);
        given(mockResponse.body()).willReturn(body);
        return CompletableFuture.completedFuture(mockResponse);
    }

    private static CompletableFuture<HttpResponse<GenresList>> genresListResponse() {
        var status = new OpenOpusResponse.OpenOpusResponseStatus("1.0", true, null, "db", 5, 0.123, "api");
        var metadata = new OpenOpusResponse.OpenOpusRequestMetadata("type", "item", null, null);
        Composer composer = new Composer(1, "Surname", "Full name", LocalDate.of(1854, 6, 12), LocalDate.of(1894, 6, 12), Epoch.LATE_ROMANTIC, "portraituri");
        GenresList body = new GenresList(status, metadata, composer, Arrays.asList(Genre.values()));
        HttpResponse<GenresList> mockResponse = mock(HttpResponse.class);
        given(mockResponse.body()).willReturn(body);
        return CompletableFuture.completedFuture(mockResponse);
    }

    private static CompletableFuture<HttpResponse<WorksList>> worksListResponse() {
        var status = new OpenOpusResponse.OpenOpusResponseStatus("1.0", true, null, "db", 5, 0.123, "api");
        var metadata = new OpenOpusResponse.OpenOpusRequestMetadata("type", "item", null, null);
        Composer composer = new Composer(1, "Surname", "Full name", LocalDate.of(1854, 6, 12), LocalDate.of(1894, 6, 12), Epoch.LATE_ROMANTIC, "portraituri");
        WorksList body = new WorksList(status, metadata, composer, new ArrayList<>());
        HttpResponse<WorksList> mockResponse = mock(HttpResponse.class);
        given(mockResponse.body()).willReturn(body);
        return CompletableFuture.completedFuture(mockResponse);
    }

    private static CompletableFuture<HttpResponse<WorkDetailResponse>> workDetailResponse() {
        var status = new OpenOpusResponse.OpenOpusResponseStatus("1.0", true, null, "db", 5, 0.123, "api");
        ComposerOverview composer = new ComposerOverview(1, "Surname", "Full name", Epoch.LATE_ROMANTIC);
        WorkDetail workDetail = new WorkDetail(5343, "title", "", Genre.CHAMBER, "search", new ArrayList<>(), null, null);
        WorkDetailResponse body = new WorkDetailResponse(status, null, composer, workDetail, null);
        HttpResponse<WorkDetailResponse> mockResponse = mock(HttpResponse.class);
        given(mockResponse.body()).willReturn(body);
        return CompletableFuture.completedFuture(mockResponse);
    }

    private static CompletableFuture<HttpResponse<ListWorksByIdResponse>> listWorksByIdResponse() {
        var status = new OpenOpusResponse.OpenOpusResponseStatus("1.0", true, null, "db", 5, 0.123, "api");
        var metadata = new OpenOpusResponse.OpenOpusRequestMetadata("type", "item", null, null);
        HashMap<String, WorkSummary> works = new HashMap<>();
        Composer composer = new Composer(1, "Surname", "Full name", LocalDate.of(1854, 6, 12), LocalDate.of(1894, 6, 12), Epoch.LATE_ROMANTIC, "portraituri");
        works.put("w:132", new WorkSummary(132, "title1", "", Genre.KEYBOARD, "1", "0", composer));
        works.put("w:534", new WorkSummary(534, "title2", "", Genre.ORCHESTRAL, "0", "0", composer));
        ListWorksByIdAbstract workComposerAbstract = new ListWorksByIdAbstract(
                new ListWorksByIdAbstract.ComposersAbstract(List.of("uri1", "uri2"), List.of("composer1", "composer2"), 2),
                new ListWorksByIdAbstract.WorksAbstract(2)
        );
        ListWorksByIdResponse body = new ListWorksByIdResponse(status, metadata, works, workComposerAbstract);
        HttpResponse<ListWorksByIdResponse> mockResponse = mock(HttpResponse.class);
        given(mockResponse.body()).willReturn(body);
        return CompletableFuture.completedFuture(mockResponse);
    }

    private static CompletableFuture<HttpResponse<RandomWorks>> randomWorksResponse() {
        var status = new OpenOpusResponse.OpenOpusResponseStatus("1.0", true, null, "db", 5, 0.123, "api");
        ComposerOverview composer = new ComposerOverview(79, "Tchaikovsky", "Pyotr Ilyich Tchaikovsky", Epoch.ROMANTIC);
        RandomWork work1 = new RandomWork(7486, "Nutcracker, op. 71a", Genre.ORCHESTRAL, composer);
        RandomWork work2 = new RandomWork(1352, "Some Work", Genre.KEYBOARD, composer);
        RandomWorks body = new RandomWorks(status, null, List.of(work1, work2));
        HttpResponse<RandomWorks> mockResponse = mock(HttpResponse.class);
        given(mockResponse.body()).willReturn(body);
        return CompletableFuture.completedFuture(mockResponse);
    }

    private static CompletableFuture<HttpResponse<ComposersList>> errorResponse() {
        var status = new OpenOpusResponse.OpenOpusResponseStatus("1.0", false, "Test error message", "db", 5, 0.123, "api");
        var metadata = new OpenOpusResponse.OpenOpusRequestMetadata("type", "item", null, null);
        ComposersList body = new ComposersList(status, metadata, null);
        HttpResponse<ComposersList> mockResponse = mock(HttpResponse.class);
        given(mockResponse.body()).willReturn(body);
        return CompletableFuture.completedFuture(mockResponse);
    }

    private static Epoch[] epochProvider() {
        return Epoch.values();
    }

    private static Stream<List<Integer>> invalidIdListProvider() {
        return Stream.of(
                List.of(0),
                List.of(-1),
                List.of(8, 3, 0),
                List.of(48, -3, 59)
        );
    }

    private static Stream<Arguments> validComposerIdAndGenreProvider() {
        return Stream.of(
                arguments(1, Genre.KEYBOARD),
                arguments(2, Genre.POPULAR),
                arguments(1, Genre.RECOMMENDED),
                arguments(73, Genre.ORCHESTRAL)
        );
    }

    public static Stream<Arguments> criteriaProvider() {
        RandomWorksCriteria criteria0 = RandomWorksCriteria.builder().build();
        String urlParams0 = "";
        RandomWorksCriteria criteria1 = RandomWorksCriteria.builder()
                .setRecommendedComposer(true)
                .build();
        String urlParams1 = "?recommendedcomposer=1";
        RandomWorksCriteria criteria2 = RandomWorksCriteria.builder()
                .setEpoch(Epoch.EARLY_ROMANTIC)
                .setPopularWork(false)
                .build();
        String urlParams2 = "?popularwork=0&epoch=Early+Romantic";
        RandomWorksCriteria criteria3 = RandomWorksCriteria.builder()
                .setComposer(List.of(34, 832, 7132))
                .setRecommendedWork(true)
                .build();
        String urlParams3 = "?recommendedwork=1&composer=34%2C832%2C7132";
        RandomWorksCriteria criteria4 = RandomWorksCriteria.builder()
                .setWork(List.of(16642, 16578, 16595))
                .setGenre(Genre.KEYBOARD)
                .setPopularComposer(true)
                .build();
        String urlParams4 = "?popularcomposer=1&genre=Keyboard&work=16642%2C16578%2C16595";

        return Stream.of(
                arguments(criteria0, urlParams0),
                arguments(criteria1, urlParams1),
                arguments(criteria2, urlParams2),
                arguments(criteria3, urlParams3),
                arguments(criteria4, urlParams4)
        );
    }
}
