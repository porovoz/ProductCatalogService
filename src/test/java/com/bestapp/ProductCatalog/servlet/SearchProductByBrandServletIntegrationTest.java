package com.bestapp.ProductCatalog.servlet;

import com.bestapp.com.servlet.SearchProductByBrandServlet;
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

class SearchProductByBrandServletIntegrationTest {

    private static Tomcat tomcat;
    private static final int PORT = 8080;
    private static final String BASE_URL = "http://localhost:" + PORT;
    private static final String SEARCH_URL = BASE_URL + "/api/products/brand";

    private String sessionCookie;

    @BeforeAll
    static void startTomcat() throws Exception {
        System.setProperty("org.aspectj.weaver.loadtime.configuration", "META-INF/aop.xml");
        System.setProperty("java.awt.headless", "true");

        tomcat = new Tomcat();
        tomcat.setPort(PORT);

        String webappDir = new File("src/main/webapp").getAbsolutePath();
        var ctx = tomcat.addContext("", webappDir);

        Tomcat.addServlet(ctx, "searchByBrandServlet", new SearchProductByBrandServlet());
        ctx.addServletMappingDecoded("/api/products/brand", "searchByBrandServlet");

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
        sessionCookie = "JSESSIONID=test-session-id"; // User login emulation
    }

    @Test
    void testSearchProductsByBrandSuccess() throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String brand = "Nike";
            HttpGet request = new HttpGet(SEARCH_URL + "?brand=" + brand);
            request.setHeader("Cookie", sessionCookie);

            try (var response = httpClient.execute(request)) {
                String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                assertEquals(200, response.getCode());
                assertNotNull(body);
                assertTrue(body.contains("brand") || body.contains("name"));
            }
        }
    }

    @Test
    void testSearchProductsByBrandNotFound() throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String brand = "NonExistingBrand";
            HttpGet request = new HttpGet(SEARCH_URL + "?brand=" + brand);
            request.setHeader("Cookie", sessionCookie);

            try (var response = httpClient.execute(request)) {
                String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                assertEquals(404, response.getCode());
                assertTrue(body.contains("No products found for this brand."));
            }
        }
    }

    @Test
    void testSearchProductsByBrandUnauthorized() throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String brand = "Nike";
            HttpGet request = new HttpGet(SEARCH_URL + "?brand=" + brand);

            try (var response = httpClient.execute(request)) {
                String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                assertEquals(401, response.getCode());
                assertTrue(body.contains("You need to log in first."));
            }
        }
    }

    @Test
    void testSearchProductsByBrandMissingParam() throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(SEARCH_URL);
            request.setHeader("Cookie", sessionCookie);

            try (var response = httpClient.execute(request)) {
                String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                assertEquals(400, response.getCode());
                assertTrue(body.contains("Brand parameter is required."));
            }
        }
    }

}
