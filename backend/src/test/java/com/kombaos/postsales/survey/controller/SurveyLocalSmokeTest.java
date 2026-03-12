package com.kombaos.postsales.survey.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.kombaos.postsales.survey.dto.SurveyResponseCreateRequest;
import com.kombaos.postsales.survey.dto.SurveyResponseResponse;
import com.kombaos.postsales.survey.dto.SurveyTemplateCreateRequest;
import com.kombaos.postsales.survey.dto.SurveyTemplateResponse;
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
class SurveyLocalSmokeTest {

    @LocalServerPort
    int port;

    static final Path tempDir = createTempDir();

    private static Path createTempDir() {
        try {
            Path dir = Files.createTempDirectory("kombaos_survey_test_");
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
    void createTemplateAndSubmitPublicResponse() {
        String baseUrl = "http://localhost:" + port;
        TestRestTemplate client = new TestRestTemplate();

        var createdTemplate = client.postForEntity(
                baseUrl + "/api/surveys/templates",
                new SurveyTemplateCreateRequest("Postventa entrega", "¿Cómo califica la entrega?", true),
                SurveyTemplateResponse.class
        );
        assertEquals(201, createdTemplate.getStatusCode().value());
        assertNotNull(createdTemplate.getBody());

        var createdResponse = client.postForEntity(
                baseUrl + "/api/public/surveys/responses",
                new SurveyResponseCreateRequest(createdTemplate.getBody().id(), "cliente@example.com", 5, "Excelente"),
                SurveyResponseResponse.class
        );
        assertEquals(201, createdResponse.getStatusCode().value());
        assertNotNull(createdResponse.getBody());

        var listed = client.getForEntity(baseUrl + "/api/surveys/responses", SurveyResponseResponse[].class);
        assertEquals(200, listed.getStatusCode().value());
        assertNotNull(listed.getBody());
        assertEquals(1, listed.getBody().length);
    }
}
