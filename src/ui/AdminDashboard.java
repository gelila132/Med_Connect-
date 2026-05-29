package ui;

import model.User;
import model.Medicine;
import service.AuthService;
import service.InventoryService;
import dao.UserDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Admin Dashboard
 */
public class AdminDashboard {
    
    private AuthService authService;
    private InventoryService inventoryService;
    private UserDAO userDAO;
    private Scanner scanner;
    
    public AdminDashboard(AuthService authService) {
        this.authService = authService;
        this.inventoryService = new InventoryService();
        this.userDAO = new UserDAO();
        this.scanner = new Scanner(System.in);
    }
    
    public void show() {
        User admin = authService.getCurrentUser();
        System.out.println("\n========================================");
        System.out.println("      ADMIN DASHBOARD                   ");
        System.out.println("========================================");
        System.out.println("Welcome, " + admin.getFullName() + "!");
        System.out.println("Role: " + admin.getRole());
        System.out.println("========================================\n");
        
        while (true) {
            System.out.println("=== ADMIN MENU ===");
            System.out.println("1. Manage Medicines");
            System.out.println("2. Manage Users");
            System.out.println("3. View All Pharmacies");
            System.out.println("4. View Inventory Reports");
            System.out.println("5. Search Medicine Availability");
            System.out.println("6. Generate Restock Report");
            System.out.println("7. Logout");
            System.out.print("\nChoose option: ");
            
            int choice = Integer.parseInt(scanner.nextLine());
            
            switch (choice) {
                case 1:
                    manageMedicines();
                    break;
                case 2:
                    manageUsers();
                    break;
                case 3:
                    viewAllPharmacies();
                    break;
                case 4:
                    viewInventoryReports();
                    break;
                case 5:
                    searchMedicineAvailability();
                    break;
                case 6:
                    generateRestockReport();
                    break;
                case 7:
                    authService.logout();
                    System.out.println("Logged out successfully!\n");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.\n");
            }
        }
    }
    
    private void manageMedicines() {
        System.out.println("\n--- Manage Medicines ---");
        System.out.println("1. View All Medicines");
        System.out.println("2. Add New Medicine");
        System.out.println("3. Search Medicines");
        System.out.print("Choose: ");
        
        int choice = Integer.parseInt(scanner.nextLine());
        
        try {
            if (choice == 1) {
                List<Medicine> medicines = inventoryService.getAllMedicines();
                System.out.println("\nID\tCode\tBrand Name\tGeneric Name\tPrice");
                System.out.println("---\t----\t----------\t------------\t-----");
                for (Medicine m : medicines) {
                    System.out.printf("%d\t%s\t%s\t%s\t$%.2f\n",
                        m.getMedicineId(), m.getMedicineCode(), m.getBrandName(),
                        m.getGenericName(), m.getUnitPrice());
                }
            } else if (choice == 2) {
                addNewMedicine();
            } else if (choice == 3) {
                System.out.print("Enter search keyword: ");
                String keyword = scanner.nextLine();
                List<Medicine> results = inventoryService.searchMedicines(keyword);
                System.out.println("\nSearch Results:");
                for (Medicine m : results) {
                    System.out.println("  - " + m.getBrandName() + " (" + m.getGenericName() + ") - $" + m.getUnitPrice());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        System.out.println();
    }
    
    private void addNewMedicine() throws SQLException {
        System.out.println("\n--- Add New Medicine ---");
        
        Medicine medicine = new Medicine();
        
        System.out.print("Medicine Code: ");
        medicine.setMedicineCode(scanner.nextLine());
        
        System.out.print("Brand Name: ");
        medicine.setBrandName(scanner.nextLine());
        
        System.out.print("Generic Name: ");
        medicine.setGenericName(scanner.nextLine());
        
        System.out.print("Manufacturer: ");
        medicine.setManufacturer(scanner.nextLine());
        
        System.out.print("Category ID: ");
        medicine.setCategoryId(Integer.parseInt(scanner.nextLine()));
        
        System.out.print("Unit Price: ");
        medicine.setUnitPrice(new java.math.BigDecimal(scanner.nextLine()));
        
        System.out.print("Dosage Form (Tablet/Capsule/Syrup): ");
        medicine.setDosageForm(scanner.nextLine());
        
        System.out.print("Strength (e.g., 500mg): ");
        medicine.setStrength(scanner.nextLine());
        
        System.out.print("Requires Prescription (true/false): ");
        medicine.setRequiresPrescription(Boolean.parseBoolean(scanner.nextLine()));
        
        if (inventoryService.addNewMedicine(medicine)) {
            System.out.println("Medicine added successfully! ID: " + medicine.getMedicineId());
        } else {
            System.out.println("Failed to add medicine.");
        }
    }
    
    private void manageUsers() {
        System.out.println("\n--- Manage Users ---");
        System.out.println("1. View All Users");
        System.out.println("2. Add New User");
        System.out.println("3. Delete User");
        System.out.print("Choose: ");
        
        int choice = Integer.parseInt(scanner.nextLine());
        
        try {
            if (choice == 1) {
                List<User> users = userDAO.getAllUsers();
                System.out.println("\nID\tUsername\tFull Name\tRole\tEmail");
                System.out.println("--\t--------\t---------\t----\t-----");
                for (User u : users) {
                    System.out.printf("%d\t%s\t%s\t%s\t%s\n",
                        u.getUserId(), u.getUsername(), u.getFullName(),
                        u.getRole(), u.getEmail());
                }
            } else if (choice == 2) {
                addNewUser();
            } else if (choice == 3) {
                System.out.print("Enter User ID to delete: ");
                int userId = Integer.parseInt(scanner.nextLine());
                if (userDAO.deleteUser(userId)) {
                    System.out.println("User deleted successfully!");
                } else {
                    System.out.println("User not found or deletion failed.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        System.out.println();
    }
    
    private void addNewUser() throws SQLException {
        System.out.println("\n--- Add New User ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        System.out.print("Full Name: ");
        String fullName = scanner.nextLine();
        
        System.out.print("Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Phone: ");
        String phone = scanner.nextLine();
        
        System.out.print("Role (ADMIN/PHARMACIST): ");
        String role = scanner.nextLine();
        
        // Create appropriate user object
        User newUser;
        if ("ADMIN".equals(role)) {
            System.out.print("Admin Level: ");
            String adminLevel = scanner.nextLine();
            System.out.print("Department: ");
            String department = scanner.nextLine();
            newUser = new model.Admin(0, username, password, fullName, email, phone, adminLevel, department);
        } else {
            System.out.print("License Number: ");
            String licenseNumber = scanner.nextLine();
            System.out.print("Pharmacy Name: ");
            String pharmacyName = scanner.nextLine();
            System.out.print("Years of Experience: ");
            int yearsExp = Integer.parseInt(scanner.nextLine());
            newUser = new model.Pharmacist(0, username, password, fullName, email, phone, licenseNumber, pharmacyName, yearsExp);
        }
        
        if (userDAO.createUser(newUser)) {
            System.out.println("User created successfully! ID: " + newUser.getUserId());
        } else {
            System.out.println("Failed to create user.");
        }
    }
    
    private void viewAllPharmacies() {
        System.out.println("\n--- Pharmacy Locations ---");
        // This would fetch from database - simplified for brevity
        System.out.println("1. City Health Pharmacy - 123 Main St");
        System.out.println("2. Wellness Rx - 456 Oak Ave");
        System.out.println("3. Downtown Medicals - 789 Pine Blvd (24/7)");
        System.out.println("4. Family Care Pharmacy - 321 Elm Street");
        System.out.println();
    }
    
    private void viewInventoryReports() {
        System.out.println("\n--- Inventory Reports ---");
        System.out.println("1. View All Pharmacies Summary");
        System.out.println("2. View Low Stock Items");
        System.out.print("Choose: ");
        
        int choice = Integer.parseInt(scanner.nextLine());
        
        try {
            if (choice == 1) {
                for (int pharmacyId = 1; pharmacyId <= 4; pharmacyId++) {
                    double totalValue = inventoryService.calculateInventoryValue(pharmacyId);
                    System.out.printf("Pharmacy %d: Total Inventory Value: $%.2f\n", pharmacyId, totalValue);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        System.out.println();
    }
    
    private void searchMedicineAvailability() {
        System.out.print("\nEnter medicine name to search: ");
        String medicineName = scanner.nextLine();
        
        try {
            List<Inventory> available = inventoryService.searchMedicineAvailability(medicineName);
            
            if (available.isEmpty()) {
                System.out.println("No pharmacies have this medicine in stock.");
            } else {
                System.out.println("\nAvailable at:");
                for (Inventory inv : available) {
                    System.out.printf("  - %s: %d units available, $%.2f each, Expires: %s\n",
                        inv.getPharmacyName(), inv.getQuantity(), inv.getUnitCost(), inv.getExpiryDate());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        System.out.println();
    }
    
    private void generateRestockReport() {
        System.out.println("\n--- Restock Report (All Pharmacies) ---");
        
        for (int pharmacyId = 1; pharmacyId <= 4; pharmacyId++) {
            try {
                List<Inventory> lowStock = inventoryService.getLowStockAlert(pharmacyId);
                if (!lowStock.isEmpty()) {
                    System.out.println("\nPharmacy " + pharmacyId + " needs restocking:");
                    for (Inventory inv : lowStock) {
                        System.out.printf("  - %s: Current stock %d, Reorder level %d\n",
                            inv.getMedicineName(), inv.getQuantity(), inv.getReorderLevel());
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error for pharmacy " + pharmacyId + ": " + e.getMessage());
            }
        }
        System.out.println();
    }
}
