package com.cedrickwong.jobTracker.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    private String title;

    public Job() {
    }

    public Job(Company company, String title) {
        this.company = company;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public Company getCompany() {
        return company;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", company=" + company +
                ", title='" + title + '\'' +
                '}';
    }
}
