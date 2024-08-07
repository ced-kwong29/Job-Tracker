package com.cedrickwong.jobTracker.rest;

import com.cedrickwong.jobTracker.service.GmailService;
import com.google.api.services.gmail.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    }
}
