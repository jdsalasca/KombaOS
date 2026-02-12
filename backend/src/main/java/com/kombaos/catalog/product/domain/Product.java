package com.kombaos.catalog.product.domain;

import java.time.Instant;

public record Product(
        String id,
        String name,
        String description,
        long priceCents,
        String currency,
        boolean active,
        Instant createdAt
) {
}
