package com.bestapp.com;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

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

    //    /**
//     * Starts the Product Catalog console application.
//     * This method is the entry point for the application. It initializes all the necessary components
//     * such as controllers, views, metrics, and logging. Once initialized, it creates an instance of
//     * the {@link AppMenu} class and invokes its {@link AppMenu#start()} method to begin the user
//     * interaction within the application.
//     */
    public static void main(String[] args) throws Exception {

//        AppFactory appFactory = new AppFactory();
//
//        ProductController productController = appFactory.createProductController();
//        AuthController authController = appFactory.createAuthController();
//        ConsoleView consoleView = appFactory.getConsoleView();
//        Metrics metrics = appFactory.getMetrics();
//        AuditLogger auditLogger = appFactory.getAuditLogger();
//
//        AppMenu appMenu = new AppMenu(
//                authController,
//                productController,
//                consoleView,
//                metrics,
//                auditLogger,
//                appFactory.getAuthService());
//
//        appMenu.start();


//        int port = 8080;
//        String contextPath = "";
//        String webappDirLocation = "src/main/webapp/";
//
//        // Включаем AspectJ LTW через системное свойство
//        System.setProperty("org.aspectj.weaver.loadtime.configuration", "META-INF/aop.xml");
//
//        Tomcat tomcat = new Tomcat();
//        tomcat.setPort(port);
//
//        // Создаем контекст
//        Context ctx = tomcat.addWebapp(contextPath, new File(webappDirLocation).getAbsolutePath());
//
//        // Настраиваем ресурсы для /WEB-INF/classes
//        StandardRoot resources = new StandardRoot(ctx);
//        File additionWebInfClasses = new File("target/classes");
//        resources.addPreResources(new DirResourceSet(
//                resources,
//                "/WEB-INF/classes",
//                additionWebInfClasses.getAbsolutePath(),
//                "/"
//        ));
//        ctx.setResources(resources);
//
//        // Устанавливаем WebappLoader (Tomcat 11)
//        org.apache.catalina.loader.WebappLoader loader = new org.apache.catalina.loader.WebappLoader();
//        loader.setLoaderClass("org.apache.catalina.loader.WebappLoader"); // имя класса загрузчика
//        ctx.setLoader(loader);
//
//        // Запускаем Tomcat
//        tomcat.start();
//        System.out.println("Tomcat started at http://localhost:" + port);
//        tomcat.getServer().await();
    }

}