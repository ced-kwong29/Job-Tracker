package com.cedrickwong.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    @Enumerated(EnumType.STRING)
    private Type type;

    private String title;

    public Job() {
    }

    public Job(Company company, String title, Type type) {
        this.company = company;
        this.title = title;
        this.type = type;
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

    public Type getType() {
        return type;
    }

    public enum Type {
        FULL_TIME,
        PART_TIME,
        CONTRACT,
        INTERNSHIP,
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", company=" + company +
                ", title='" + title + '\'' +
                ", type=" + type +
                '}';
    }
}
