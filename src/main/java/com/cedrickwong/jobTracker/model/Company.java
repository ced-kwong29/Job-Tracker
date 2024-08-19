package com.cedrickwong.jobTracker.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "companies",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = "name")
        }
)
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Company() {
    }

    public Company(String name) {
        this.name = name;
    }

    public Long getId() {
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