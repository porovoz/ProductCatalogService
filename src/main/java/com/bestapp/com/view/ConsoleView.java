package com.bestapp.com.view;

import com.bestapp.com.model.Product;

import java.util.List;
import java.util.Scanner;

/**
 * Console-based view responsible for all user input and output.
 */
public class ConsoleView {

    private final Scanner scanner = new Scanner(System.in);

    /**
     * Reads a line of text from the user.
     */
    public String read(String msg) {
        System.out.print(msg);
        return scanner.nextLine().trim();
    }

    /**
     * Reads a text with validation.
     */
    public String readNonEmptyText(String msg) {
        while (true) {
            try {
                String input = read(msg);
                if (!input.isBlank()) {
                    return input;
                }
                System.out.println("Input cannot be empty. Try again.");
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input, try again.");
            }
        }
    }

    /**
     * Reads an integer value with validation.
     */
    public int readPositiveInt(String msg) {
        while (true) {
            try {
                int value = Integer.parseInt(read(msg));
                if (value >= 0) {
                    return value;
                }
                System.out.println("Quantity cannot be negative. Try again.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, try again.");
            }
        }
    }

    /**
     * Reads a double value with validation.
     */
    public double readPositiveDouble(String msg) {
        while (true) {
            try {
                double value = Double.parseDouble(read(msg));
                if (value >= 0) {
                    return value;
                }
                System.out.println("Price cannot be negative. Try again.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, try again.");
            }
        }
    }

    /**
     * Displays a message.
     */
    public void showMessage(String msg) { System.out.println(msg); }

    /**
     * Displays a list of all products.
     */
    public void showProducts(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }
        products.forEach(p -> System.out.printf("ID: %s | Name: %s | Price: %.2f | Category: %s | Brand: %s | Quantity: %d%n",
                p.getId(), p.getName(), p.getPrice(), p.getCategory(), p.getBrand(), p.getStockQuantity()));
    }

}
