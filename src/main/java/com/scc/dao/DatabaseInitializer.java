
package com.scc.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseInitializer {

    private DatabaseInitializer() {
        // Prevent instantiation.
    }

    public static void initialize() {

        String createUsers = """
                CREATE TABLE IF NOT EXISTS users (
                    id       INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    roles    TEXT NOT NULL,
                    status   TEXT NOT NULL DEFAULT 'PENDING'
                )
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            // Create the users table only if it does not already exist.
            statement.execute(createUsers);

            // Verify SQLite foreign-key enforcement.
            try (var result = statement.executeQuery(
                    "PRAGMA foreign_keys")) {

                if (!result.next() || result.getInt(1) != 1) {
                    throw new SQLException(
                            "SQLite foreign-key enforcement is disabled."
                    );
                }
            }

            System.out.println("Database initialized successfully.");

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Database initialization failed: "
                            + exception.getMessage(),
                    exception
            );
        }
    }
}
