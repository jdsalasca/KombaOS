package com.kombaos.postsales.survey.repository;

import com.kombaos.postsales.survey.domain.SurveyResponse;
import java.util.List;

public interface SurveyResponseStore {
    List<SurveyResponse> list();

    SurveyResponse create(String templateId, String customerEmail, int score, String comment);
}
