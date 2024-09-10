package com.cedrickwong.backend.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "job_id", referencedColumnName = "id")
    private Job job;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDate date;



    public Application() {
    }

    public Application(User user, Job job, LocalDate date, Status status) {
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

    public void setJob(Job job) {
        this.job = job;
    }

    public Job getJob() {
        return job;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
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

    @Override
    public String toString() {
        return "Application{" +
                "id=" + id +
                ", user=" + user +
                ", job=" + job +
                ", date=" + date +
                ", status=" + status +
                '}';
    }
}