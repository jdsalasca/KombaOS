package com.kombaos.inventory.material;

import java.time.Instant;

public record Material(
        String id,
        String name,
        String unit,
        Instant createdAt
) {
}
