package com.kombaos.sales.order.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.kombaos.catalog.product.dto.ProductCreateRequest;
import com.kombaos.catalog.product.dto.ProductResponse;
import com.kombaos.sales.order.domain.SalesOrderStatus;
import com.kombaos.sales.order.dto.SalesOrderCreateRequest;
import com.kombaos.sales.order.dto.SalesOrderResponse;
import com.kombaos.sales.order.dto.SalesOrderUpdateStatusRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PublicSalesOrdersLocalSmokeTest {

    @LocalServerPort
    int port;

    static final Path tempDir = createTempDir();

    private static Path createTempDir() {
        try {
            Path dir = Files.createTempDirectory("kombaos_sales_test_");
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
    void createPublicOrderAndManageStatusFromBackoffice() {
        String baseUrl = "http://localhost:" + port;

        TestRestTemplate client = new TestRestTemplate();
        var createdProduct = client.postForEntity(
                baseUrl + "/api/products",
                new ProductCreateRequest("Ruana venta", "Catalogo publico", 1800000L, "COP", true),
                ProductResponse.class
        );
        assertEquals(201, createdProduct.getStatusCode().value());
        assertNotNull(createdProduct.getBody());

        var createdOrder = client.postForEntity(
                baseUrl + "/api/public/orders",
                new SalesOrderCreateRequest("Ana", "ana@example.com", createdProduct.getBody().id(), new BigDecimal("2")),
                SalesOrderResponse.class
        );
        assertEquals(201, createdOrder.getStatusCode().value());
        assertNotNull(createdOrder.getBody());
        assertEquals(SalesOrderStatus.PENDING, createdOrder.getBody().status());

        var fetchedPublic = client.getForEntity(
                baseUrl + "/api/public/orders/" + createdOrder.getBody().id(),
                SalesOrderResponse.class
        );
        assertEquals(200, fetchedPublic.getStatusCode().value());
        assertNotNull(fetchedPublic.getBody());

        var updated = client.exchange(
                baseUrl + "/api/sales/orders/" + createdOrder.getBody().id() + "/status",
                HttpMethod.PUT,
                new HttpEntity<>(new SalesOrderUpdateStatusRequest(SalesOrderStatus.CONFIRMED)),
                SalesOrderResponse.class
        );
        assertEquals(200, updated.getStatusCode().value());
        assertNotNull(updated.getBody());
        assertEquals(SalesOrderStatus.CONFIRMED, updated.getBody().status());
    }
}
