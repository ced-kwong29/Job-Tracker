package com.cedrickwong.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "keywords")
public class Keyword {
    @Id
    @Column(nullable = false, unique = true)
    private String keyword;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    public Keyword() {
    }

    public Keyword(Category category, String keyword) {
        this.category = category;
        this.keyword = keyword;
    }

    public Category getCategory() {
        return category;
    }

    public String getKeyword() {
        return keyword;
    }

    public enum Category {
        RECRUITING,
        JOB_BOARD,
        TLD,
        ROLES_OF_INTEREST
    }
}
