package com.cedrickwong.backend.service;

import com.cedrickwong.backend.configuration.ParseurClient;

import com.google.gson.JsonObject;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ParseurService {

    private final ParseurClient parseurClient;

    public ParseurService(ParseurClient parseurClient) {
        this.parseurClient = parseurClient;
    }

    public Mono<JsonObject> getData(String endpoint) {
        return parseurClient.fetchData(endpoint);
    }
}
