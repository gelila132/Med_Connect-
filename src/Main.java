import ui.LoginUI;
import ui.CustomerUI;
import util.DatabaseConnection;

import java.util.Scanner;

/**
 * Main entry point for Med-Connect Application
 * Demonstrates: Application initialization and startup
 */
public class Main {
    
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                                                          ║");
        System.out.println("║           WELCOME TO MED-CONNECT SYSTEM                  ║");
        System.out.println("║      Pharmaceutical Inventory & Logistics Engine        ║");
        System.out.println("║                                                          ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Test database connection
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        
        if (dbConnection.testConnection()) {
            System.out.println("✓ Database connection established successfully!");
            System.out.println();
            
            while (true) {
                System.out.println("┌─────────────────────────────────────────────────────┐");
                System.out.println("│                    MAIN MENU                         │");
                System.out.println("├─────────────────────────────────────────────────────┤");
                System.out.println("│  1. Login (Admin/Pharmacist)                        │");
                System.out.println("│  2. Customer Portal (Medicine Lookup)               │");
                System.out.println("│  3. Exit                                             │");
                System.out.println("└─────────────────────────────────────────────────────┘");
                System.out.print("\nChoose option: ");
                
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        LoginUI loginUI = new LoginUI();
                        loginUI.show();
                        break;
                    case 2:
                        CustomerUI customerUI = new CustomerUI();
                        customerUI.show();
                        break;
                    case 3:
                        System.out.println("\nThank you for using Med-Connect. Goodbye!");
                        dbConnection.closeConnection();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.\n");
                }
            }
        } else {
            System.err.println("✗ ERROR: Cannot connect to database!");
            System.err.println("\nPlease check:");
            System.err.println("  1. SQL Server is running");
            System.err.println("  2. Database 'MedConnect' exists");
            System.err.println("  3. Connection credentials in DatabaseConnection.java are correct");
            System.err.println("  4. JDBC driver (mssql-jdbc.jar) is in classpath");
            System.err.println("\nPress Enter to exit...");
            scanner.nextLine();
        }
        
        scanner.close();
    }
}
