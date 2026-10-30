
package com.scc.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:SalesRecords.db";

    private DatabaseConnection() {
        // Prevent instantiation.
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL);

        try (Statement statement = connection.createStatement()) {

            // Enable foreign keys for this connection.
            statement.execute("PRAGMA foreign_keys = ON");

            // Verify that foreign keys are enabled.
            try (var result = statement.executeQuery("PRAGMA foreign_keys")) {
                if (!result.next() || result.getInt(1) != 1) {
                    throw new SQLException(
                            "Failed to enable SQLite foreign keys."
                    );
                }
            }

            return connection;

        } catch (SQLException exception) {
            try {
                connection.close();
            } catch (SQLException closeException) {
                exception.addSuppressed(closeException);
            }

            throw exception;
        }
    }
}
