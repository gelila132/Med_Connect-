package model;

/**
 * Prescription Item (line item) class
 */
public class PrescriptionItem {
    
    private int prescriptionItemId;
    private int prescriptionId;
    private int medicineId;
    private String medicineName;
    private int quantityPrescribed;
    private int quantityFilled;
    private String instructions;
    
    public PrescriptionItem() {}
    
    public PrescriptionItem(int prescriptionItemId, int prescriptionId, int medicineId,
                            String medicineName, int quantityPrescribed, 
                            int quantityFilled, String instructions) {
        this.prescriptionItemId = prescriptionItemId;
        this.prescriptionId = prescriptionId;
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.quantityPrescribed = quantityPrescribed;
        this.quantityFilled = quantityFilled;
        this.instructions = instructions;
    }
    
    // Getters and Setters
    public int getPrescriptionItemId() { return prescriptionItemId; }
    public void setPrescriptionItemId(int prescriptionItemId) { this.prescriptionItemId = prescriptionItemId; }
    
    public int getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(int prescriptionId) { this.prescriptionId = prescriptionId; }
    
    public int getMedicineId() { return medicineId; }
    public void setMedicineId(int medicineId) { this.medicineId = medicineId; }
    
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    
    public int getQuantityPrescribed() { return quantityPrescribed; }
    public void setQuantityPrescribed(int quantityPrescribed) { this.quantityPrescribed = quantityPrescribed; }
    
    public int getQuantityFilled() { return quantityFilled; }
    public void setQuantityFilled(int quantityFilled) { this.quantityFilled = quantityFilled; }
    
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    
    public int getRemainingQuantity() {
        return quantityPrescribed - quantityFilled;
    }
    
    public boolean isFullyFilled() {
        return quantityFilled >= quantityPrescribed;
    }
}
