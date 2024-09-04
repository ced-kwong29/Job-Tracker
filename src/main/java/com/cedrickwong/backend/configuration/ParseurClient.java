package com.cedrickwong.backend.configuration;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

public class ParseurClient {

    private final WebClient webClient;

    public ParseurClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<JsonObject> fetchData(String endpoint) {
        return this.webClient.get().uri(endpoint)
                                    .retrieve()
                                    .bodyToMono(String.class)
                                    .map(response -> JsonParser.parseString(response).getAsJsonObject())
                                    .doOnNext(response -> System.out.println("Response: " + response))
                                    .doOnError(error -> System.err.println("Error: " + error.getMessage()));
    }
}
