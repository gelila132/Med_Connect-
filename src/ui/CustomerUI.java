package ui;

import model.Medicine;
import model.Inventory;
import service.InventoryService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Customer UI - For public medicine lookup
 * No login required
 */
public class CustomerUI {
    
    private InventoryService inventoryService;
    private Scanner scanner;
    
    public CustomerUI() {
        this.inventoryService = new InventoryService();
        this.scanner = new Scanner(System.in);
    }
    
    public void show() {
        System.out.println("\n========================================");
        System.out.println("      MED-CONNECT - PUBLIC PORTAL       ");
        System.out.println("========================================");
        System.out.println("    Find Medicines Across Pharmacies    ");
        System.out.println("========================================\n");
        
        while (true) {
            System.out.println("=== MENU ===");
            System.out.println("1. Search Medicine Availability");
            System.out.println("2. Browse All Medicines");
            System.out.println("3. Find Nearest Pharmacy");
            System.out.println("4. Exit to Main Menu");
            System.out.print("\nChoose option: ");
            
            int choice = Integer.parseInt(scanner.nextLine());
            
            try {
                switch (choice) {
                    case 1:
                        searchMedicine();
                        break;
                    case 2:
                        browseMedicines();
                        break;
                    case 3:
                        findNearestPharmacy();
                        break;
                    case 4:
                        System.out.println("Returning to main menu...\n");
                        return;
                    default:
                        System.out.println("Invalid option.\n");
                }
            } catch (SQLException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
    
    private void searchMedicine() throws SQLException {
        System.out.print("\nEnter medicine name: ");
        String name = scanner.nextLine();
        
        List<Inventory> available = inventoryService.searchMedicineAvailability(name);
        
        if (available.isEmpty()) {
            System.out.println("\nNo pharmacies currently have this medicine in stock.\n");
        } else {
            System.out.println("\n--- AVAILABLE AT ---");
            for (Inventory inv : available) {
                System.out.println("\nPharmacy: " + inv.getPharmacyName());
                System.out.println("  Location: " + getPharmacyLocation(inv.getPharmacyId()));
                System.out.println("  Quantity: " + inv.getQuantity() + " units");
                System.out.println("  Price: $" + inv.getUnitCost());
                System.out.println("  Expiry Date: " + inv.getExpiryDate());
                System.out.println("  Contact: " + getPharmacyContact(inv.getPharmacyId()));
            }
            System.out.println();
        }
    }
    
    private void browseMedicines() throws SQLException {
        List<Medicine> medicines = inventoryService.getAllMedicines();
        
        System.out.println("\n--- ALL MEDICINES IN CATALOG ---");
        System.out.printf("%-5s %-25s %-25s %-10s\n", "ID", "Brand Name", "Generic Name", "Price");
        System.out.println("--------------------------------------------------------------");
        
        for (Medicine m : medicines) {
            System.out.printf("%-5d %-25s %-25s $%-10.2f\n",
                m.getMedicineId(), 
                truncate(m.getBrandName(), 25),
                truncate(m.getGenericName(), 25),
                m.getUnitPrice());
        }
        System.out.println();
        
        System.out.print("Enter medicine ID to check availability (or 0 to exit): ");
        int medId = Integer.parseInt(scanner.nextLine());
        
        if (medId > 0) {
            checkSpecificMedicineAvailability(medId);
        }
    }
    
    private void checkSpecificMedicineAvailability(int medicineId) throws SQLException {
        Medicine medicine = null;
        List<Medicine> all = inventoryService.getAllMedicines();
        for (Medicine m : all) {
            if (m.getMedicineId() == medicineId) {
                medicine = m;
                break;
            }
        }
        
        if (medicine == null) {
            System.out.println("Medicine not found.\n");
            return;
        }
        
        System.out.println("\nChecking availability for: " + medicine.getBrandName());
        List<Inventory> available = inventoryService.searchMedicineAvailability(medicine.getBrandName());
        
        if (available.isEmpty()) {
            System.out.println("Not available at any pharmacy.\n");
        } else {
            for (Inventory inv : available) {
                System.out.printf("  %s: %d units available at $%.2f\n",
                    inv.getPharmacyName(), inv.getQuantity(), inv.getUnitCost());
            }
            System.out.println();
        }
    }
    
    private void findNearestPharmacy() {
        System.out.println("\n--- PHARMACY LOCATIONS ---");
        System.out.println("1. City Health Pharmacy - 123 Main St, Anytown");
        System.out.println("   Hours: Mon-Fri 8am-8pm, Sat 9am-5pm");
        System.out.println("   Phone: 555-0100");
        System.out.println();
        System.out.println("2. Wellness Rx - 456 Oak Ave, Sometown");
        System.out.println("   Hours: Mon-Sun 7am-11pm");
        System.out.println("   Phone: 555-0200");
        System.out.println();
        System.out.println("3. Downtown Medicals - 789 Pine Blvd, Yourcity");
        System.out.println("   Hours: 24/7");
        System.out.println("   Phone: 555-0300");
        System.out.println();
        System.out.println("4. Family Care Pharmacy - 321 Elm Street, Othertown");
        System.out.println("   Hours: Mon-Fri 9am-7pm, Sat 10am-3pm");
        System.out.println("   Phone: 555-0400");
        System.out.println();
    }
    
    // Helper methods
    private String getPharmacyLocation(int pharmacyId) {
        switch (pharmacyId) {
            case 1: return "123 Main St, Anytown, ST 12345";
            case 2: return "456 Oak Ave, Sometown, ST 67890";
            case 3: return "789 Pine Blvd, Yourcity, ST 11223";
            case 4: return "321 Elm Street, Othertown, ST 44556";
            default: return "Address not available";
        }
    }
    
    private String getPharmacyContact(int pharmacyId) {
        switch (pharmacyId) {
            case 1: return "555-0100";
            case 2: return "555-0200";
            case 3: return "555-0300";
            case 4: return "555-0400";
            default: return "Contact not available";
        }
    }
    
    private String truncate(String str, int length) {
        if (str == null) return "";
        if (str.length() <= length) return str;
        return str.substring(0, length - 3) + "...";
    }
}
