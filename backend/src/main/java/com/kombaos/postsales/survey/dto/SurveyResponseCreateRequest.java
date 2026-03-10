package com.kombaos.postsales.survey.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record SurveyResponseCreateRequest(
        @NotBlank String templateId,
        @NotBlank @Email String customerEmail,
        @Min(1) @Max(5) int score,
        String comment
) {
}
