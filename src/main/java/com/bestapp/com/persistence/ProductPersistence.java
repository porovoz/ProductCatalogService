package com.bestapp.com.persistence;

import com.bestapp.com.model.Product;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving and loading products to/from a CSV file.
 * Fields order in CSV:
 * id;name;description;price;category;brand;stockQuantity
 */
public class ProductPersistence {

    private static final String FILE_PATH = "products.csv";
    private static final String DELIMITER = ";";

    private ProductPersistence() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Saves a list of products to a CSV file.
     *
     * @param products list of products to save.
     */
    public static void saveProducts(List<Product> products) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("id;name;description;price;category;brand;stockQuantity");
            writer.newLine();
            for (Product p : products) {
                writer.write(String.join(DELIMITER,
                        p.getId(),
                        escape(p.getName()),
                        escape(p.getDescription()),
                        String.valueOf(p.getPrice()),
                        escape(p.getCategory()),
                        escape(p.getBrand()),
                        String.valueOf(p.getStockQuantity())));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving products: " + e.getMessage());
        }
    }

    /**
     * Loads products from a CSV file.
     *
     * @return list of loaded products.
     */
    public static List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return products;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String header = reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(DELIMITER);
                if (data.length == 7) {
                    Product p = new Product(data[1], data[2],
                            Double.parseDouble(data[3]), data[4], data[5], Integer.parseInt(data[6]));
                    p.setId(data[0]);
                    products.add(p);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading products: " + e.getMessage());
        }
        return products;
    }

    /**
     * Escapes delimiter characters in CSV fields by replacing them with comma.
     *
     * @param text field value
     * @return escaped field
     */
    private static String escape(String text) {
        return text.replace(DELIMITER, ",");
    }

}
