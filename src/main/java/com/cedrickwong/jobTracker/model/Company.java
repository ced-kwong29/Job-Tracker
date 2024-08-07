package com.cedrickwong.jobTracker.model;

import java.util.UUID;

public record Company(UUID id, String name) {

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
