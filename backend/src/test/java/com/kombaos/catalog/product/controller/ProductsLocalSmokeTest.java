package com.kombaos.catalog.product.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.kombaos.catalog.product.dto.ProductCreateRequest;
import com.kombaos.catalog.product.dto.ProductResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
class ProductsLocalSmokeTest {

    @LocalServerPort
    int port;

    static final Path tempDir = createTempDir();

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
    void createAndGetProduct() {
        TestRestTemplate client = new TestRestTemplate();

        var created = client.postForEntity(
                "http://localhost:" + port + "/api/products",
                new ProductCreateRequest("Ruana", "Ruana de lana", 120_000_00L, "COP", true),
                ProductResponse.class
        );

        assertEquals(201, created.getStatusCode().value());
        assertNotNull(created.getBody());
        assertNotNull(created.getBody().id());

        var fetched = client.getForEntity(
                "http://localhost:" + port + "/api/products/" + created.getBody().id(),
                ProductResponse.class
        );
        assertEquals(200, fetched.getStatusCode().value());
        assertNotNull(fetched.getBody());
        assertEquals("Ruana", fetched.getBody().name());
    }
}
