package com.kombaos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class KombaosBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(KombaosBackendApplication.class, args);
	}

}
