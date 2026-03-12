package com.kombaos.postsales.survey.controller;

import com.kombaos.postsales.survey.domain.SurveyResponse;
import com.kombaos.postsales.survey.domain.SurveyTemplate;
import com.kombaos.postsales.survey.dto.SurveyResponseCreateRequest;
import com.kombaos.postsales.survey.dto.SurveyResponseResponse;
import com.kombaos.postsales.survey.dto.SurveyTemplateCreateRequest;
import com.kombaos.postsales.survey.dto.SurveyTemplateResponse;
import com.kombaos.postsales.survey.service.SurveyService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SurveyController {

    private final SurveyService service;

    public SurveyController(SurveyService service) { this.service = service; }

    @GetMapping("/api/surveys/templates")
    public List<SurveyTemplateResponse> listTemplates() {
        return service.listTemplates().stream().map(SurveyController::toTemplateResponse).toList();
    }

    @PostMapping("/api/surveys/templates")
    @ResponseStatus(HttpStatus.CREATED)
    public SurveyTemplateResponse createTemplate(@Valid @RequestBody SurveyTemplateCreateRequest request) {
        boolean active = request.active() == null || request.active();
        return toTemplateResponse(service.createTemplate(request.name(), request.question(), active));
    }

    @GetMapping("/api/surveys/responses")
    public List<SurveyResponseResponse> listResponses() {
        return service.listResponses().stream().map(SurveyController::toResponse).toList();
    }

    @PostMapping("/api/public/surveys/responses")
    @ResponseStatus(HttpStatus.CREATED)
    public SurveyResponseResponse createPublicResponse(@Valid @RequestBody SurveyResponseCreateRequest request) {
        return toResponse(service.createResponse(
                request.templateId(),
                request.customerEmail(),
                request.score(),
                request.comment()
        ));
    }

    private static SurveyTemplateResponse toTemplateResponse(SurveyTemplate template) {
        return new SurveyTemplateResponse(template.id(), template.name(), template.question(), template.active(), template.createdAt());
    }

    private static SurveyResponseResponse toResponse(SurveyResponse response) {
        return new SurveyResponseResponse(response.id(), response.templateId(), response.customerEmail(), response.score(), response.comment(), response.createdAt());
    }
}
