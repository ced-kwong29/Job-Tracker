package com.cedrickwong.jobTracker.model;

import java.util.UUID;

public class User {
    private final String email, firstName, lastName;
    private final UUID userUid;

    public User(String email, String firstName, String lastName, UUID userUid) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userUid = userUid;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public UUID getUserUid() {
        return userUid;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userUid=" + userUid +
                '}';
    }
}
