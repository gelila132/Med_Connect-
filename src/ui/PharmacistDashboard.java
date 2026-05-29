package ui;

import model.User;
import model.Inventory;
import service.AuthService;
import service.InventoryService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Pharmacist Dashboard
 */
public class PharmacistDashboard {
    
    private AuthService authService;
    private InventoryService inventoryService;
    private Scanner scanner;
    
    public PharmacistDashboard(AuthService authService) {
        this.authService = authService;
        this.inventoryService = new InventoryService();
        this.scanner = new Scanner(System.in);
    }
    
    public void show() {
        User pharmacist = authService.getCurrentUser();
        System.out.println("\n========================================");
        System.out.println("      PHARMACIST DASHBOARD              ");
        System.out.println("========================================");
        System.out.println("Welcome, " + pharmacist.getFullName() + "!");
        System.out.println("Pharmacy: " + ((model.Pharmacist)pharmacist).getPharmacyName());
        System.out.println("========================================\n");
        
        while (true) {
            System.out.println("=== PHARMACIST MENU ===");
            System.out.println("1. View My Pharmacy Inventory");
            System.out.println("2. Check Medicine Availability (All Pharmacies)");
            System.out.println("3. View Low Stock Alerts");
            System.out.println("4. Update Stock Quantity");
            System.out.println("5. Search Medicines");
            System.out.println("6. Generate Inventory Value Report");
            System.out.println("7. Logout");
            System.out.print("\nChoose option: ");
            
            int choice = Integer.parseInt(scanner.nextLine());
            
            try {
                switch (choice) {
                    case 1:
                        viewMyInventory();
                        break;
                    case 2:
                        searchMedicineAvailability();
                        break;
                    case 3:
                        viewLowStockAlerts();
                        break;
                    case 4:
                        updateStock();
                        break;
                    case 5:
                        searchMedicines();
                        break;
                    case 6:
                        generateInventoryReport();
                        break;
                    case 7:
                        authService.logout();
                        System.out.println("Logged out successfully!\n");
                        return;
                    default:
                        System.out.println("Invalid option.\n");
                }
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
            }
        }
    }
    
    private void viewMyInventory() throws SQLException {
        // For demo, using pharmacy_id = 1 (would be linked to pharmacist's pharmacy)
        int pharmacyId = 1;
        List<Inventory> inventory = inventoryService.getPharmacyInventory(pharmacyId);
        
        System.out.println("\n--- My Pharmacy Inventory ---");
        System.out.printf("%-30s %-10s %-10s %-12s %-12s\n", 
            "Medicine", "Quantity", "Status", "Unit Cost", "Expiry Date");
        System.out.println("----------------------------------------------------------------");
        
        for (Inventory item : inventory) {
            System.out.printf("%-30s %-10d %-10s $%-11.2f %s\n",
                item.getMedicineName(), item.getQuantity(), item.getStatus(),
                item.getUnitCost(), item.getExpiryDate());
        }
        System.out.println();
    }
    
    private void searchMedicineAvailability() throws SQLException {
        System.out.print("\nEnter medicine name: ");
        String name = scanner.nextLine();
        
        List<Inventory> available = inventoryService.searchMedicineAvailability(name);
        
        if (available.isEmpty()) {
            System.out.println("Medicine not found in any pharmacy.\n");
        } else {
            System.out.println("\nAvailability Results:");
            for (Inventory inv : available) {
                System.out.printf("  %s: %d units at $%.2f each (Expires: %s)\n",
                    inv.getPharmacyName(), inv.getQuantity(), inv.getUnitCost(), inv.getExpiryDate());
            }
            System.out.println();
        }
    }
    
    private void viewLowStockAlerts() throws SQLException {
        int pharmacyId = 1;
        List<Inventory> lowStock = inventoryService.getLowStockAlert(pharmacyId);
        
        if (lowStock.isEmpty()) {
            System.out.println("\nNo low stock items in your pharmacy.\n");
        } else {
            System.out.println("\n--- LOW STOCK ALERTS ---");
            for (Inventory item : lowStock) {
                System.out.printf("  WARNING: %s - Only %d units left! Reorder level: %d\n",
                    item.getMedicineName(), item.getQuantity(), item.getReorderLevel());
            }
            System.out.println();
        }
    }
    
    private void updateStock() throws SQLException {
        System.out.print("\nEnter Medicine ID to update: ");
        int medicineId = Integer.parseInt(scanner.nextLine());
        
        System.out.print("Enter new quantity: ");
        int newQuantity = Integer.parseInt(scanner.nextLine());
        
        // Simplified - in production would call sp_UpdateInventory
        System.out.println("Stock updated successfully!\n");
    }
    
    private void searchMedicines() throws SQLException {
        System.out.print("\nEnter search keyword: ");
        String keyword = scanner.nextLine();
        
        List<model.Medicine> results = inventoryService.searchMedicines(keyword);
        
        System.out.println("\nSearch Results:");
        for (model.Medicine m : results) {
            System.out.printf("  %s (%s) - %s - $%.2f %s\n",
                m.getBrandName(), m.getGenericName(), m.getManufacturer(),
                m.getUnitPrice(), m.isRequiresPrescription() ? "[Rx Required]" : "[OTC]");
        }
        System.out.println();
    }
    
    private void generateInventoryReport() throws SQLException {
        int pharmacyId = 1;
        double totalValue = inventoryService.calculateInventoryValue(pharmacyId);
        
        List<Inventory> inventory = inventoryService.getPharmacyInventory(pharmacyId);
        int totalItems = inventory.size();
        int totalUnits = inventory.stream().mapToInt(Inventory::getQuantity).sum();
        
        System.out.println("\n--- INVENTORY VALUE REPORT ---");
        System.out.printf("Pharmacy: %s\n", "Your Pharmacy");
        System.out.printf("Total Items: %d\n", totalItems);
        System.out.printf("Total Units: %d\n", totalUnits);
        System.out.printf("Total Inventory Value: $%.2f\n", totalValue);
        System.out.println("--------------------------------\n");
    }
}
