package com.scc.ui;

import com.scc.model.User;
import java.util.Scanner;

/** Shared dashboard behavior used by the role-specific menus. */
public class UserMenu {
    protected final Scanner scanner;
    protected final User user;
    private final String menuTitle;

    public UserMenu(Scanner scanner, User user) {
        this(scanner, user, "Dashboard");
    }

    protected UserMenu(Scanner scanner, User user, String menuTitle) {
        this.scanner = scanner;
        this.user = user;
        this.menuTitle = menuTitle;
    }

    public void show() {
        System.out.println("\nLogin successful! Welcome, " + user.username() + ".");
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- " + menuTitle + " ---");
            System.out.println("Username : " + user.username());
            System.out.println("Role     : " + user.roles());
            System.out.println("Status   : " + user.status());
            System.out.println("[1] Logout");
            System.out.print("Choose: ");
            if (scanner.nextLine().trim().equals("1")) {
                System.out.println("You have been logged out.");
                loggedIn = false;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }
}
