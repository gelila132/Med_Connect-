package model;

import java.math.BigDecimal;

/**
 * Medicine entity class
 * Demonstrates: Encapsulation, POJO pattern
 */
public class Medicine {
    
    private int medicineId;
    private String medicineCode;
    private String brandName;
    private String genericName;
    private String manufacturer;
    private int categoryId;
    private String categoryName;
    private BigDecimal unitPrice;
    private String dosageForm;
    private String strength;
    private boolean requiresPrescription;
    private int expiryWarningDays;
    
    // Constructors
    public Medicine() {}
    
    public Medicine(int medicineId, String medicineCode, String brandName, 
                    String genericName, String manufacturer, int categoryId,
                    BigDecimal unitPrice, String dosageForm, String strength, 
                    boolean requiresPrescription) {
        this.medicineId = medicineId;
        this.medicineCode = medicineCode;
        this.brandName = brandName;
        this.genericName = genericName;
        this.manufacturer = manufacturer;
        this.categoryId = categoryId;
        this.unitPrice = unitPrice;
        this.dosageForm = dosageForm;
        this.strength = strength;
        this.requiresPrescription = requiresPrescription;
        this.expiryWarningDays = 90;
    }
    
    // Getters and Setters
    public int getMedicineId() { return medicineId; }
    public void setMedicineId(int medicineId) { this.medicineId = medicineId; }
    
    public String getMedicineCode() { return medicineCode; }
    public void setMedicineCode(String medicineCode) { this.medicineCode = medicineCode; }
    
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    
    public String getGenericName() { return genericName; }
    public void setGenericName(String genericName) { this.genericName = genericName; }
    
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    
    public String getDosageForm() { return dosageForm; }
    public void setDosageForm(String dosageForm) { this.dosageForm = dosageForm; }
    
    public String getStrength() { return strength; }
    public void setStrength(String strength) { this.strength = strength; }
    
    public boolean isRequiresPrescription() { return requiresPrescription; }
    public void setRequiresPrescription(boolean requiresPrescription) { this.requiresPrescription = requiresPrescription; }
    
    public int getExpiryWarningDays() { return expiryWarningDays; }
    public void setExpiryWarningDays(int expiryWarningDays) { this.expiryWarningDays = expiryWarningDays; }
    
    @Override
    public String toString() {
        return brandName + " (" + strength + ") - $" + unitPrice;
    }
}
