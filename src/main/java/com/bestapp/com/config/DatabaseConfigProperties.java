package com.bestapp.com.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * Provides database configuration properties.
 * Reads properties from application.properties.
 */
public class DatabaseConfigProperties {

    private final Properties properties = new Properties();

    public DatabaseConfigProperties() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (in == null) {
                throw new IllegalStateException("application.properties not found");
            }
            properties.load(in);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load application.properties: " + e.getMessage(), e);
        }
    }

    public String getProperty(String key) {
        return System.getProperty(key, properties.getProperty(key));
    }

}
