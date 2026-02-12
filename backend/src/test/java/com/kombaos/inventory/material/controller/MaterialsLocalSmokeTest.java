package com.kombaos.inventory.material.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.kombaos.inventory.material.dto.MaterialCreateRequest;
import com.kombaos.inventory.material.dto.MaterialResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MaterialsLocalSmokeTest {

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
    void healthAndCreateMaterial() {
        TestRestTemplate client = new TestRestTemplate();
        var health = client.getForEntity("http://localhost:" + port + "/api/health", String.class);
        assertEquals(200, health.getStatusCode().value());

        var created = client.postForEntity(
                "http://localhost:" + port + "/api/materials",
                new MaterialCreateRequest("Lana", "kg", null, null, null, null, null),
                MaterialResponse.class
        );
        assertEquals(201, created.getStatusCode().value());
        assertNotNull(created.getBody());
        assertNotNull(created.getBody().id());
    }

    @Test
    void listMaterialsSupportsFilters() {
        TestRestTemplate client = new TestRestTemplate();
        client.postForEntity(
                "http://localhost:" + port + "/api/materials",
                new MaterialCreateRequest("Algodon", "kg", "Proveedor A", "CO", true, 1234L, "COP"),
                MaterialResponse.class
        );
        client.postForEntity(
                "http://localhost:" + port + "/api/materials",
                new MaterialCreateRequest("Lana", "kg", "Proveedor B", "PE", false, null, null),
                MaterialResponse.class
        );

        var bySupplier = client.getForEntity(
                "http://localhost:" + port + "/api/materials?supplier=" + java.net.URLEncoder.encode("proveedor a", java.nio.charset.StandardCharsets.UTF_8),
                MaterialResponse[].class
        );
        assertEquals(200, bySupplier.getStatusCode().value());
        assertNotNull(bySupplier.getBody());
        assertEquals(1, bySupplier.getBody().length);
        assertEquals("Algodon", bySupplier.getBody()[0].name());

        var certifiedOnly = client.getForEntity(
                "http://localhost:" + port + "/api/materials?certified=true",
                MaterialResponse[].class
        );
        assertEquals(200, certifiedOnly.getStatusCode().value());
        assertNotNull(certifiedOnly.getBody());
        assertTrue(java.util.Arrays.stream(certifiedOnly.getBody()).allMatch(MaterialResponse::certified));

        var qSearch = client.getForEntity(
                "http://localhost:" + port + "/api/materials?q=lana",
                MaterialResponse[].class
        );
        assertEquals(200, qSearch.getStatusCode().value());
        assertNotNull(qSearch.getBody());
        assertTrue(java.util.Arrays.stream(qSearch.getBody()).anyMatch(m -> m.name().equals("Lana")));
    }
}
