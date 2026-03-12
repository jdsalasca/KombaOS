package com.kombaos.postsales.survey.repository.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kombaos.config.KombaosProperties;
import com.kombaos.persistence.file.FileJsonListStore;
import com.kombaos.postsales.survey.domain.SurveyTemplate;
import com.kombaos.postsales.survey.repository.SurveyTemplateStore;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "kombaos.environment", havingValue = "local")
public class FileSurveyTemplateStore implements SurveyTemplateStore {

    private static final TypeReference<List<SurveyTemplate>> TYPE = new TypeReference<>() {
    };
    private final FileJsonListStore<SurveyTemplate> store;

    public FileSurveyTemplateStore(ObjectMapper objectMapper, KombaosProperties properties) {
        this.store = new FileJsonListStore<>(objectMapper,
                Path.of(properties.getLocalStorageDir()).resolve("survey_templates.json"), TYPE);
    }

    @Override
    public List<SurveyTemplate> list() {
        return store.withLock(() -> store.readAll().stream().sorted(Comparator.comparing(SurveyTemplate::createdAt)).toList());
    }

    @Override
    public Optional<SurveyTemplate> getById(String id) {
        return store.withLock(() -> store.readAll().stream().filter(t -> t.id().equals(id)).findFirst());
    }

    @Override
    public SurveyTemplate create(String name, String question, boolean active) {
        return store.withLock(() -> {
            List<SurveyTemplate> all = store.readAll();
            SurveyTemplate created = new SurveyTemplate(UUID.randomUUID().toString(), name, question, active, Instant.now());
            all.add(created);
            store.writeAll(all);
            return created;
        });
    }
}
