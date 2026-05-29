package service;

import dao.InventoryDAO;
import dao.MedicineDAO;
import dao.PharmacyDAO;
import model.Inventory;
import model.Medicine;
import model.Pharmacy;

import java.sql.SQLException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Report Service - Generates various reports
 * Demonstrates: Business logic aggregation
 */
public class ReportService {
    
    private InventoryDAO inventoryDAO;
    private MedicineDAO medicineDAO;
    private PharmacyDAO pharmacyDAO;
    
    public ReportService() {
        this.inventoryDAO = new InventoryDAO();
        this.medicineDAO = new MedicineDAO();
        this.pharmacyDAO = new PharmacyDAO();
    }
    
    // Generate inventory summary report
    public void generateInventorySummary() throws SQLException {
        System.out.println("\n========== INVENTORY SUMMARY REPORT ==========");
        System.out.printf("%-25s %-15s %-15s %-15s\n", 
            "Pharmacy", "Total Items", "Total Units", "Total Value");
        System.out.println("--------------------------------------------------------");
        
        List<Pharmacy> pharmacies = pharmacyDAO.getAllPharmacies();
        
        for (Pharmacy pharmacy : pharmacies) {
            List<Inventory> inventory = inventoryDAO.getInventoryByPharmacy(pharmacy.getPharmacyId());
            
            int totalItems = inventory.size();
            int totalUnits = inventory.stream().mapToInt(Inventory::getQuantity).sum();
            double totalValue = inventory.stream()
                .mapToDouble(i -> i.getTotalValue().doubleValue())
                .sum();
            
            System.out.printf("%-25s %-15d %-15d $%-14.2f\n",
                pharmacy.getName(), totalItems, totalUnits, totalValue);
        }
        System.out.println("========================================================\n");
    }
    
    // Generate low stock report
    public void generateLowStockReport() throws SQLException {
        System.out.println("\n========== LOW STOCK REPORT ==========");
        
        List<Pharmacy> pharmacies = pharmacyDAO.getAllPharmacies();
        boolean hasLowStock = false;
        
        for (Pharmacy pharmacy : pharmacies) {
            List<Inventory> lowStock = inventoryDAO.getLowStockItems(pharmacy.getPharmacyId());
            
            if (!lowStock.isEmpty()) {
                hasLowStock = true;
                System.out.println("\nPharmacy: " + pharmacy.getName());
                System.out.println("----------------------------------------");
                for (Inventory item : lowStock) {
                    System.out.printf("  - %s: %d units (Reorder at %d)\n",
                        item.getMedicineName(), item.getQuantity(), item.getReorderLevel());
                }
            }
        }
        
        if (!hasLowStock) {
            System.out.println("No low stock items in any pharmacy.");
        }
        System.out.println("\n========================================\n");
    }
    
    // Generate expiring medicines report
    public void generateExpiringReport(int daysThreshold) throws SQLException {
        System.out.println("\n========== EXPIRING MEDICINES REPORT ==========");
        System.out.println("(Expiring within " + daysThreshold + " days)\n");
        
        List<Pharmacy> pharmacies = pharmacyDAO.getAllPharmacies();
        boolean hasExpiring = false;
        
        for (Pharmacy pharmacy : pharmacies) {
            List<Inventory> inventory = inventoryDAO.getInventoryByPharmacy(pharmacy.getPharmacyId());
            
            List<Inventory> expiring = new java.util.ArrayList<>();
            java.util.Date today = new java.util.Date();
            long thresholdMillis = daysThreshold * 24L * 60 * 60 * 1000;
            
            for (Inventory item : inventory) {
                long daysUntilExpiry = (item.getExpiryDate().getTime() - today.getTime()) / (1000 * 60 * 60 * 24);
                if (daysUntilExpiry <= daysThreshold && daysUntilExpiry > 0 && item.getQuantity() > 0) {
                    expiring.add(item);
                }
            }
            
            if (!expiring.isEmpty()) {
                hasExpiring = true;
                System.out.println("\nPharmacy: " + pharmacy.getName());
                System.out.println("----------------------------------------");
                for (Inventory item : expiring) {
                    long daysLeft = (item.getExpiryDate().getTime() - today.getTime()) / (1000 * 60 * 60 * 24);
                    System.out.printf("  - %s: %d units, Expires: %s (%d days left)\n",
                        item.getMedicineName(), item.getQuantity(), 
                        item.getExpiryDate(), daysLeft);
                }
            }
        }
        
        if (!hasExpiring) {
            System.out.println("No medicines expiring within " + daysThreshold + " days.");
        }
        System.out.println("\n================================================\n");
    }
    
    // Generate category distribution report
    public void generateCategoryReport() throws SQLException {
        System.out.println("\n========== CATEGORY DISTRIBUTION REPORT ==========");
        
        List<Medicine> medicines = medicineDAO.getAllMedicines();
        Map<String, Integer> categoryCount = new HashMap<>();
        Map<String, Double> categoryValue = new HashMap<>();
        
        for (Medicine medicine : medicines) {
            String category = medicine.getCategoryName();
            categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
            categoryValue.put(category, categoryValue.getOrDefault(category, 0.0) + medicine.getUnitPrice().doubleValue());
        }
        
        System.out.printf("%-20s %-15s %-15s\n", "Category", "Medicine Count", "Avg Price");
        System.out.println("------------------------------------------------");
        
        for (String category : categoryCount.keySet()) {
            double avgPrice = categoryValue.get(category) / categoryCount.get(category);
            System.out.printf("%-20s %-15d $%-14.2f\n",
                category, categoryCount.get(category), avgPrice);
        }
        System.out.println("==================================================\n");
    }
    
    // Generate supplier performance report
    public void generateSupplierReport() throws SQLException {
        System.out.println("\n========== SUPPLIER PERFORMANCE REPORT ==========");
        System.out.printf("%-25s %-15s %-15s\n", "Supplier", "Rating", "Status");
        System.out.println("------------------------------------------------");
        
        // Note: This would fetch from Supplier table
        System.out.printf("%-25s %-15s %-15s\n", "PharmaDistro Inc.", "4.8", "Active");
        System.out.printf("%-25s %-15s %-15s\n", "MedSupply Global", "4.5", "Active");
        System.out.printf("%-25s %-15s %-15s\n", "HealthLogistics LLC", "4.2", "Active");
        System.out.printf("%-25s %-15s %-15s\n", "DirectMed Pharma", "4.9", "Active");
        System.out.println("==================================================\n");
    }
}
