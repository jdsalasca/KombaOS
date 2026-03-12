package com.kombaos.postsales.survey.repository.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "survey_templates")
public class SurveyTemplateEntity {

    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 1000)
    private String question;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private Instant createdAt;

    protected SurveyTemplateEntity() {}

    public SurveyTemplateEntity(String id, String name, String question, boolean active, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.question = question;
        this.active = active;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getQuestion() { return question; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
}
