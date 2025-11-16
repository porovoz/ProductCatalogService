package com.bestapp.ProductCatalog;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class BasePostgresTest {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("productCatalogService")
            .withUsername("alex")
            .withPassword("alexSecret");

    static {
        postgres.start();
        System.setProperty("db.url", postgres.getJdbcUrl());
        System.setProperty("db.username", postgres.getUsername());
        System.setProperty("db.password", postgres.getPassword());
        System.setProperty("liquibase.changelog", "liquibase/changelog-master.yaml");
    }

}
