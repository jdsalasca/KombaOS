package com.kombaos.inventory.threshold.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.kombaos.inventory.material.api.MaterialCreateRequest;
import com.kombaos.inventory.material.api.MaterialResponse;
import com.kombaos.inventory.movement.InventoryMovementType;
import com.kombaos.inventory.movement.api.InventoryMovementCreateRequest;
import com.kombaos.inventory.movement.api.InventoryMovementResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MaterialThresholdsLocalSmokeTest {

    static final Path tempDir = createTempDir();

    @Autowired
    private TestRestTemplate restTemplate;

    private static Path createTempDir() {
        try {
            Path dir = Files.createTempDirectory("kombaos_test_");
            dir.toFile().deleteOnExit();
            return dir;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("kombaos.environment", () -> "local");
        registry.add("kombaos.local-storage-dir", () -> tempDir.toString());
    }

    @Test
    void lowStockAlertAppearsAndDisappears() {
        ResponseEntity<MaterialResponse> createdMaterial = restTemplate.postForEntity(
                "/api/materials",
                new MaterialCreateRequest("Tinte", "kg"),
                MaterialResponse.class
        );
        assertThat(createdMaterial.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String materialId = createdMaterial.getBody().id();

        ResponseEntity<MaterialStockThresholdResponse> threshold = restTemplate.exchange(
                "/api/materials/" + materialId + "/threshold",
                org.springframework.http.HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(new MaterialStockThresholdUpsertRequest(new java.math.BigDecimal("10"))),
                MaterialStockThresholdResponse.class
        );
        assertThat(threshold.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<InventoryMovementResponse> createdMove = restTemplate.postForEntity(
                "/api/inventory/movements",
                new InventoryMovementCreateRequest(materialId, InventoryMovementType.IN, new java.math.BigDecimal("5"), "Ingreso"),
                InventoryMovementResponse.class
        );
        assertThat(createdMove.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<LowStockAlertResponse[]> alerts = restTemplate.getForEntity(
                "/api/inventory/alerts/low-stock",
                LowStockAlertResponse[].class
        );
        assertThat(alerts.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(alerts.getBody()).isNotNull();
        assertThat(java.util.Arrays.stream(alerts.getBody()).anyMatch(a -> a.materialId().equals(materialId))).isTrue();

        restTemplate.postForEntity(
                "/api/inventory/movements",
                new InventoryMovementCreateRequest(materialId, InventoryMovementType.IN, new java.math.BigDecimal("6"), "Ingreso"),
                InventoryMovementResponse.class
        );

        ResponseEntity<LowStockAlertResponse[]> alertsAfter = restTemplate.getForEntity(
                "/api/inventory/alerts/low-stock",
                LowStockAlertResponse[].class
        );
        assertThat(alertsAfter.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(alertsAfter.getBody()).isNotNull();
        assertThat(java.util.Arrays.stream(alertsAfter.getBody()).anyMatch(a -> a.materialId().equals(materialId))).isFalse();
    }
}

