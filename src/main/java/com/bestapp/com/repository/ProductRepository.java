package com.bestapp.com.repository;

import com.bestapp.com.config.DatabaseConfig;
import com.bestapp.com.model.Product;

import java.sql.*;
import java.util.*;

/**
 * Represents the product repository.
 * <p>
 * Provides CRUD operations, caching of frequent queries, and search functionality.
 * </p>
 */
public class ProductRepository {

    private final Connection connection;

    public ProductRepository() {
        this.connection = DatabaseConfig.getConnection();
    }

    /**
     * Saves a new product to the repository.
     *
     * @param product new product to add
     */
    public void save(Product product) {
        String sql = "INSERT INTO app_data.products (name, description, price, category, brand, stock_quantity) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getDescription());
            preparedStatement.setDouble(3, product.getPrice());
            preparedStatement.setString(4, product.getCategory());
            preparedStatement.setString(5, product.getBrand());
            preparedStatement.setInt(6, product.getStockQuantity());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    long id = resultSet.getLong("id");
                    product.setId(id);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save product: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing product by ID.
     * If the ID is not found, an exception is thrown.
     * </p>
     *
     * @param id         product ID to update
     * @param product new product data
     * @throws IllegalArgumentException if the product does not exist
     */
    public void updateById(Long id, Product product) {
        String sql = "UPDATE app_data.products SET name=?, description=?, price=?, category=?, brand=?, stock_quantity=? WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getDescription());
            preparedStatement.setDouble(3, product.getPrice());
            preparedStatement.setString(4, product.getCategory());
            preparedStatement.setString(5, product.getBrand());
            preparedStatement.setInt(6, product.getStockQuantity());
            preparedStatement.setLong(7, id);
            int updated = preparedStatement.executeUpdate();
            if (updated == 0) {
                throw new IllegalArgumentException("Product with ID " + id + " not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update product: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a product from the repository by its ID.
     *
     * @param id ID of the product to remove
     * @throws IllegalArgumentException if the product is not found
     */
    public void deleteById(Long id) {
        String sql = "DELETE FROM app_data.products WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            int deleted = preparedStatement.executeUpdate();
            if (deleted == 0) {
                throw new IllegalArgumentException("Product with ID " + id + " not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete product: " + e.getMessage(), e);
        }
    }


    /**
     * Returns all products in the repository.
     * @return list of all products
     */
    public List<Product> findAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT id, name, description, price, category, brand, stock_quantity FROM app_data.products ORDER BY id";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                Product product = mapRow(rs);
                list.add(product);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch products: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Searches products by category.
     *
     * @param category category name
     * @return list of matching products
     */
    public List<Product> findByCategory(String category) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT id, name, description, price, category, brand, stock_quantity FROM app_data.products WHERE LOWER(category)=LOWER(?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, category);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) list.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /**
     * Searches products by brand.
     *
     * @param brand brand name
     * @return list of matching products
     */
    public List<Product> findByBrand(String brand) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT id, name, description, price, category, brand, stock_quantity FROM app_data.products WHERE LOWER(brand)=LOWER(?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, brand);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) list.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /**
     * Searches products within a price range.
     *
     * @param min minimum price (inclusive)
     * @param max maximum price (inclusive)
     * @return list of products within range
     */
    public List<Product> findByPriceRange(double min, double max) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT id, name, description, price, category, brand, stock_quantity FROM app_data.products WHERE price BETWEEN ? AND ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDouble(1, min);
            preparedStatement.setDouble(2, max);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) list.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /**
     * @return true if a product exists by ID.
     */
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM app_data.products WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Product mapRow(ResultSet resultSet) throws SQLException {
        Product product = new Product(
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getDouble("price"),
                resultSet.getString("category"),
                resultSet.getString("brand"),
                resultSet.getInt("stock_quantity")
        );
        product.setId(resultSet.getLong("id"));
        return product;
    }
}
