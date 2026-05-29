package ui;

import service.AuthService;
import model.User;

import java.util.Scanner;

/**
 * Login UI - Console-based interface
 * Demonstrates: User interaction, MVC pattern
 */
public class LoginUI {
    
    private AuthService authService;
    private Scanner scanner;
    
    public LoginUI() {
        this.authService = new AuthService();
        this.scanner = new Scanner(System.in);
    }
    
    public void show() {
        System.out.println("========================================");
        System.out.println("      WELCOME TO MED-CONNECT SYSTEM     ");
        System.out.println("========================================");
        System.out.println();
        
        while (true) {
            System.out.println("1. Login");
            System.out.println("2. Exit");
            System.out.print("Choose option: ");
            
            int choice = Integer.parseInt(scanner.nextLine());
            
            if (choice == 1) {
                if (handleLogin()) {
                    // After successful login, redirect to appropriate dashboard
                    User user = authService.getCurrentUser();
                    if (user.getRole().equals("ADMIN")) {
                        AdminDashboard adminDashboard = new AdminDashboard(authService);
                        adminDashboard.show();
                    } else if (user.getRole().equals("PHARMACIST")) {
                        PharmacistDashboard pharmacistDashboard = new PharmacistDashboard(authService);
                        pharmacistDashboard.show();
                    }
                } else {
                    System.out.println("Login failed! Invalid username or password.\n");
                }
            } else if (choice == 2) {
                System.out.println("Thank you for using Med-Connect. Goodbye!");
                break;
            } else {
                System.out.println("Invalid option. Please try again.\n");
            }
        }
        scanner.close();
    }
    
    private boolean handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        return authService.login(username, password);
    }
}
