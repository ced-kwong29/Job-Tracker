package com.cedrickwong.backend.model;

import jakarta.persistence.*;

import java.util.Objects;

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

    public void setCompany(Company company) {
        this.company = company;
    }

    public Company getCompany() {
        return company;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        FULL_TIME,
        PART_TIME,
        CONTRACT,
        INTERNSHIP,
        UNKNOWN
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return Objects.equals(id, job.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, company, type, title);
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
