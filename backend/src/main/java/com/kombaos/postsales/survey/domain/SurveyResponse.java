package com.kombaos.postsales.survey.domain;

import java.time.Instant;

public record SurveyResponse(
        String id,
        String templateId,
        String customerEmail,
        int score,
        String comment,
        Instant createdAt
) {
}
