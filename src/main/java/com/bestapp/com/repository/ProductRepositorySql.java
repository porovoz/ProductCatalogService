package com.bestapp.com.repository;

/**
 * Enum representing SQL queries for interacting with the `app_data.products` table in the database.
 * Each constant in this enum corresponds to a specific SQL query used for CRUD (Create, Read, Update, Delete)
 * operations on the `products` table, along with select queries for filtering products based on various criteria.
 * The `getQuery()` method allows retrieval of the SQL query string associated with each constant.
 */
public enum ProductRepositorySql {

    INSERT_PRODUCT("INSERT INTO app_data.products (name, description, price, category, brand, stock_quantity) " +
            "VALUES (?, ?, ?, ?, ?, ?) RETURNING id"),

    UPDATE_PRODUCT_BY_ID("UPDATE app_data.products SET name=?, description=?, price=?, category=?, brand=?, stock_quantity=? WHERE id=?"),

    DELETE_PRODUCT_BY_ID("DELETE FROM app_data.products WHERE id=?"),

    SELECT_ALL_PRODUCTS("SELECT id, name, description, price, category, brand, stock_quantity FROM app_data.products ORDER BY id"),

    SELECT_BY_CATEGORY("SELECT id, name, description, price, category, brand, stock_quantity FROM app_data.products WHERE LOWER(category)=LOWER(?)"),

    SELECT_BY_BRAND("SELECT id, name, description, price, category, brand, stock_quantity FROM app_data.products WHERE LOWER(brand)=LOWER(?)"),

    SELECT_BY_PRICE_RANGE("SELECT id, name, description, price, category, brand, stock_quantity FROM app_data.products WHERE price BETWEEN ? AND ?"),

    SELECT_EXISTS_BY_ID("SELECT 1 FROM app_data.products WHERE id=?");

    private final String query;

    ProductRepositorySql(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
