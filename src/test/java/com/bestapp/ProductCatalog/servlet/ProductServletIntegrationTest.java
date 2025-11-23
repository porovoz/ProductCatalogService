package com.bestapp.ProductCatalog.servlet;

import org.apache.catalina.startup.Tomcat;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ProductServletIntegrationTest {
    private static Tomcat tomcat;
    private static final String BASE_URL = "http://localhost:8080";
    private static final String LOGIN_URL = BASE_URL + "/api/auth/login";
    private static final String PRODUCT_URL = BASE_URL + "/api/products/";

    private String sessionCookie; // Для хранения JSESSIONID после логина

    @BeforeAll
    static void setupTomcat() throws Exception {
        tomcat = new Tomcat();
        tomcat.setPort(8080);

        // Каталог веб-приложения
        String webappDir = new File("src/main/webapp").getAbsolutePath();
        tomcat.addContext("", webappDir);

        tomcat.start();
    }

    @AfterAll
    static void tearDownTomcat() throws Exception {
        if (tomcat != null) {
            tomcat.stop();
        }
    }

    /**
     * Выполняем логин и сохраняем cookie сессии
     */
    private String loginAndGetSessionCookie() throws IOException, ProtocolException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String jsonLoginRequest = "{\"username\":\"user1\",\"password\":\"password123\"}";
            HttpPost loginRequest = new HttpPost(LOGIN_URL);
            loginRequest.setEntity(new StringEntity(jsonLoginRequest, ContentType.APPLICATION_JSON));

            try (var response = httpClient.execute(loginRequest)) {
                assertEquals(200, response.getCode());
                String cookieHeader = response.getHeader("Set-Cookie").getValue();
                assertNotNull(cookieHeader);
                return cookieHeader.split(";")[0]; // "JSESSIONID=..."
            }
        }
    }

    @BeforeEach
    void login() throws IOException, ProtocolException {
        sessionCookie = loginAndGetSessionCookie();
    }

    @Test
    void testAddProduct() throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String jsonProductRequest = "{\"id\":1,\"name\":\"Test Product\",\"price\":100.0}";
            HttpPost productRequest = new HttpPost(PRODUCT_URL);
            productRequest.setHeader("Cookie", sessionCookie);
            productRequest.setEntity(new StringEntity(jsonProductRequest, ContentType.APPLICATION_JSON));

            try (var response = httpClient.execute(productRequest)) {
                String body = EntityUtils.toString(response.getEntity());
                assertEquals(201, response.getCode());
                assertTrue(body.contains("Product added successfully"));
            }
        }
    }

    @Test
    void testGetAllProducts() throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet getRequest = new HttpGet(PRODUCT_URL);
            getRequest.setHeader("Cookie", sessionCookie);

            try (var response = httpClient.execute(getRequest)) {
                String body = EntityUtils.toString(response.getEntity());
                assertEquals(200, response.getCode());
                assertTrue(body.contains("Test Product"));
            }
        }
    }

    @Test
    void testUnauthorizedAccess() throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet getRequest = new HttpGet(PRODUCT_URL);
            // Без session cookie
            try (var response = httpClient.execute(getRequest)) {
                String body = EntityUtils.toString(response.getEntity());
                assertEquals(401, response.getCode());
                assertTrue(body.contains("You need to log in first."));
            }
        }
    }

}
