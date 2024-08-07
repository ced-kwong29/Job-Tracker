package com.cedrickwong.jobTracker.rest;

import com.cedrickwong.jobTracker.service.GmailService;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="/gmail")
public class GmailController extends BaseController {

    private final GmailService gmailService;

    @Autowired
    public GmailController(GmailService gmailService) {
        this.gmailService = gmailService;
    }


    @GetMapping(path ="/applications/{userId}")
    public ResponseEntity<?> getApplications(@PathVariable String userId, @RequestParam String pageToken, @RequestParam String maxResults) {
        try {
            List<Message> messages = gmailService.getMessages(userId, pageToken, Long.parseLong(maxResults));
            findApplications(messages);

            return ResponseEntity.ok("");
        } catch (IOException e) {
            return errorMessage(e);
        }
    }

    private void findApplications(List<Message> messages) {
        for (Message message : messages) {
            scanMessage(message);
        }
    }

    private void scanMessage(Message message) {
        MessagePart payload = message.getPayload();
        Map<String, Object> mapping = payload.getHeaders()
                .stream()
                .collect(Collectors.toMap(MessagePartHeader::getName, MessagePartHeader::getValue));
        mapping.put("date", message.getInternalDate());
    }
}
