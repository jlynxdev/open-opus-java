package dev.jlynx.openopusjava;

import dev.jlynx.openopusjava.request.RandomWorksCriteria;
import dev.jlynx.openopusjava.response.body.*;
import dev.jlynx.openopusjava.response.subtype.Genre;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

class TempMain {

    public static void main(String[] args) {
//        OpenOpusClientOptions options = OpenOpusClientOptions.builder()
//                .withLoggingEnabled()
//                .build();
//        OpenOpusClient client = new OpenOpusClient(options);
//
//        PerformerRolesResponse body = client.listPerformerRoles(List.of("987?$&/34", "murray 5perahia", "", "ghg")).join().body();
//        System.out.println(body);

        OpenOpusClient client = new OpenOpusClient(OpenOpusClientOptions.withDefaults());
        CompletableFuture<HttpResponse<ComposersList>> composers = client.listComposers('b');
    }
}
