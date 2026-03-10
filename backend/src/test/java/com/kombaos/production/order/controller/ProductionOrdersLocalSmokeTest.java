package com.kombaos.production.order.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.kombaos.production.order.domain.ProductionOrderStatus;
import com.kombaos.production.order.dto.ProductionOrderCreateRequest;
import com.kombaos.production.order.dto.ProductionOrderResponse;
import com.kombaos.production.order.dto.ProductionOrderUpdateStatusRequest;
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
class ProductionOrdersLocalSmokeTest {

    @LocalServerPort
    int port;

    static final Path tempDir = createTempDir();

    private static Path createTempDir() {
        try {
            Path dir = Files.createTempDirectory("kombaos_production_test_");
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
    void createListAndUpdateStatus() {
        TestRestTemplate client = new TestRestTemplate();
        String baseUrl = "http://localhost:" + port + "/api/production/orders";

        var created = client.postForEntity(
                baseUrl,
                new ProductionOrderCreateRequest("prod-1", new BigDecimal("12.5"), LocalDate.now().plusDays(7)),
                ProductionOrderResponse.class
        );

        assertEquals(201, created.getStatusCode().value());
        assertNotNull(created.getBody());
        assertNotNull(created.getBody().id());
        assertEquals(ProductionOrderStatus.PLANNED, created.getBody().status());

        var list = client.getForEntity(baseUrl, ProductionOrderResponse[].class);
        assertEquals(200, list.getStatusCode().value());
        assertNotNull(list.getBody());
        assertEquals(1, list.getBody().length);

        var updated = client.exchange(
                baseUrl + "/" + created.getBody().id() + "/status",
                org.springframework.http.HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(new ProductionOrderUpdateStatusRequest(ProductionOrderStatus.IN_PROGRESS)),
                ProductionOrderResponse.class
        );
        assertEquals(200, updated.getStatusCode().value());
        assertNotNull(updated.getBody());
        assertEquals(ProductionOrderStatus.IN_PROGRESS, updated.getBody().status());
    }
}
