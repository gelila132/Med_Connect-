import ui.LoginUI;
import util.DatabaseConnection;

/**
 * Main entry point for Med-Connect Application
 * Demonstrates: Application initialization and startup
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("Starting Med-Connect System...");
        
        // Test database connection
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        
        if (dbConnection.testConnection()) {
            System.out.println("Database connection successful!");
            System.out.println();
            
            // Launch the login UI
            LoginUI loginUI = new LoginUI();
            loginUI.show();
        } else {
            System.err.println("ERROR: Cannot connect to database!");
            System.err.println("Please check:");
            System.err.println("1. SQL Server is running");
            System.err.println("2. Database 'MedConnect' exists");
            System.err.println("3. Connection credentials in DatabaseConnection.java are correct");
            System.err.println("4. JDBC driver (mssql-jdbc.jar) is in classpath");
        }
        
        // Close database connection
        dbConnection.closeConnection();
    }
}
