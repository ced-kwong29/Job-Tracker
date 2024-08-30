package com.cedrickwong.backend.rest;

import com.cedrickwong.backend.model.Keyword;
import com.cedrickwong.backend.service.GmailService;

import com.google.api.services.gmail.model.Message;

import com.google.gson.*;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/gmail")
public class GmailController extends BaseController {

    private final GmailService gmailService;
    private final HttpSession httpSession;
    private static final String keywordsFilePath = "/keywords.json";

    @Autowired
    public GmailController(GmailService gmailService, HttpSession httpSession, Gson gson) {
        super(gson);
        this.gmailService = gmailService;
        this.httpSession = httpSession;
    }

//    private JsonObject loadKeywords() throws Exception {
//        InputStream in = GmailController.class.getResourceAsStream(keywordsFilePath);
//        if (in == null) {
//            throw new FileNotFoundException(keywordsFilePath);
//        }
//
//        JsonObject jsonObject = new JsonObject();
//
//        JsonObject keywordsJsonObject = JsonParser.parseReader(new InputStreamReader(in)).getAsJsonObject();
//        for (String key : keywordsJsonObject.keySet()) {
//            JsonElement jsonElement = keywordsJsonObject.get(key);
//            assert jsonElement.isJsonArray();
//            jsonObject.add(key, jsonElement.getAsJsonArray());
//        }
//
//        return jsonObject;
//    }
//
//    private void updateKeywords(String propertyName, String[] keywordArray, JsonObject jsonObject) {
//        if (keywordArray != null) {
//            JsonArray jsonArray = jsonObject.getAsJsonArray(propertyName);
//            for (String keyword : keywordArray) {
//                jsonArray.add(keyword);
//            }
//        }
//    }

    private void writeToJsonFile(JsonObject jsonObject) throws Exception {
       FileWriter writer = new FileWriter(keywordsFilePath);
       gson.toJson(jsonObject, writer);
    }

    @PutMapping(path = "/keywords")
    public ResponseEntity<?> addKeywords(@RequestBody Map<Keyword.Category, String[]> keywords)  {
        if (httpSession.getAttribute("user") == null) {
            return super.notLoggedInErrorResponse();
        }
        for(Map.Entry<Keyword.Category, String[]> entry : keywords.entrySet()) {
            for (String keyword : entry.getValue()) {
                Keyword word = new Keyword(entry.getKey(), keyword);
            }
        }
//        if (!(keywords.containsKey("roles_of_interest") || keywords.containsKey("job_boards") || keywords.containsKey("recruitment"))) {
//            return super.getOkResponse(true, "No keywords found");
//        }

        return null;

//        try {
//            JsonObject jsonObject = loadKeywords();
//            updateKeywords("roles_of_interest", keywords.get("roles_of_interest"), jsonObject);
//            updateKeywords("job_boards", keywords.get("job_boards"), jsonObject);
//            updateKeywords("recruitment", keywords.get("recruitment"), jsonObject);
////            writeToJsonFile(jsonObject);
//
//            return super.actionOkResponse("keywords update");
//        } catch (Exception e) {
//            return super.getErrorResponse(e.getMessage());
//        }
    }

    @GetMapping(path="/emails/{id}")
    public ResponseEntity<?> getApplications(@PathVariable String id, @RequestParam String pageToken, @RequestParam String maxResults) {
        if (httpSession.getAttribute("user") == null) {
            return super.notLoggedInErrorResponse();
        }

        try {
            List<Message> messages = gmailService.getMessages(id, pageToken, Long.parseLong(maxResults));
            getApplications(messages);

            return ResponseEntity.ok("");
        } catch (Exception e) {
            return super.getErrorResponse(e.getMessage());
        }
    }

    private void getApplications(List<Message> messages) {
        for (Message message : messages) {
            processMessage(message);
        }
    }

    private void processMessage(Message message) {
        List<String> labelIds = message.getLabelIds();
//        if (labelIds.size() >= 2) {
//            String company = labelIds.getById(0), jobType = labelIds.getById(1);
//
//            Date date = new Date(message.getInternalDate());
//
//            MessagePart payload = message.getPayload();
//            for (MessagePart messagePart : payload.getParts()) {
//                if (messagePart.getMimeType() == "") {
//
//                }
//            }
//        }
    }
}
