package com.alexnerd.excelloader;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgreSQLContainerExtension implements BeforeAllCallback {

    private static final PostgreSQLContainer container = new PostgreSQLContainer("postgres:13.4-alpine");

    static {
        container.start();
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        System.setProperty("spring.datasource.url", container.getJdbcUrl());
        System.setProperty("spring.datasource.username", container.getUsername());
        System.setProperty("spring.datasource.password", container.getPassword());
    }
}
