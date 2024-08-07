package com.cedrickwong.jobTracker.model;

import java.util.UUID;

public record Job(UUID id, String title, Company company) {

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", company=" + company +
                '}';
    }
}
