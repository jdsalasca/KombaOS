package com.kombaos.postsales.survey.dto;

import java.time.Instant;

public record SurveyTemplateResponse(
        String id,
        String name,
        String question,
        boolean active,
        Instant createdAt
) {
}
