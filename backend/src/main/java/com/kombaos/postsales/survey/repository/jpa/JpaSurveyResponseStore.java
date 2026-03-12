package com.kombaos.postsales.survey.repository.jpa;

import com.kombaos.postsales.survey.domain.SurveyResponse;
import com.kombaos.postsales.survey.repository.SurveyResponseStore;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "kombaos.environment", havingValue = "cloud", matchIfMissing = true)
public class JpaSurveyResponseStore implements SurveyResponseStore {

    private final SurveyResponseJpaRepository repository;

    public JpaSurveyResponseStore(SurveyResponseJpaRepository repository) { this.repository = repository; }

    @Override
    public List<SurveyResponse> list() {
        return repository.findAll().stream().map(this::toModel).sorted(Comparator.comparing(SurveyResponse::createdAt)).toList();
    }

    @Override
    public SurveyResponse create(String templateId, String customerEmail, int score, String comment) {
        SurveyResponseEntity saved = repository.save(new SurveyResponseEntity(UUID.randomUUID().toString(), templateId, customerEmail, score, comment, Instant.now()));
        return toModel(saved);
    }

    private SurveyResponse toModel(SurveyResponseEntity e) {
        return new SurveyResponse(e.getId(), e.getTemplateId(), e.getCustomerEmail(), e.getScore(), e.getComment(), e.getCreatedAt());
    }
}
