package com.kombaos.postsales.survey.service;

import com.kombaos.postsales.survey.domain.SurveyResponse;
import com.kombaos.postsales.survey.domain.SurveyTemplate;
import com.kombaos.postsales.survey.repository.SurveyResponseStore;
import com.kombaos.postsales.survey.repository.SurveyTemplateStore;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class SurveyService {

    private final SurveyTemplateStore templateStore;
    private final SurveyResponseStore responseStore;

    public SurveyService(SurveyTemplateStore templateStore, SurveyResponseStore responseStore) {
        this.templateStore = templateStore;
        this.responseStore = responseStore;
    }

    public List<SurveyTemplate> listTemplates() { return templateStore.list(); }

    public SurveyTemplate createTemplate(String name, String question, boolean active) {
        return templateStore.create(name, question, active);
    }

    public List<SurveyResponse> listResponses() { return responseStore.list(); }

    public SurveyResponse createResponse(String templateId, String customerEmail, int score, String comment) {
        SurveyTemplate template = templateStore.getById(templateId)
                .orElseThrow(() -> new NoSuchElementException("Survey template not found: " + templateId));
        if (!template.active()) {
            throw new IllegalArgumentException("Survey template is inactive: " + templateId);
        }
        return responseStore.create(templateId, customerEmail, score, comment);
    }
}
