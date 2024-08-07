package com.cedrickwong.jobTracker.model;

import java.util.Date;
import java.util.UUID;

public class Application {

    private final UUID id;
    private final Job job;
    private final User user;
    private final Date date;
    private Status status;

    public Application(UUID id, Job job, User user, Date date, Status status) {
        this.id = id;
        this.job = job;
        this.user = user;
        this.date = date;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public Job getJob() {
        return job;
    }

    public User getUser() {
        return user;
    }

    public Date getDate() {
        return date;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public enum Status {
        WAITING,
        INTERVIEWING,
        REJECTED,
        OFFERED,
        ACCEPTED,
        DECLINED
    }

    @Override
    public String toString() {
        return "Application{" +
                "id=" + id +
                ", job=" + job +
                ", user=" + user +
                ", date=" + date +
                ", status=" + status +
                '}';
    }
}
