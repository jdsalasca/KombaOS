package com.kombaos;

import java.awt.Desktop;
import java.net.URI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ConfigurationPropertiesScan
public class KombaosBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(KombaosBackendApplication.class, args);
	}

	@Bean
	ApplicationListener<ApplicationReadyEvent> openBrowserOnStart(Environment environment) {
		return event -> {
			var env = environment.getProperty("kombaos.environment", "cloud");
			if (!"local".equalsIgnoreCase(env)) return;
			var enabled = environment.getProperty("kombaos.desktop.open", Boolean.class, true);
			if (!enabled) return;
			if (!Desktop.isDesktopSupported()) return;
			if (!(event.getApplicationContext() instanceof org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext serverContext)) {
				return;
			}
			var port = serverContext.getWebServer().getPort();
			var url = "http://localhost:" + port + "/";
			try {
				Desktop.getDesktop().browse(URI.create(url));
			} catch (Exception ignored) {
			}
		};
	}

}
