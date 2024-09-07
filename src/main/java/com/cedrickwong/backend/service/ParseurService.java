package com.cedrickwong.backend.service;

import com.cedrickwong.backend.configuration.ParseurClient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class ParseurService {

    private final ParseurClient parseurClient;

    public ParseurService(ParseurClient parseurClient) {
        this.parseurClient = parseurClient;
    }

    public JsonArray getDocuments(String mailboxId) throws RuntimeException {
        Optional<JsonObject> documentsJsonObject = parseurClient.fetchData(mailboxId);
        if (documentsJsonObject.isEmpty()) {
            throw new RuntimeException("Could not fetch documents from Parseur");
        }

        JsonArray jsonArray = documentsJsonObject.get().getAsJsonArray("results");
        for (JsonElement jsonElement : jsonArray) {
            JsonObject documentJsonObject = JsonParser.parseString(jsonElement.getAsJsonObject().get("result").getAsString())
                                                        .getAsJsonObject();

            if (!(documentJsonObject.has("JobRole")) && documentJsonObject.has("JobCompany") && documentJsonObject.has("Received")) {
                jsonArray.remove(jsonElement);
            }
        }

        return jsonArray;
    }
}
