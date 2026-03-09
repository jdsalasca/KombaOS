package com.kombaos.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.kombaos.catalog.product.dto.ProductCreateRequest;
import com.kombaos.catalog.product.dto.ProductResponse;
import com.kombaos.inventory.material.dto.MaterialCreateRequest;
import com.kombaos.inventory.material.dto.MaterialResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthSecuritySmokeTest {

    @LocalServerPort
    int port;

    static final Path tempDir = createTempDir();

    private static Path createTempDir() {
        try {
            Path dir = Files.createTempDirectory("kombaos_security_test_");
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
        registry.add("kombaos.security.enabled", () -> true);
    }

    @Test
    void enforcesAuthenticationOnProtectedRoutesAndKeepsHealthPublic() {
        String baseUrl = "http://localhost:" + port;

        TestRestTemplate anonymous = new TestRestTemplate();
        assertEquals(HttpStatus.OK,
                anonymous.getForEntity(baseUrl + "/api/health", String.class).getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED,
                anonymous.getForEntity(baseUrl + "/api/auth/login", String.class).getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED,
                anonymous.getForEntity(baseUrl + "/api/materials", String.class).getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED,
                anonymous.getForEntity(baseUrl + "/api/products", String.class).getStatusCode());
    }

    @Test
    void loginAndRoleAuthorizationMatrix() {
        String baseUrl = "http://localhost:" + port;

        TestRestTemplate operacion = new TestRestTemplate("operacion", "operacion123");
        ResponseEntity<AuthLoginResponse> loginResponse = operacion.exchange(
                baseUrl + "/api/auth/login",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());
        assertEquals("operacion", loginResponse.getBody().username());
        assertTrue(loginResponse.getBody().roles().contains("ROLE_OPERACION"));

        var materialResponse = operacion.postForEntity(
                baseUrl + "/api/materials",
                new MaterialCreateRequest("Lino", "kg", null, null, null, null, null),
                MaterialResponse.class
        );
        assertEquals(HttpStatus.CREATED, materialResponse.getStatusCode());

        var blockedProducts = operacion.postForEntity(
                baseUrl + "/api/products",
                new ProductCreateRequest("Poncho", "Tradicional", 1200000L, "COP", true),
                ProductResponse.class
        );
        assertEquals(HttpStatus.FORBIDDEN, blockedProducts.getStatusCode());

        TestRestTemplate comercial = new TestRestTemplate("comercial", "comercial123");
        var productResponse = comercial.postForEntity(
                baseUrl + "/api/products",
                new ProductCreateRequest("Ruana", "Lana virgen", 2500000L, "COP", true),
                ProductResponse.class
        );
        assertEquals(HttpStatus.CREATED, productResponse.getStatusCode());

        var blockedMaterials = comercial.postForEntity(
                baseUrl + "/api/materials",
                new MaterialCreateRequest("Seda", "kg", null, null, null, null, null),
                MaterialResponse.class
        );
        assertEquals(HttpStatus.FORBIDDEN, blockedMaterials.getStatusCode());

        TestRestTemplate admin = new TestRestTemplate("admin", "admin123");
        var adminCreatesProduct = admin.postForEntity(
                baseUrl + "/api/products",
                new ProductCreateRequest("Manta", "Tejido manual", 3200000L, "COP", true),
                ProductResponse.class
        );
        assertEquals(HttpStatus.CREATED, adminCreatesProduct.getStatusCode());

        var adminCreatesMaterial = admin.postForEntity(
                baseUrl + "/api/materials",
                new MaterialCreateRequest("Alpaca", "kg", null, null, null, null, null),
                MaterialResponse.class
        );
        assertEquals(HttpStatus.CREATED, adminCreatesMaterial.getStatusCode());
    }

    private record AuthLoginResponse(String username, List<String> roles) {
    }
}
