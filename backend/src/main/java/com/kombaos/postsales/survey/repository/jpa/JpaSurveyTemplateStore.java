package com.kombaos.postsales.survey.repository.jpa;

import com.kombaos.postsales.survey.domain.SurveyTemplate;
import com.kombaos.postsales.survey.repository.SurveyTemplateStore;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "kombaos.environment", havingValue = "cloud", matchIfMissing = true)
public class JpaSurveyTemplateStore implements SurveyTemplateStore {

    private final SurveyTemplateJpaRepository repository;

    public JpaSurveyTemplateStore(SurveyTemplateJpaRepository repository) { this.repository = repository; }

    @Override
    public List<SurveyTemplate> list() {
        return repository.findAll().stream().map(this::toModel).sorted(Comparator.comparing(SurveyTemplate::createdAt)).toList();
    }

    @Override
    public Optional<SurveyTemplate> getById(String id) {
        return repository.findById(id).map(this::toModel);
    }

    @Override
    public SurveyTemplate create(String name, String question, boolean active) {
        SurveyTemplateEntity saved = repository.save(new SurveyTemplateEntity(UUID.randomUUID().toString(), name, question, active, Instant.now()));
        return toModel(saved);
    }

    private SurveyTemplate toModel(SurveyTemplateEntity e) {
        return new SurveyTemplate(e.getId(), e.getName(), e.getQuestion(), e.isActive(), e.getCreatedAt());
    }
}
