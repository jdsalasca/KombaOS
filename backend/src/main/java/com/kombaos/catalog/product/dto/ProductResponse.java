package com.kombaos.catalog.product.dto;

import java.time.Instant;

public record ProductResponse(
        String id,
        String name,
        String description,
        long priceCents,
        String currency,
        boolean active,
        Instant createdAt
) {
}
