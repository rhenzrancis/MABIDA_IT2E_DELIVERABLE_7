package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Optional;
import java.util.Scanner;

/** Central handler for registration, login, and role-based UI routing. */
public class Authentication {
    private final Scanner scanner;
    private final UserDao userDao;

    public Authentication(Scanner scanner, UserDao userDao) {
        this.scanner = scanner;
        this.userDao = userDao;
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n=====================================");
            System.out.println("       CUP AND CORNER CAFE");
            System.out.println("=====================================");
            System.out.println("[1] Register Account");
            System.out.println("[2] Login");
            System.out.println("[3] Exit");
            System.out.print("Choose: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> register();
                case "2" -> login();
                case "3" -> {
                    System.out.println("Thank you for using CUP AND CORNER CAFE. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
    }

    private void register() {
        System.out.println("\n--- Register Account ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Username and password cannot be empty.");
            return;
        }
        if (userDao.usernameExists(username)) {
            System.out.println("Username already taken. Try another one.");
            return;
        }

        String role = chooseRole();
        if (role == null) {
            System.out.println("Invalid role. Registration cancelled.");
            return;
        }

        if (userDao.register(username, password, role)) {
            System.out.println("Account registered! Role: " + role + " | Status: PENDING");
            System.out.println("Please wait for an administrator to approve your account.");
        } else {
            System.out.println("Registration failed. Please try again.");
        }
    }

    private String chooseRole() {
        System.out.println("Select role:");
        System.out.println("[1] ADMIN");
        System.out.println("[2] CASHIER");
        System.out.println("[3] CUSTOMER");
        System.out.print("Choose: ");
        return switch (scanner.nextLine().trim()) {
            case "1" -> "ADMIN";
            case "2" -> "CASHIER";
            case "3" -> "CUSTOMER";
            default -> null;
        };
    }

    private void login() {
        System.out.println("\n--- Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        Optional<User> result = userDao.login(username, password);
        if (result.isEmpty()) {
            System.out.println("Invalid username or password.");
            return;
        }

        User user = result.get();
        routeUser(user);
    }

    private void routeUser(User user) {
        switch (user.roles().trim().toUpperCase()) {
            case "ADMIN" -> new AdminMenu(scanner, user).show();
            case "CASHIER" -> new CashierMenu(scanner, user).show();
            case "CUSTOMER" -> new CustomerMenu(scanner, user).show();
            default -> System.out.println("No menu is configured for role: " + user.roles());
        }
    }
}
