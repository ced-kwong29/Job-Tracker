package com.cedrickwong.jobTracker.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "jobId", referencedColumnName = "id")
    private Job job;

    private Date date;
    private Status status;

    public Application() {
    }

    public Application(User user, Job job, Date date, Status status) {
        this.user = user;
        this.job = job;
        this.date = date;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Job getJob() {
        return job;
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
        ASSESSMENT,
        INTERVIEWING,
        REJECTED,
        OFFERED,
        ACCEPTED,
        DECLINED
    }
}