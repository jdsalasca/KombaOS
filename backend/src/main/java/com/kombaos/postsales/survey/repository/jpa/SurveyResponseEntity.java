package com.kombaos.postsales.survey.repository.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "survey_responses")
public class SurveyResponseEntity {

    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false, length = 50)
    private String templateId;

    @Column(nullable = false, length = 200)
    private String customerEmail;

    @Column(nullable = false)
    private int score;

    @Column(length = 2000)
    private String comment;

    @Column(nullable = false)
    private Instant createdAt;

    protected SurveyResponseEntity() {}

    public SurveyResponseEntity(String id, String templateId, String customerEmail, int score, String comment, Instant createdAt) {
        this.id = id;
        this.templateId = templateId;
        this.customerEmail = customerEmail;
        this.score = score;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getTemplateId() { return templateId; }
    public String getCustomerEmail() { return customerEmail; }
    public int getScore() { return score; }
    public String getComment() { return comment; }
    public Instant getCreatedAt() { return createdAt; }
}
