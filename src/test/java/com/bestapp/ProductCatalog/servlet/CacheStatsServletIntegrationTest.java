package com.bestapp.ProductCatalog.servlet;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CacheStatsServletIntegrationTest {

    private static Tomcat tomcat;
    private static final String BASE_URL = "http://localhost:8080";
    private static final String LOGIN_URL = BASE_URL + "/api/auth/login"; // Assume login URL is this

    @BeforeAll
    static void setup() throws Exception {
        // Запуск встроенного Tomcat
        tomcat = new Tomcat();
        tomcat.setPort(8080);

        // Указываем каталог веб-приложения
        String webappDir = new File("src/main/webapp").getAbsolutePath();
        Context ctx = tomcat.addContext("", webappDir);

        // Добавляем конфигурацию сервера
        tomcat.start();
    }

    @AfterAll
    static void tearDown() throws Exception {
        if (tomcat != null) {
            tomcat.stop();
        }
    }

    @Test
    void testCacheStatsWithLogin() throws IOException, ParseException {
        // Authorization emulation

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 1. Login
            String jsonLoginRequest = "{\"username\":\"user1\", \"password\":\"password123\"}";
            HttpPost loginRequest = new HttpPost(LOGIN_URL);
            loginRequest.setEntity(new org.apache.hc.core5.http.io.entity.StringEntity(jsonLoginRequest));
            loginRequest.setHeader("Content-Type", "application/json");

            try (CloseableHttpResponse loginResponse = httpClient.execute(loginRequest)) {
                assertEquals(200, loginResponse.getCode());  // Успешный логин
            }

            // 2. Sending GET request
            HttpGet request = new HttpGet(BASE_URL + "/api/cache/stats");
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());

                assertEquals(200, response.getCode());
                assertNotNull(jsonResponse);
                assertTrue(jsonResponse.contains("cacheHits"));
                assertTrue(jsonResponse.contains("cacheMisses"));
            }
        }
    }

    @Test
    void testCacheStatsWithoutLogin() throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(BASE_URL + "/api/cache/stats");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());

                assertEquals(401, response.getCode());
                assertTrue(jsonResponse.contains("You need to log in first."));
            }
        }
    }

    @Test
    void testCacheStatsWithInvalidSession() throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(BASE_URL + "/api/cache/stats");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());

                assertEquals(401, response.getCode());
                assertTrue(jsonResponse.contains("You need to log in first."));
            }
        }
    }

}
