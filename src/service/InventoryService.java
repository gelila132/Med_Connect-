package service;

import dao.InventoryDAO;
import dao.MedicineDAO;
import model.Inventory;
import model.Medicine;

import java.sql.SQLException;
import java.util.List;

/**
 * Inventory Service - Business logic for inventory operations
 */
public class InventoryService {
    
    private InventoryDAO inventoryDAO;
    private MedicineDAO medicineDAO;
    
    public InventoryService() {
        this.inventoryDAO = new InventoryDAO();
        this.medicineDAO = new MedicineDAO();
    }
    
    public List<Inventory> getPharmacyInventory(int pharmacyId) throws SQLException {
        return inventoryDAO.getInventoryByPharmacy(pharmacyId);
    }
    
    public List<Inventory> searchMedicineAvailability(String medicineName) throws SQLException {
        return inventoryDAO.checkAvailability(medicineName);
    }
    
    public List<Inventory> getLowStockAlert(int pharmacyId) throws SQLException {
        return inventoryDAO.getLowStockItems(pharmacyId);
    }
    
    public List<Medicine> getAllMedicines() throws SQLException {
        return medicineDAO.getAllMedicines();
    }
    
    public List<Medicine> searchMedicines(String keyword) throws SQLException {
        return medicineDAO.searchMedicines(keyword);
    }
    
    public boolean addNewMedicine(Medicine medicine) throws SQLException {
        // Business validation
        if (medicine.getBrandName() == null || medicine.getBrandName().trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine name cannot be empty");
        }
        if (medicine.getUnitPrice().doubleValue() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        return medicineDAO.addMedicine(medicine);
    }
    
    public double calculateInventoryValue(int pharmacyId) throws SQLException {
        List<Inventory> inventory = inventoryDAO.getInventoryByPharmacy(pharmacyId);
        double total = 0;
        for (Inventory item : inventory) {
            total += item.getTotalValue().doubleValue();
        }
        return total;
    }
}
