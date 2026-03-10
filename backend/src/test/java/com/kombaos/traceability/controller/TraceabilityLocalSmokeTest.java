package com.kombaos.traceability.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.kombaos.catalog.product.dto.ProductCreateRequest;
import com.kombaos.catalog.product.dto.ProductResponse;
import com.kombaos.inventory.material.dto.MaterialCreateRequest;
import com.kombaos.inventory.material.dto.MaterialResponse;
import com.kombaos.production.order.dto.ProductionOrderCreateRequest;
import com.kombaos.production.order.dto.ProductionOrderResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TraceabilityLocalSmokeTest {

    @LocalServerPort
    int port;

    static final Path tempDir = createTempDir();

    private static Path createTempDir() {
        try {
            Path dir = Files.createTempDirectory("kombaos_traceability_test_");
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
    void buildTraceabilitySheetFromProductionOrder() {
        TestRestTemplate client = new TestRestTemplate();
        String baseUrl = "http://localhost:" + port;

        var createdProduct = client.postForEntity(
                baseUrl + "/api/products",
                new ProductCreateRequest("Ruana trazable", "Producto con ficha", 1500000L, "COP", true),
                ProductResponse.class
        );
        assertEquals(201, createdProduct.getStatusCode().value());
        assertNotNull(createdProduct.getBody());

        var createdMaterial = client.postForEntity(
                baseUrl + "/api/materials",
                new MaterialCreateRequest("Lana merino", "kg", "Proveedor A", "CO", true, 50000L, "COP"),
                MaterialResponse.class
        );
        assertEquals(201, createdMaterial.getStatusCode().value());

        var createdOrder = client.postForEntity(
                baseUrl + "/api/production/orders",
                new ProductionOrderCreateRequest(createdProduct.getBody().id(), new BigDecimal("10"), LocalDate.now().plusDays(5)),
                ProductionOrderResponse.class
        );
        assertEquals(201, createdOrder.getStatusCode().value());
        assertNotNull(createdOrder.getBody());

        var traceability = client.getForEntity(
                baseUrl + "/api/traceability/production-orders/" + createdOrder.getBody().id(),
                TraceabilityController.ProductionOrderTraceabilityResponse.class
        );

        assertEquals(200, traceability.getStatusCode().value());
        assertNotNull(traceability.getBody());
        assertNotNull(traceability.getBody().order());
        assertNotNull(traceability.getBody().product());
        assertNotNull(traceability.getBody().materials());
        assertEquals(createdOrder.getBody().id(), traceability.getBody().order().id());
        assertEquals(createdProduct.getBody().id(), traceability.getBody().product().id());
        assertTrue(traceability.getBody().materials().stream().anyMatch(m -> m.id().equals(createdMaterial.getBody().id())));
    }
}
