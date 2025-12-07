# Product Catalog Service (Marketplace)
## Описание проекта

**Product Catalog Service** — это приложение, предоставляющее REST API 
для управления продуктами, аутентификации пользователей, а также 
кеширования часто запрашиваемых данных. С помощью API можно добавлять, 
редактировать, удалять товары, а также фильтровать их по категориям, 
брендам и диапазону цен.

Приложение включает:

- Добавление, редактирование, удаление и просмотр товаров
- Поиск и фильтрацию по категории, бренду и диапазону цен
- Авторизацию пользователей с использованием JWT токенов
- Аудит действий (логирование входа и бизнес-действий)
- Кеширование часто запрашиваемых данных
- Простой механизм логирования и мониторинга производительности
---

## Структура проекта

- `auth/` — классы для обработки аутентификации и управления JWT токенами.
- `dto/` — ообъекты передачи данных (DTO) для взаимодействия между слоями.
- `service/` — бизнес-логика приложения.
- `repository/` - взаимодействие с базой данных.
- `model/` — модель продукта.
- `security/` — классы для обработки безопасности и авторизации пользователей.

## Технологии

| Компонент | Используемая технология                            |
|------------|----------------------------------------------------|
| Язык | Java 17+                                           |
| Хранение данных | PostgreSQL                                         |
| Коллекции | Map, List, EnumMap, LinkedHashMap                  |
| Кеширование | Кеширование результатов запросов с использованием HashMap    |
| Авторизация | JWT (JSON Web Token)                                         |
| Логирование | SLF4J, AuditAspect, PerformanceLoggingAspect |

---

## Запуск проекта

1. **Склонируйте репозиторий:**
   ```bash
   git clone https://github.com/username/product-catalog-service.git
   cd product-catalog-service

2. **Скомпилируйте проект:**
    ```bash
   mvn clean install
   
3. **Запустите приложение:**
После сборки проекта, запустите приложение с помощью встроенного сервера:
   ```bash
   mvn spring-boot:run
   
4. **Авторизация:**
   
Используйте предустановленные учетные данные для авторизации:
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

### Примеры запросов в Postman

- Регистрация пользователя

  POST /api/auth/register

URL: http://localhost:8080/api/auth/register

Body:

 ```
  {
  "username": "newuser",
  "password": "mewpassword"
  }
```
- Login

POST /api/auth/login

URL: http://localhost:8080/api/auth/login

Body:

 ```
  {
  "username": "admin",
  "password": "password"
  }
```
- Получение нового access токена (с помощью refresh токена):

POST /api/auth/token

URL: http://localhost:8080/api/auth/token

Body:

 ```
  {
  "refreshToken": "your_refresh_token_here"
  }
```
- Обновление refresh токена:

POST /api/auth/refresh

URL: http://localhost:8080/api/auth/refresh

Body:

 ```
  {
  "refreshToken": "your_refresh_token_here"
  }
```
- Create product
  POST /api/products/

URL: http://localhost:8080/api/products

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

- Get all products
  POST /api/products/

URL: http://localhost:8080/api/products

cURL:
 ```
curl --location 'http://localhost:8080/api/products?pageNumber=1&pageSize=10' \
--header 'Authorization: Bearer your_jwt_token'
 ```

- Update product
  PUT /api/products/{id}

URL: http://localhost:8080/api/products/{id}

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

- Delete product
  DELETE /api/products/{id}

URL: http://localhost:8080/api/products/{id}

cURL:
 ```
curl --location --request DELETE 'http://localhost:8080/api/products/1' \
--header 'Authorization: Bearer your_jwt_token'
 ```

- Get products by brand
  GET /api/products/brand?brand={brand}

URL: http://localhost:8080/api/products/brand?brand={brand}

cURL:
 ```
curl --location 'http://localhost:8080/api/products/brand?brand=BrandA' \
--header 'Authorization: Bearer your_jwt_token'
 ```

- Get products by category
  GET /api/products/category?category={category}

URL: http://localhost:8080/api/products/category?category={category}

cURL:
 ```
curl --location 'http://localhost:8080/api/products/category?category=Category1' \
--header 'Authorization: Bearer your_jwt_token'
 ```

- Get products by price range
  GET /api/products/price-range?min={min}&max={max}

URL: http://localhost:8080/api/products/price-range?min={min}&max={max}

cURL:
 ```
curl --location 'http://localhost:8080/api/products/price-range?min=10&max=100' \
--header 'Authorization: Bearer your_jwt_token'
 ```
