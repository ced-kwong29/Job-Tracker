package com.cedrickwong.backend.configuration;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

public class ParseurClient {

    private final WebClient webClient;

    public ParseurClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Optional<JsonObject> fetchData(String mailboxId) {
        return webClient.get().uri("/parser/" + mailboxId + "/document_set").retrieve()
                                                                                .bodyToMono(String.class)
                                                                                .map(responseString -> JsonParser.parseString(responseString).getAsJsonObject())
                                                                                .blockOptional();
    }
}
