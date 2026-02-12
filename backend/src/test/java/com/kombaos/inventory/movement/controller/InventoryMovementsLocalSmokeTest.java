package com.kombaos.inventory.movement.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.kombaos.inventory.material.dto.MaterialCreateRequest;
import com.kombaos.inventory.material.dto.MaterialResponse;
import com.kombaos.inventory.movement.domain.InventoryMovementType;
import com.kombaos.inventory.movement.dto.InventoryMovementCreateRequest;
import com.kombaos.inventory.movement.dto.InventoryMovementResponse;
import com.kombaos.inventory.movement.dto.MaterialStockResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
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
class InventoryMovementsLocalSmokeTest {

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
    void createMovementAndComputeStock() {
        ResponseEntity<MaterialResponse> createdMaterial = restTemplate.postForEntity(
                "/api/materials",
                new MaterialCreateRequest("Lana", "kg", null, null, null, null, null),
                MaterialResponse.class
        );
        assertThat(createdMaterial.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String materialId = createdMaterial.getBody().id();

        ResponseEntity<InventoryMovementResponse> createdMove = restTemplate.postForEntity(
                "/api/inventory/movements",
                new InventoryMovementCreateRequest(materialId, InventoryMovementType.IN, new java.math.BigDecimal("5.5"), "Compra"),
                InventoryMovementResponse.class
        );
        assertThat(createdMove.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<MaterialStockResponse> stock = restTemplate.getForEntity(
                "/api/materials/" + materialId + "/stock",
                MaterialStockResponse.class
        );
        assertThat(stock.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(stock.getBody().stock()).isEqualByComparingTo("5.5");
    }

    @Test
    void outWithInsufficientStockReturnsBadRequest() {
        ResponseEntity<MaterialResponse> createdMaterial = restTemplate.postForEntity(
                "/api/materials",
                new MaterialCreateRequest("Algodon", "kg", null, null, null, null, null),
                MaterialResponse.class
        );
        assertThat(createdMaterial.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String materialId = createdMaterial.getBody().id();

        ResponseEntity<Map> bad = restTemplate.postForEntity(
                "/api/inventory/movements",
                new InventoryMovementCreateRequest(materialId, InventoryMovementType.OUT, new java.math.BigDecimal("1"), "Consumo"),
                Map.class
        );
        assertThat(bad.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(bad.getBody()).containsEntry("error", "bad_request");
    }
}
