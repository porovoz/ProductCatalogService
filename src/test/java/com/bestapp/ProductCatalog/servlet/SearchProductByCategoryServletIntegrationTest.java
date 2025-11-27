package com.bestapp.ProductCatalog.servlet;

import com.bestapp.com.servlet.SearchProductByCategoryServlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class SearchProductByCategoryServletIntegrationTest {

    private static Tomcat tomcat;
    private static final int PORT = 8080;
    private static final String BASE_URL = "http://localhost:" + PORT;
    private static final String SEARCH_URL = BASE_URL + "/api/products/category";

    private String sessionCookie;

    @BeforeAll
    static void startTomcat() throws Exception {
        System.setProperty("org.aspectj.weaver.loadtime.configuration", "META-INF/aop.xml");
        System.setProperty("java.awt.headless", "true");

        tomcat = new Tomcat();
        tomcat.setPort(PORT);

        String webappDir = new File("src/main/webapp").getAbsolutePath();
        var ctx = tomcat.addContext("", webappDir);

        Tomcat.addServlet(ctx, "searchByCategoryServlet", new SearchProductByCategoryServlet());
        ctx.addServletMappingDecoded("/api/products/category", "searchByCategoryServlet");

        tomcat.start();
    }

    @AfterAll
    static void stopTomcat() throws Exception {
        if (tomcat != null) {
            tomcat.stop();
            tomcat.destroy();
        }
    }

    @BeforeEach
    void mockLogin() {
        sessionCookie = "JSESSIONID=test-session-id";
    }

    @Test
    void testSearchProductsByCategorySuccess() throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String category = "Shoes";
            HttpGet request = new HttpGet(SEARCH_URL + "?category=" + category);
            request.setHeader("Cookie", sessionCookie);

            try (var response = httpClient.execute(request)) {
                String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                assertEquals(200, response.getCode());
                assertNotNull(body);
                assertTrue(body.contains("category") || body.contains("name"));
            }
        }
    }

    @Test
    void testSearchProductsByCategoryNotFound() throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String category = "NonExistingCategory";
            HttpGet request = new HttpGet(SEARCH_URL + "?category=" + category);
            request.setHeader("Cookie", sessionCookie);

            try (var response = httpClient.execute(request)) {
                String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                assertEquals(404, response.getCode());
                assertTrue(body.contains("No products found in this category."));
            }
        }
    }

    @Test
    void testSearchProductsByCategoryMissingParam() throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(SEARCH_URL);
            request.setHeader("Cookie", sessionCookie);

            try (var response = httpClient.execute(request)) {
                String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                assertEquals(400, response.getCode());
                assertTrue(body.contains("Category parameter is required."));
            }
        }
    }

    @Test
    void testSearchProductsByCategoryUnauthorized() throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String category = "Shoes";
            HttpGet request = new HttpGet(SEARCH_URL + "?category=" + category);

            try (var response = httpClient.execute(request)) {
                String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                assertEquals(401, response.getCode());
                assertTrue(body.contains("You need to log in first."));
            }
        }
    }

}
