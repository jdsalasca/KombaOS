package com.kombaos.web.controller;

public record ApiErrorResponse(
        String code,
        String message,
        Object details,
        String traceId
) {
}
