package com.cedrickwong.jobTracker.model;

import java.util.UUID;

public class Job {
    private final UUID id;
    private final String title;
    private final Company company;

    public Job(UUID id, String title, Company company) {
        this.id = id;
        this.title = title;
        this.company = company;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Company getCompany() {
        return company;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", company=" + company +
                '}';
    }
}
