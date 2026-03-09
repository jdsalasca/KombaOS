package com.kombaos.inventory.movement.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kombaos.inventory.material.domain.Material;
import com.kombaos.inventory.material.service.MaterialService;
import com.kombaos.inventory.movement.domain.InventoryMovement;
import com.kombaos.inventory.movement.domain.InventoryMovementType;
import com.kombaos.inventory.movement.repository.InventoryMovementStore;
import com.kombaos.inventory.threshold.domain.MaterialStockThreshold;
import com.kombaos.inventory.threshold.repository.MaterialStockThresholdStore;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventoryMovementServiceTest {

    @Mock
    private InventoryMovementStore movementStore;

    @Mock
    private MaterialService materialService;

    @Mock
    private MaterialStockThresholdStore thresholdStore;

    private InventoryMovementService service;

    @BeforeEach
    void setUp() {
        service = new InventoryMovementService(movementStore, materialService, thresholdStore);
    }

    @Test
    void rejectsBlankMaterialId() {
        assertThatThrownBy(() -> service.create(" ", InventoryMovementType.IN, new BigDecimal("1"), "seed"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("materialId is required");
    }

    @Test
    void requiresReasonForOutAndAdjustMovements() {
        when(materialService.getById("mat-1")).thenReturn(new Material(
                "mat-1", "Algodon", "kg", null, null, false, null, null, Instant.now()
        ));

        assertThatThrownBy(() -> service.create("mat-1", InventoryMovementType.OUT, new BigDecimal("1"), " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Reason is required");

        assertThatThrownBy(() -> service.create("mat-1", InventoryMovementType.ADJUST, new BigDecimal("-1"), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Reason is required");
    }

    @Test
    void rejectsOutMovementThatGoesBelowConfiguredMinStock() {
        when(materialService.getById("mat-1")).thenReturn(new Material(
                "mat-1", "Algodon", "kg", null, null, false, null, null, Instant.now()
        ));
        when(movementStore.list(Optional.of("mat-1"))).thenReturn(List.of(
                new InventoryMovement("in-1", "mat-1", InventoryMovementType.IN, new BigDecimal("10"), "Initial", Instant.now())
        ));
        when(thresholdStore.getByMaterialId("mat-1")).thenReturn(Optional.of(
                new MaterialStockThreshold("mat-1", new BigDecimal("8"), Instant.now())
        ));

        assertThatThrownBy(() -> service.create("mat-1", InventoryMovementType.OUT, new BigDecimal("3"), "Consume"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("below minimum threshold");

        verify(movementStore, never()).create(any(), any(), any(), any());
    }

    @Test
    void allowsOutMovementWhenResultStaysAboveThreshold() {
        when(materialService.getById("mat-1")).thenReturn(new Material(
                "mat-1", "Algodon", "kg", null, null, false, null, null, Instant.now()
        ));
        when(movementStore.list(Optional.of("mat-1"))).thenReturn(List.of(
                new InventoryMovement("in-1", "mat-1", InventoryMovementType.IN, new BigDecimal("10"), "Initial", Instant.now())
        ));
        when(thresholdStore.getByMaterialId("mat-1")).thenReturn(Optional.of(
                new MaterialStockThreshold("mat-1", new BigDecimal("5"), Instant.now())
        ));

        service.create("mat-1", InventoryMovementType.OUT, new BigDecimal("3"), "Consume");

        verify(movementStore).create(eq("mat-1"), eq(InventoryMovementType.OUT), eq(new BigDecimal("3")), eq("Consume"));
    }

    @Test
    void keepsLegacyRuleWhenNoThresholdExists() {
        when(materialService.getById("mat-1")).thenReturn(new Material(
                "mat-1", "Algodon", "kg", null, null, false, null, null, Instant.now()
        ));
        when(movementStore.list(Optional.of("mat-1"))).thenReturn(List.of(
                new InventoryMovement("in-1", "mat-1", InventoryMovementType.IN, new BigDecimal("2"), "Initial", Instant.now())
        ));

        assertThatThrownBy(() -> service.create("mat-1", InventoryMovementType.OUT, new BigDecimal("3"), "Consume"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient stock");
    }
}
