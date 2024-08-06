package com.cedrickwong.jobTracker.model;

import java.util.UUID;

public class Company {
    private final UUID id;
    private final String name;

    public Company(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
