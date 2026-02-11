package com.kombaos.inventory.material.api;

import java.time.Instant;

public record MaterialResponse(
        String id,
        String name,
        String unit,
        Instant createdAt
) {
}
