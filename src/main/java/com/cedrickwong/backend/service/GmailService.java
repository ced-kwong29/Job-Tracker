package com.cedrickwong.backend.service;

import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.Gmail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class GmailService {

    private final Gmail gmail;
    private static final String labelName = "Job Applications";

    @Autowired
    public GmailService(Gmail gmail) {
        try {
            this.gmail = gmail;
        } catch (RuntimeException e) {
            throw new IllegalStateException("Failed to initialize Gmail service", e);
        }
    }

    private List<String> getLabel(String id) throws IOException {
        List<Label> labels = gmail.users().labels()
                                        .list(id)
                                        .execute()
                                        .getLabels();

        for (Label label : labels) {
            if (labelName.equals(label.getName())) {
                return List.of(label.getId());
            }
        }

        throw new IOException("Label not found in inbox of User " + id);
    }

    public List<Message> getMessages(String id, String pageToken, Long maxResults) throws IOException {
        return gmail.users().messages()
                            .list(id)
                            .setLabelIds(getLabel(id))
                            .setPageToken(pageToken)
                            .setMaxResults(maxResults)
                            .execute()
                            .getMessages();
    }
}
