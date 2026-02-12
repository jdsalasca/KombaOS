package com.kombaos.inventory.material.domain;

import java.time.Instant;

public record Material(
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
