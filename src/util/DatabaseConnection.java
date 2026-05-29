package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class for database connection management
 * Demonstrates: Singleton Design Pattern, Encapsulation
 */
public class DatabaseConnection {
    
    // Singleton instance
    private static DatabaseConnection instance;
    
    // Connection properties (encapsulated)
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=MedConnect;encrypt=true;trustServerCertificate=true";
    private static final String USERNAME = "sa"; // Change as per your setup
    private static final String PASSWORD = "YourStrongPassword123"; // Change as per your setup
    
    private Connection connection;
    
    // Private constructor (Singleton pattern)
    private DatabaseConnection() {
        try {
            // Load SQL Server JDBC Driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
    }
    
    // Public method to get singleton instance
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    // Get database connection
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
        return connection;
    }
    
    // Close connection
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    // Test connection
    public boolean testConnection() {
        try {
            getConnection();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
