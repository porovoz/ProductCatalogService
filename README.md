# Product Catalog Service (Marketplace)
## Описание проекта

**Product Catalog Service** — это приложение предоставляет REST API для управления продуктами, 
а также аутентификации пользователей и работы с кэшем. Включает работу с базой данных, добавление/удаление продуктов, 
а также различные фильтрации продуктов по категориям, брендам и диапазону цен.

Приложение поддерживает:

- Добавление, редактирование, удаление и просмотр товаров
- Поиск и фильтрацию по категории, бренду и диапазону цен
- Авторизацию пользователей
- Аудит действий (логирование входа/выхода и бизнес-действий)
- Кеширование часто запрашиваемых данных
- Простая метрика (время выполнения поиска)
- Логирование вызова методов и действий пользователя
---

## Структура проекта

- `servlet/` — классы для обработки HTTP-запросов.
- `dto/` — объекты для передачи данных между слоями.
- `service/` — бизнес-логика приложения.
- `repository/` - взаимодействие с базой данных.
- `model/` — модель продукта.
- `aspect/` — классы аспектов AspectJ для метрик и логирования.

## Технологии

| Компонент | Используемая технология                            |
|------------|----------------------------------------------------|
| Язык | Java 17+                                           |
| Хранение данных | PostgreSQL                                         |
| Коллекции | Map, List, EnumMap, LinkedHashMap                  |
| Кеширование | EnumMap<CacheType, Map<String, List<Product>>>     |
| Авторизация | PostgreSQL                                         |
| Логирование | AuditAspect, PerformanceLoggingAspect, AuditLogger |

---

## Запуск проекта

1. **Склонируйте репозиторий:**
   ```bash
   git clone https://github.com/username/product-catalog-service.git
   cd product-catalog-service

2. **Скомпилируйте проект:**
    ```bash
   javac -d out src/com/bestapp/com/**/*.java
   
3. **Запустите приложение:**
После сборки проекта, запустите приложение с помощью встроенного сервера Tomcat, используя команду:
   ```bash
   mvn tomcat7:run
   
4. **Авторизация:**
   * Username: admin
   * Password: password

### Авторизация

- Предустановленные пользователи:

    - admin / admin

### Управление товарами

| Функция                       | Описание |
|-------------------------------|-----------------------|
| Добавить товар                | Создаёт новый товар |
| Изменить товар                | Обновляет данные по ID |
| Удалить товар                 | Удаляет товар по ID |
| Просмотреть все товары        | Отображает весь каталог |
| Получить товары по категории  | Отображает товары по категории |
| Получить товары по бренду     | Отображает товары по бренду |
| Получить товары диапазону цен | Отображает товары диапазону цен |

### Поиск и фильтрация

- Поиск по категории

- Поиск по бренду

- Поиск по диапазону цен

Результаты кешируются для ускорения повторных запросов.

### Кеширование

- Первый запрос сохраняет результат в кеше

- Повторный запрос — мгновенный доступ

- Кеш сбрасывается при изменении каталога

### Примеры запросов в Postman

- Login
POST productCatalogService/api/auth/login

URL: http://localhost:8080/productCatalogService/api/auth/login

Body:

 ```
  {
  "username": "admin",
  "password": "password"
  }
```
cURL:
 ```
curl --location 'http://localhost:8080/productCatalogService/api/auth/login' \
--header 'Content-Type: application/json' \
--data '{
"username": "admin",
"password": "password"
}
'
 ```

- Logout
  POST /api/auth/logout

URL: http://localhost:8080/productCatalogService/api/auth/logout

cURL:
 ```
curl --location --request POST 'http://localhost:8080/productCatalogService/api/auth/logout'
 ```

- Create product
  POST productCatalogService/api/products/

URL: http://localhost:8080/productCatalogService/api/products

Body:

 ```
  {
  "name": "Test Product",
  "description": "Description",
  "price": 100.0,
  "category": "Category 1",
  "brand": "Brand A",
  "stockQuantity": 10
}
```
cURL:
 ```
curl --location 'http://localhost:8080/productCatalogService/api/products' \
--header 'Content-Type: application/json' 'Cookie: JSESSIONID=74C6F75665CD6104040F88EDFC403656' \
--data '{
  "name": "Test Product",
  "description": "Description",
  "price": 100.0,
  "category": "Category 1",
  "brand": "Brand A",
  "stockQuantity": 10
}
'
 ```

- Get all products
  POST productCatalogService/api/products/

URL: http://localhost:8080/productCatalogService/api/products

cURL:
 ```
curl --location 'http://localhost:8080/productCatalogService/api/products'
--header 'Cookie: JSESSIONID=74C6F75665CD6104040F88EDFC403656'
 ```

- Update product
  PUT productCatalogService/api/products/{id}

URL: http://localhost:8080/productCatalogService/api/products/{id}

Body:

 ```
  {
  "id": 1,
  "name": "Updated Product",
  "description": "Updated Description",
  "price": 150.0,
  "category": "Updated Category",
  "brand": "Updated Brand",
  "stockQuantity": 5
}
```
cURL:
 ```
curl --location --request PUT 'http://localhost:8080/productCatalogService/api/products' \
--header 'Content-Type: application/json' 'Cookie: JSESSIONID=74C6F75665CD6104040F88EDFC403656'\
--data '{
  "id": 1,
  "name": "Updated Product",
  "description": "Updated Description",
  "price": 150.0,
  "category": "Updated Category",
  "brand": "Updated Brand",
  "stockQuantity": 5
}
'
 ```

- Delete product
  DELETE productCatalogService/api/products/{id}

URL: http://localhost:8080/productCatalogService/api/products/{id}

cURL:
 ```
curl --location --request DELETE 'http://localhost:8080/productCatalogService/api/products/1' \
--header 'Cookie: JSESSIONID=74C6F75665CD6104040F88EDFC403656'
 ```

- Get products by brand
  GET productCatalogService/api/products/brand?brand={brand}

URL: http://localhost:8080/productCatalogService/api/products/brand?brand={brand}

cURL:
 ```
curl --location 'http://localhost:8080/productCatalogService/api/products/brand?brand=SpeedX' \
--header 'Cookie: JSESSIONID=269C25B347DA06D422226963D173162F'
 ```

- Get products by category
  GET productCatalogService/api/products/category?category={category}

URL: http://localhost:8080/productCatalogService/api/products/category?category={category}

cURL:
 ```
curl --location 'http://localhost:8080/productCatalogService/api/products/category?category=Bicycles' \
--header 'Cookie: JSESSIONID=269C25B347DA06D422226963D173162F'
 ```

- Get products by price range
  GET productCatalogService/api/products/price-range?min={min}&max={max}

URL: http://localhost:8080/productCatalogService/api/products/price-range?min={min}&max={max}

cURL:
 ```
curl --location 'http://localhost:8080/productCatalogService/api/products/price-range?min=1&max=40' \
--header 'Cookie: JSESSIONID=269C25B347DA06D422226963D173162F'
 ```

- Get cache statistics
  GET productCatalogService/api/cache/stats

URL: http://localhost:8080/productCatalogService/api/cache/stats

cURL:
 ```
curl --location 'http://localhost:8080/productCatalogService/api/products/cache/stats' \
--header 'Cookie: JSESSIONID=269C25B347DA06D422226963D173162F'
 ```
