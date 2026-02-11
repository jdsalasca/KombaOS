package com.kombaos.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kombaos")
public class KombaosProperties {

    private String environment;
    private String localStorageDir;

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getLocalStorageDir() {
        return localStorageDir;
    }

    public void setLocalStorageDir(String localStorageDir) {
        this.localStorageDir = localStorageDir;
    }
}
