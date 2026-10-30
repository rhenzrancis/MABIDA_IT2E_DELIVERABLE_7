
package com.scc;

import com.scc.dao.DatabaseInitializer;
import com.scc.dao.UserDao;
import com.scc.ui.Authentication;

import java.util.Scanner;

/**
 * Single application entry point.
 */
public final class Main {

    private Main() {
        // Prevent instantiation.
    }

    public static void main(String[] args) {
        try {
            // Initialize the database before starting the application.
            DatabaseInitializer.initialize();

            try (Scanner scanner = new Scanner(System.in)) {
                UserDao userDao = new UserDao();

                Authentication authentication =
                        new Authentication(scanner,u1serDao);

                authentication.start();
            }

        } catch (IllegalStateException exception) {
            System.out.println("System startup failed.");
            System.out.println("Please check the database configuration.");
            System.out.println("Details: " + exception.getMessage());
        }
    }
}
