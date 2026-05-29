package service;

import dao.PrescriptionDAO;
import dao.InventoryDAO;
import model.Prescription;
import model.PrescriptionItem;
import model.Inventory;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Prescription Service - Handles prescription operations
 */
public class PrescriptionService {
    
    private PrescriptionDAO prescriptionDAO;
    private InventoryDAO inventoryDAO;
    
    public PrescriptionService() {
        this.prescriptionDAO = new PrescriptionDAO();
        this.inventoryDAO = new InventoryDAO();
    }
    
    // Get all prescriptions
    public List<Prescription> getAllPrescriptions() throws SQLException {
        return prescriptionDAO.getAllPrescriptions();
    }
    
    // Get prescription by number
    public Prescription getPrescriptionByNumber(String prescriptionNo) throws SQLException {
        return prescriptionDAO.getPrescriptionByNumber(prescriptionNo);
    }
    
    // Create new prescription
    public boolean createPrescription(Prescription prescription) throws SQLException {
        // Validate prescription data
        if (prescription.getPrescriptionNo() == null || prescription.getPrescriptionNo().trim().isEmpty()) {
            throw new IllegalArgumentException("Prescription number is required");
        }
        if (prescription.getPatientName() == null || prescription.getPatientName().trim().isEmpty()) {
            throw new IllegalArgumentException("Patient name is required");
        }
        if (prescription.getDoctorName() == null || prescription.getDoctorName().trim().isEmpty()) {
            throw new IllegalArgumentException("Doctor name is required");
        }
        if (prescription.getValidUntil().before(new Date())) {
            throw new IllegalArgumentException("Valid until date must be in the future");
        }
        
        prescription.setStatus("Pending");
        return prescriptionDAO.createPrescription(prescription);
    }
    
    // Fill prescription
    public boolean fillPrescription(String prescriptionNo, int pharmacyId) throws SQLException {
        Prescription prescription = prescriptionDAO.getPrescriptionByNumber(prescriptionNo);
        
        if (prescription == null) {
            throw new IllegalArgumentException("Prescription not found");
        }
        
        if (prescription.isExpired()) {
            throw new IllegalStateException("Prescription has expired");
        }
        
        if (!"Pending".equals(prescription.getStatus()) && !"Partially Filled".equals(prescription.getStatus())) {
            throw new IllegalStateException("Prescription cannot be filled. Current status: " + prescription.getStatus());
        }
        
        // Check if all items can be filled
        boolean allAvailable = true;
        for (PrescriptionItem item : prescription.getItems()) {
            int remainingNeeded = item.getQuantityPrescribed() - item.getQuantityFilled();
            if (remainingNeeded > 0) {
                // Check inventory at pharmacy
                List<Inventory> inventory = inventoryDAO.getInventoryByPharmacy(pharmacyId);
                boolean found = false;
                for (Inventory inv : inventory) {
                    if (inv.getMedicineId() == item.getMedicineId() && inv.getQuantity() >= remainingNeeded) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    allAvailable = false;
                }
            }
        }
        
        if (allAvailable) {
            // Fill all items completely
            for (PrescriptionItem item : prescription.getItems()) {
                int remainingNeeded = item.getQuantityPrescribed() - item.getQuantityFilled();
                if (remainingNeeded > 0) {
                    prescriptionDAO.updatePrescriptionItemFilled(
                        item.getPrescriptionItemId(), 
                        item.getQuantityPrescribed()
                    );
                    
                    // Update inventory (decrease stock)
                    // This would call inventory update logic
                }
            }
            return prescriptionDAO.updatePrescription
