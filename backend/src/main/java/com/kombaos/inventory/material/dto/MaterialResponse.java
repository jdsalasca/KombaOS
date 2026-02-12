package com.kombaos.inventory.material.dto;

import java.time.Instant;

public record MaterialResponse(
        String id,
        String name,
        String unit,
        String supplier,
        String origin,
        boolean certified,
        Long costCents,
        String currency,
        Instant createdAt
) {
}
