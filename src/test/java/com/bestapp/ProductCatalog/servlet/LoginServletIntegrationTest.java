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
    void testLoginSuccess() throws IOException, ParseException {
        // Подготовим JSON запрос для успешного логина
        String jsonRequest = "{\"username\":\"user1\", \"password\":\"password123\"}";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(BASE_URL + "/api/auth/login");

            // Устанавливаем тело запроса с данными для логина
            request.setEntity(new org.apache.hc.core5.http.io.entity.StringEntity(jsonRequest));
            request.setHeader("Content-Type", "application/json");

            // Отправляем запрос
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseString = EntityUtils.toString(response.getEntity());

                // Проверяем успешный ответ
                assertEquals(200, response.getCode());
                assertTrue(responseString.contains("Login successful."));
            }
        }
    }

    @Test
    void testLoginFailureInvalidCredentials() throws IOException, ParseException {
        // Подготовим JSON запрос с некорректными данными
        String jsonRequest = "{\"username\":\"user1\", \"password\":\"wrongpassword\"}";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(BASE_URL + "/api/auth/login");

            // Устанавливаем тело запроса с неправильными данными
            request.setEntity(new org.apache.hc.core5.http.io.entity.StringEntity(jsonRequest));
            request.setHeader("Content-Type", "application/json");

            // Отправляем запрос
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseString = EntityUtils.toString(response.getEntity());

                // Проверяем, что сервер вернул ошибку авторизации
                assertEquals(401, response.getCode());
                assertTrue(responseString.contains("Invalid credentials."));
            }
        }
    }

    @Test
    void testLoginAlreadyLoggedIn() throws IOException, ParseException {
        // Имитируем, что пользователь уже авторизован в сессии
        String jsonRequest = "{\"username\":\"user1\", \"password\":\"password123\"}";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(BASE_URL + "/api/auth/login");

            // Устанавливаем тело запроса
            request.setEntity(new org.apache.hc.core5.http.io.entity.StringEntity(jsonRequest));
            request.setHeader("Content-Type", "application/json");

            // Отправляем первый запрос, чтобы залогиниться
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                // Проверка успешного логина
                assertEquals(200, response.getCode());
            }

            // Теперь отправляем запрос снова, чтобы проверить "уже залогинен"
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseString = EntityUtils.toString(response.getEntity());

                // Проверяем, что сервер вернул сообщение об уже авторизованном пользователе
                assertEquals(200, response.getCode());
                assertTrue(responseString.contains("Already logged in as: user1"));
            }
        }
    }

}
