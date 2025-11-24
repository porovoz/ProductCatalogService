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

class LogoutServletIntegrationTest {

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
    void testLogoutSuccess() throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String jsonLoginRequest = "{\"username\":\"user1\", \"password\":\"password123\"}";
            HttpPost loginRequest = new HttpPost(BASE_URL + "/api/auth/login");

            loginRequest.setEntity(new org.apache.hc.core5.http.io.entity.StringEntity(jsonLoginRequest));
            loginRequest.setHeader("Content-Type", "application/json");

            try (CloseableHttpResponse loginResponse = httpClient.execute(loginRequest)) {
                assertEquals(200, loginResponse.getCode());  // Проверяем успешный логин
            }

            HttpPost logoutRequest = new HttpPost(BASE_URL + "/api/auth/logout");

            try (CloseableHttpResponse logoutResponse = httpClient.execute(logoutRequest)) {
                String responseString = EntityUtils.toString(logoutResponse.getEntity());

                assertEquals(200, logoutResponse.getCode());
                assertTrue(responseString.contains("Logged out successfully."));
            }
        }
    }

    @Test
    void testLogoutWithoutLogin() throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost logoutRequest = new HttpPost(BASE_URL + "/api/auth/logout");

            try (CloseableHttpResponse logoutResponse = httpClient.execute(logoutRequest)) {
                String responseString = EntityUtils.toString(logoutResponse.getEntity());

                assertEquals(400, logoutResponse.getCode());
                assertTrue(responseString.contains("No user is currently logged in."));
            }
        }
    }

}
