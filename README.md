# Open Opus Java - a Java client library for the Open Opus API

## Description
Open Opus Java is an asynchronous Java client for the [Open Opus API](https://openopus.org/). It
provides convenient access to the Open Opus classical music metadata service, offering composer details, works
and metadata in a structured, easy-to-use format.

[//]: # (Available in [Maven Central]&#40;https://search.maven.org/&#41;.)

## Features
- Asynchronous HTTP requests for non-blocking operations using Java Futures.
- Data retrieval on composers, genres, works, and performers.
- Easy integration with Java applications.

## Getting Started

### Installation
The official JAR files are available as a GitHub Release. You may install it
with the GitHub CLI:

```shell
# list available version releases
gh release list --repo jlynxdev/open-opus-java

# download a release of choice
gh release download v0.1.5 --repo jlynxdev/open-opus-java --pattern "*.jar"
```

Install the downloaded jar into your local Maven repository (`~/.m2` directory):
```shell
mvn install:install-file -Dfile=[path-to-jar-file] -DgroupId=dev.jlynx -DartifactId=open-opus-java -Dversion=0.1.5 -Dpackaging=jar
```

Finally, add the dependency to your `pom.xml` to use this library in your Maven project:

```xml
<dependency>
    <groupId>dev.jlynx</groupId>
    <artifactId>open-opus-java</artifactId>
    <version>0.1.5</version>
</dependency>
```

[//]: # (For Gradle users, add the following to your `build.gradle`:)

[//]: # ()
[//]: # (```groovy)

[//]: # (implementation 'dev.jlynx:open-opus-java:0.1.0')

[//]: # (```)

### Example usage

#### Initialization

```java
import dev.jlynx.openopusjava.OpenOpusClient;
import dev.jlynx.openopusjava.OpenOpusClientOptions;

OpenOpusClientOptions options = OpenOpusClientOptions.builder()
        .withLoggingEnabled()
        .build();
// Setting the options object is not required; defaults will be used if it's not passed
OpenOpusClient client = new OpenOpusClient(options);
// Use the client object to fetch data...
```

#### Fetching composer data

```java
import dev.jlynx.openopusjava.OpenOpusClient;
import dev.jlynx.openopusjava.response.body.ComposersList;
import dev.jlynx.openopusjava.response.subtype.Epoch;

OpenOpusClient client = new OpenOpusClient();
// Get a list of popular composers
CompletableFuture<HttpResponse<ComposersList>> popular = client.getPopularComposers();
// Get composers by the first letter of the surname
CompletableFuture<HttpResponse<ComposersList>> byLetter = client.listComposers('b');
// Get composers by musical epoch
CompletableFuture<HttpResponse<ComposersList>> byEpoch = client.listComposers(Epoch.LATE_ROMANTIC);
// Search composers by name
CompletableFuture<HttpResponse<ComposersList>> searchComposers = client.searchComposers("j s bach");
```

#### Fetching work data

```java
import dev.jlynx.openopusjava.OpenOpusClient;
import dev.jlynx.openopusjava.request.RandomWorksCriteria;
import dev.jlynx.openopusjava.response.body.WorksList;
import dev.jlynx.openopusjava.response.subtype.Epoch;
import dev.jlynx.openopusjava.response.subtype.Genre;

OpenOpusClient client = new OpenOpusClient();
// Get works by composer id
CompletableFuture<HttpResponse<WorksList>> byComposer = client.listWorks(46);
// Search works by name, composer id and genre
CompletableFuture<HttpResponse<WorksList>> searchWorks = client.searchWorks("sonata", 196, Genre.KEYBOARD);
// Get random works by the given criteria
CompletableFuture<HttpResponse<WorksList>> searchWorks = client.listRandomWorks(RandomWorksCriteria.builder()
        .setPopularWork(true)
        .setEpoch(Epoch.BAROQUE)
        .setGenre(Genre.ORCHESTRAL)
);
```

#### Search everything

```java
import dev.jlynx.openopusjava.OpenOpusClient;
import dev.jlynx.openopusjava.response.body.OmnisearchResponse;

OpenOpusClient client = new OpenOpusClient();
// Search for all entities by name and specify pagination offset
CompletableFuture<HttpResponse<OmnisearchResponse>> searched = client.search("beethoven symphony", 0);
```

## Requirements

- Java 21 or higher
- Maven or Gradle build tool (optionally)

## Documentation

For official API documentation, refer to the [Open Opus API Usage Guide](https://github.com/openopus-org/openopus_api/blob/master/USAGE.md).

## Contributions

Contributions are welcome! Feel free to fork this repository and submit a pull request or create an issue.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

This project utilizes the [Open Opus API](https://openopus.org/) for accessing classical music metadata.
Thanks to the Open Opus team for making this valuable data accessible!
