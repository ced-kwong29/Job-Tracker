package com.cedrickwong.jobTracker.rest;

import com.cedrickwong.jobTracker.service.GmailService;

import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.sql.Date;

@RestController
@RequestMapping(path="/gmail")
public class GmailController extends BaseController {

    private final GmailService gmailService;
    private final Gson gson;
    private static final String keywordsFilePath = "/keywords.json";
    private Map<String, List<String>> keywords;

    @Autowired
    public GmailController(GmailService gmailService, Gson gson) {
        this.gmailService = gmailService;
        this.gson = gson;
        this.keywords = null;
    }

    @GetMapping(path="/applications/{userId}")
    public ResponseEntity<?> getApplications(@PathVariable String userId, @RequestParam String pageToken, @RequestParam String maxResults) {
        try {
            List<Message> messages = gmailService.getMessages(userId, pageToken, Long.parseLong(maxResults));
            if (keywords == null) {
                keywords = loadKeywords();
            }
            getApplications(messages);

            return ResponseEntity.ok("");
        } catch (Exception e) {
            return errorMessage(e);
        }
    }

    private void getApplications(List<Message> messages) {
        for (Message message : messages) {
            processMessage(message);
        }
    }

    private void processMessage(Message message) {
        List<String> labelIds = message.getLabelIds();
        if (labelIds.size() >= 2) {
            String company = labelIds.get(0), jobType = labelIds.get(1);

            Date date = new Date(message.getInternalDate());

            MessagePart payload = message.getPayload();
            for (MessagePart messagePart : payload.getParts()) {
                if (messagePart.getMimeType() == "") {

                }
            }
        }
    }


    private Map<String, List<String>> loadKeywords() {
        InputStream in = GmailController.class.getResourceAsStream(keywordsFilePath);
        return gson.fromJson(new InputStreamReader(in), Map.class);
    }
}
