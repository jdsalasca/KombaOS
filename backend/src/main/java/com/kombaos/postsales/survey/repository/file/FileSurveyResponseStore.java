package com.kombaos.postsales.survey.repository.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kombaos.config.KombaosProperties;
import com.kombaos.persistence.file.FileJsonListStore;
import com.kombaos.postsales.survey.domain.SurveyResponse;
import com.kombaos.postsales.survey.repository.SurveyResponseStore;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "kombaos.environment", havingValue = "local")
public class FileSurveyResponseStore implements SurveyResponseStore {

    private static final TypeReference<List<SurveyResponse>> TYPE = new TypeReference<>() {
    };
    private final FileJsonListStore<SurveyResponse> store;

    public FileSurveyResponseStore(ObjectMapper objectMapper, KombaosProperties properties) {
        this.store = new FileJsonListStore<>(objectMapper,
                Path.of(properties.getLocalStorageDir()).resolve("survey_responses.json"), TYPE);
    }

    @Override
    public List<SurveyResponse> list() {
        return store.withLock(() -> store.readAll().stream().sorted(Comparator.comparing(SurveyResponse::createdAt)).toList());
    }

    @Override
    public SurveyResponse create(String templateId, String customerEmail, int score, String comment) {
        return store.withLock(() -> {
            List<SurveyResponse> all = store.readAll();
            SurveyResponse created = new SurveyResponse(UUID.randomUUID().toString(), templateId, customerEmail, score, comment, Instant.now());
            all.add(created);
            store.writeAll(all);
            return created;
        });
    }
}
