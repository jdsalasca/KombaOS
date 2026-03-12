package com.kombaos.postsales.survey.domain;

import java.time.Instant;

public record SurveyTemplate(
        String id,
        String name,
        String question,
        boolean active,
        Instant createdAt
) {
}
