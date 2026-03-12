package com.kombaos.postsales.survey.repository;

import com.kombaos.postsales.survey.domain.SurveyTemplate;
import java.util.List;
import java.util.Optional;

public interface SurveyTemplateStore {
    List<SurveyTemplate> list();

    Optional<SurveyTemplate> getById(String id);

    SurveyTemplate create(String name, String question, boolean active);
}
