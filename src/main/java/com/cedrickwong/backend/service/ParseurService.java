package com.cedrickwong.backend.service;

import com.cedrickwong.backend.configuration.ParseurClient;

import com.google.gson.JsonArray;

import org.springframework.stereotype.Service;

@Service
public class ParseurService {

    private final ParseurClient parseurClient;

    public ParseurService(ParseurClient parseurClient) {
        this.parseurClient = parseurClient;
    }

    public JsonArray getDocuments(String mailboxId) throws RuntimeException {
        return parseurClient.fetchData(mailboxId)
                            .map(jsonObject -> jsonObject.get("results").getAsJsonArray())
                            .orElseThrow(() -> new RuntimeException("Could not fetch documents from Parseur"));
    }
}