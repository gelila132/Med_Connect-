package model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Inventory entity class
 */
public class Inventory {
    
    private int inventoryId;
    private int pharmacyId;
    private String pharmacyName;
    private int medicineId;
    private String medicineName;
    private int quantity;
    private int reorderLevel;
    private BigDecimal unitCost;
    private String batchNo;
    private Date expiryDate;
    private Date lastUpdated;
    private String status;
    
    public Inventory() {}
    
    public Inventory(int inventoryId, int pharmacyId, String pharmacyName,
                     int medicineId, String medicineName, int quantity,
                     int reorderLevel, BigDecimal unitCost, String batchNo,
                     Date expiryDate, Date lastUpdated) {
        this.inventoryId = inventoryId;
        this.pharmacyId = pharmacyId;
        this.pharmacyName = pharmacyName;
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
        this.unitCost = unitCost;
        this.batchNo = batchNo;
        this.expiryDate = expiryDate;
        this.lastUpdated = lastUpdated;
        determineStatus();
    }
    
    // Determine stock status based on quantity and expiry
    private void determineStatus() {
        if (quantity <= 0) {
            this.status = "OUT OF STOCK";
        } else if (quantity <= reorderLevel) {
            this.status = "LOW STOCK";
        } else {
            Date today = new Date();
            long daysUntilExpiry = (expiryDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24);
            if (daysUntilExpiry < 30) {
                this.status = "EXPIRING SOON";
            } else {
                this.status = "OK";
            }
        }
    }
    
    // Getters and setters
    public int getInventoryId() { return inventoryId; }
    public void setInventoryId(int inventoryId) { this.inventoryId = inventoryId; }
    
    public int getPharmacyId() { return pharmacyId; }
    public void setPharmacyId(int pharmacyId) { this.pharmacyId = pharmacyId; }
    
    public String getPharmacyName() { return pharmacyName; }
    public void setPharmacyName(String pharmacyName) { this.pharmacyName = pharmacyName; }
    
    public int getMedicineId() { return medicineId; }
    public void setMedicineId(int medicineId) { this.medicineId = medicineId; }
    
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { 
        this.quantity = quantity;
        determineStatus();
    }
    
    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }
    
    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }
    
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    
    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { 
        this.expiryDate = expiryDate;
        determineStatus();
    }
    
    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }
    
    public String getStatus() { return status; }
    
    public BigDecimal getTotalValue() {
        return unitCost.multiply(BigDecimal.valueOf(quantity));
    }
}
