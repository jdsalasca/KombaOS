package com.kombaos.postsales.survey.dto;

import java.time.Instant;

public record SurveyResponseResponse(
        String id,
        String templateId,
        String customerEmail,
        int score,
        String comment,
        Instant createdAt
) {
}
