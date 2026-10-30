
package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.List;
import java.util.Scanner;

public final class AdminMenu extends UserMenu {

    private static final List<String> VALID_ROLES =
            List.of("ADMIN", "CASHIER", "CUSTOMER");

    private static final List<String> VALID_STATUSES =
            List.of("APPROVED", "PENDING");

    private final UserDao userDao;

    public AdminMenu(Scanner scanner, User user) {
        super(scanner, user, "ADMIN MENU");
        this.userDao = new UserDao();
    }

    @Override
    public void show() {

        System.out.println("\nLogin successful! Welcome, "
                + user.username() + ".");

        boolean running = true;

        while (running) {

            System.out.println("\n==============================================");
            System.out.println("                  ADMIN MENU");
            System.out.println("==============================================");
            System.out.println("Username : " + user.username());
            System.out.println("Role     : " + user.roles());
            System.out.println("Status   : " + user.status());
            System.out.println("----------------------------------------------");
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("4. Update User Role and Status");
            System.out.println("5. Logout");
            System.out.println("==============================================");

            int choice = readIntInput("Choose option: ");

            switch (choice) {

                case 1:
                    displayUsers(userDao.getAllUsers());
                    break;

                case 2:
                    displayUsers(userDao.getPendingUsers());
                    break;

                case 3:
                    approveUserAccount();
                    break;

                case 4:
                    updateUserAccount();
                    break;

                case 5:
                    System.out.println("You have been logged out.");
                    running = false;
                    break;

                case 0:
                    System.out.println("No action selected.");
                    break;

                default:
                    System.out.println("Invalid choice. Please select 1-5.");
            }
        }
    }

    private void updateUserAccount() {

        List<User> users = userDao.getAllUsers();

        System.out.println("\n========== UPDATE USER ACCOUNT ==========");

        displayUsers(users);

        if (users.isEmpty()) {
            System.out.println("No user accounts found.");
            return;
        }

        System.out.println("\nEnter 0 at any prompt to cancel.");

        int targetId = readIntInput("Enter target User ID: ");

        if (targetId == 0) {
            System.out.println("Update cancelled.");
            return;
        }

        User target = users.stream()
                .filter(u -> u.id() == targetId)
                .findFirst()
                .orElse(null);

        if (target == null) {
            System.out.println("User ID not found.");
            return;
        }

        // Prevent active admin from modifying own account.
        if (target.id() == user.id()
                && "ADMIN".equalsIgnoreCase(user.roles())
                && "APPROVED".equalsIgnoreCase(user.status())) {

            System.out.println(
                    "WARNING: Modifying the currently active admin account is restricted."
            );

            return;
        }

        String newRole = readValidRole();

        if (newRole == null) {
            System.out.println("Update cancelled.");
            return;
        }

        String newStatus = readValidStatus();

        if (newStatus == null) {
            System.out.println("Update cancelled.");
            return;
        }

        boolean updated = userDao.updateUserRoleAndStatus(
                targetId,
                newRole,
                newStatus
        );

        if (updated) {
            System.out.println("\nSUCCESS: User updated successfully!");
            System.out.println("User ID : " + targetId);
            System.out.println("New Role: " + newRole);
            System.out.println("Status  : " + newStatus);
        } else {
            System.out.println("Update failed. Please try again.");
        }
    }

    // Numeric input validation
    private int readIntInput(String prompt) {

        while (true) {

            System.out.print(prompt);

            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                return 0;
            }

            try {

                int value = Integer.parseInt(input);

                if (value > 0) {
                    return value;
                }

                System.out.println(
                        "Invalid input. Enter a positive number or 0 to cancel."
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Invalid input. Please enter numbers only."
                );
            }
        }
    }

    // Role validation
    private String readValidRole() {

        while (true) {

            System.out.println("\nValid Roles: " + VALID_ROLES);
            System.out.print("Enter new role (0 to cancel): ");

            String role = scanner.nextLine()
                    .trim()
                    .toUpperCase();

            if (role.equals("0")) {
                return null;
            }

            if (VALID_ROLES.contains(role)) {
                return role;
            }

            System.out.println("Invalid role. Please try again.");
        }
    }

    // Status validation
    private String readValidStatus() {

        while (true) {

            System.out.println("\nValid Statuses: " + VALID_STATUSES);
            System.out.print("Enter new status (0 to cancel): ");

            String status = scanner.nextLine()
                    .trim()
                    .toUpperCase();

            if (status.equals("0")) {
                return null;
            }

            if (VALID_STATUSES.contains(status)) {
                return status;
            }

            System.out.println("Invalid status. Please try again.");
        }
    }

    // Approve pending account
    private void approveUserAccount() {

        List<User> pendingUsers = userDao.getPendingUsers();

        System.out.println("\n========== PENDING REGISTRATIONS ==========");

        displayUsers(pendingUsers);

        if (pendingUsers.isEmpty()) {
            System.out.println("No pending accounts found.");
            return;
        }

        int userId = readIntInput(
                "Enter User ID to approve (0 to cancel): "
        );

        if (userId == 0) {
            System.out.println("Approval cancelled.");
            return;
        }

        boolean exists = pendingUsers.stream()
                .anyMatch(u -> u.id() == userId);

        if (!exists) {
            System.out.println("User ID not found in pending list.");
            return;
        }

        if (userDao.approveUser(userId)) {
            System.out.println("User account approved successfully!");
        } else {
            System.out.println("Approval failed.");
        }
    }

    // Display users in table format
    private void displayUsers(List<User> users) {

        String border =
                "+-------+-----------------+--------------+------------+";

        System.out.println(border);

        System.out.printf(
                "| %-5s | %-15s | %-12s | %-10s |%n",
                "ID", "USERNAME", "ROLE", "STATUS"
        );

        System.out.println(border);

        if (users.isEmpty()) {

            System.out.printf(
                    "| %-5s | %-15s | %-12s | %-10s |%n",
                    "-", "No user records", "found", "-"
            );

        } else {

            for (User u : users) {

                System.out.printf(
                        "| %-5d | %-15s | %-12s | %-10s |%n",
                        u.id(),
                        fit(u.username(), 15),
                        fit(u.roles(), 12),
                        fit(u.status(), 10)
                );
            }
        }

        System.out.println(border);
        System.out.println("Total records: " + users.size());
    }

    private String fit(String value, int width) {

        if (value == null) {
            return "";
        }

        return value.length() > width
                ? value.substring(0, width)
                : value;
    }
}
