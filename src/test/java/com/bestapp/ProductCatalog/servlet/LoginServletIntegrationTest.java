package com.bestapp.ProductCatalog.servlet;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginServletIntegrationTest {

    private static Tomcat tomcat;
    private static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    static void setup() throws Exception {
        tomcat = new Tomcat();
        tomcat.setPort(8080);

        String webappDir = new File("src/main/webapp").getAbsolutePath();
        tomcat.addContext("", webappDir);

        tomcat.start();
    }

    @AfterAll
    static void tearDown() throws Exception {
        if (tomcat != null) {
            tomcat.stop();
        }
    }

    @Test
    void testLoginSuccess() throws IOException, ParseException {
        String jsonRequest = "{\"username\":\"user1\", \"password\":\"password123\"}";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(BASE_URL + "/api/auth/login");

            request.setEntity(new org.apache.hc.core5.http.io.entity.StringEntity(jsonRequest));
            request.setHeader("Content-Type", "application/json");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseString = EntityUtils.toString(response.getEntity());

                assertEquals(200, response.getCode());
                assertTrue(responseString.contains("Login successful."));
            }
        }
    }

    @Test
    void testLoginFailureInvalidCredentials() throws IOException, ParseException {
        String jsonRequest = "{\"username\":\"user1\", \"password\":\"wrongpassword\"}";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(BASE_URL + "/api/auth/login");

            request.setEntity(new org.apache.hc.core5.http.io.entity.StringEntity(jsonRequest));
            request.setHeader("Content-Type", "application/json");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseString = EntityUtils.toString(response.getEntity());

                assertEquals(401, response.getCode());
                assertTrue(responseString.contains("Invalid credentials."));
            }
        }
    }

    @Test
    void testLoginAlreadyLoggedIn() throws IOException, ParseException {
        String jsonRequest = "{\"username\":\"user1\", \"password\":\"password123\"}";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(BASE_URL + "/api/auth/login");

            request.setEntity(new org.apache.hc.core5.http.io.entity.StringEntity(jsonRequest));
            request.setHeader("Content-Type", "application/json");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                assertEquals(200, response.getCode());
            }

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseString = EntityUtils.toString(response.getEntity());

                assertEquals(200, response.getCode());
                assertTrue(responseString.contains("Already logged in as: user1"));
            }
        }
    }

}
