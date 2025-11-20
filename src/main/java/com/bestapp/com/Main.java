package com.bestapp.com;

import com.bestapp.com.audit.AuditLogger;
import com.bestapp.com.controller.AuthController;
import com.bestapp.com.controller.ProductController;
import com.bestapp.com.factory.AppFactory;
import com.bestapp.com.menu.AppMenu;
import com.bestapp.com.metrics.Metrics;
import com.bestapp.com.view.ConsoleView;


/**
 * Entry point for the Product Catalog Application.
 * The {@code Main} class is responsible for initializing and starting the console-based
 * Product Catalog application. It sets up the necessary components such as controllers, views,
 * metrics, and logging mechanisms, and then launches the application's main menu.
 * The application allows users to manage products in a catalog, perform authentication, and
 * view metrics related to the system's operation.
 * This class serves as the main entry point and invokes the application's main menu to start
 * the user interaction loop.
 */
public class Main {

    /**
     * Starts the Product Catalog console application.
     * This method is the entry point for the application. It initializes all the necessary components
     * such as controllers, views, metrics, and logging. Once initialized, it creates an instance of
     * the {@link AppMenu} class and invokes its {@link AppMenu#start()} method to begin the user
     * interaction within the application.
     */
    public static void main(String[] args) {

        AppFactory appFactory = new AppFactory();

        ProductController productController = appFactory.createProductController();
        AuthController authController = appFactory.createAuthController();
        ConsoleView consoleView = appFactory.getConsoleView();
        Metrics metrics = appFactory.getMetrics();
        AuditLogger auditLogger = appFactory.getAuditLogger();

        AppMenu appMenu = new AppMenu(
                authController,
                productController,
                consoleView,
                metrics,
                auditLogger,
                appFactory.getAuthService());

        appMenu.start();
    }

}