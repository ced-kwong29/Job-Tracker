package com.cedrickwong.jobTracker.service;

import com.cedrickwong.jobTracker.factory.GmailClientFactory;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.Gmail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class GmailService {

    private final Gmail gmail;

    @Autowired
    public GmailService(GmailClientFactory gmailClientFactory) {
        try {
            this.gmail = gmailClientFactory.getGmail();
        } catch (RuntimeException e) {
            throw new IllegalStateException("Failed to initialize Gmail service", e);
        }
    }

    public List<Message> getMessages(String userId, String pageToken, Long maxResults) throws IOException {
        return gmail.users().messages().list(userId)
                                        .setPageToken(pageToken)
                                        .setMaxResults(maxResults)
                                        .execute()
                                        .getMessages();
    }
}
