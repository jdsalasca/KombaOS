package com.kombaos.postsales.survey.dto;

import jakarta.validation.constraints.NotBlank;

public record SurveyTemplateCreateRequest(
        @NotBlank String name,
        @NotBlank String question,
        Boolean active
) {
}
