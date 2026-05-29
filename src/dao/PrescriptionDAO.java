package dao;

import model.Prescription;
import model.PrescriptionItem;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Prescription operations
 */
public class PrescriptionDAO {
    
    private DatabaseConnection dbConnection;
    
    public PrescriptionDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    // Get all prescriptions
    public List<Prescription> getAllPrescriptions() throws SQLException {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT p.*, ph.Name as Pharmacy_Name " +
                     "FROM Prescription p " +
                     "LEFT JOIN Pharmacy ph ON p.Pharmacy_ID = ph.Pharmacy_ID";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Prescription prescription = new Prescription();
                prescription.setPrescriptionId(rs.getInt("Prescription_ID"));
                prescription.setPrescriptionNo(rs.getString("Prescription_No"));
                prescription.setPatientName(rs.getString("Patient_Name"));
                prescription.setPatientPhone(rs.getString("Patient_Phone"));
                prescription.setDoctorName(rs.getString("Doctor_Name"));
                prescription.setIssueDate(rs.getDate("Issue_Date"));
                prescription.setValidUntil(rs.getDate("Valid_Until"));
                prescription.setStatus(rs.getString("Status"));
                prescription.setPharmacyId(rs.getInt("Pharmacy_ID"));
                if (rs.getObject("Pharmacy_ID") != null) {
                    prescription.setPharmacyName(rs.getString("Pharmacy_Name"));
                }
                prescription.setFilledDate(rs.getDate("Filled_Date"));
                
                // Load prescription items
                prescription.setItems(getPrescriptionItems(prescription.getPrescriptionId()));
                
                prescriptions.add(prescription);
            }
        }
        return prescriptions;
    }
    
    // Get prescription items
    private List<PrescriptionItem> getPrescriptionItems(int prescriptionId) throws SQLException {
        List<PrescriptionItem> items = new ArrayList<>();
        String sql = "SELECT pi.*, m.Brand_Name as Medicine_Name " +
                     "FROM Prescription_Item pi " +
                     "JOIN Medicine m ON pi.Medicine_ID = m.Medicine_ID " +
                     "WHERE pi.Prescription_ID = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, prescriptionId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PrescriptionItem item = new PrescriptionItem();
                    item.setPrescriptionItemId(rs.getInt("Prescription_Item_ID"));
                    item.setPrescriptionId(rs.getInt("Prescription_ID"));
                    item.setMedicineId(rs.getInt("Medicine_ID"));
                    item.setMedicineName(rs.getString("Medicine_Name"));
                    item.setQuantityPrescribed(rs.getInt("Quantity_Prescribed"));
                    item.setQuantityFilled(rs.getInt("Quantity_Filled"));
                    item.setInstructions(rs.getString("Instructions"));
                    items.add(item);
                }
            }
        }
        return items;
    }
    
    // Get prescription by number
    public Prescription getPrescriptionByNumber(String prescriptionNo) throws SQLException {
        String sql = "SELECT p.*, ph.Name as Pharmacy_Name " +
                     "FROM Prescription p " +
                     "LEFT JOIN Pharmacy ph ON p.Pharmacy_ID = ph.Pharmacy_ID " +
                     "WHERE p.Prescription_No = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, prescriptionNo);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Prescription prescription = new Prescription();
                    prescription.setPrescriptionId(rs.getInt("Prescription_ID"));
                    prescription.setPrescriptionNo(rs.getString("Prescription_No"));
                    prescription.setPatientName(rs.getString("Patient_Name"));
                    prescription.setPatientPhone(rs.getString("Patient_Phone"));
                    prescription.setDoctorName(rs.getString("Doctor_Name"));
                    prescription.setIssueDate(rs.getDate("Issue_Date"));
                    prescription.setValidUntil(rs.getDate("Valid_Until"));
                    prescription.setStatus(rs.getString("Status"));
                    prescription.setPharmacyId(rs.getInt("Pharmacy_ID"));
                    if (rs.getObject("Pharmacy_ID") != null) {
                        prescription.setPharmacyName(rs.getString("Pharmacy_Name"));
                    }
                    prescription.setFilledDate(rs.getDate("Filled_Date"));
                    prescription.setItems(getPrescriptionItems(prescription.getPrescriptionId()));
                    
                    return prescription;
                }
            }
        }
        return null;
    }
    
    // Create new prescription
    public boolean createPrescription(Prescription prescription) throws SQLException {
        String sql = "INSERT INTO Prescription (Prescription_No, Patient_Name, Patient_Phone, " +
                     "Doctor_Name, Issue_Date, Valid_Until, Status, Pharmacy_ID) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, prescription.getPrescriptionNo());
            pstmt.setString(2, prescription.getPatientName());
            pstmt.setString(3, prescription.getPatientPhone());
            pstmt.setString(4, prescription.getDoctorName());
            pstmt.setDate(5, new java.sql.Date(prescription.getIssueDate().getTime()));
            pstmt.setDate(6, new java.sql.Date(prescription.getValidUntil().getTime()));
            pstmt.setString(7, prescription.getStatus());
            if (prescription.getPharmacyId() != null) {
                pstmt.setInt(8, prescription.getPharmacyId());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        prescription.setPrescriptionId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    // Update prescription status
    public boolean updatePrescriptionStatus(int prescriptionId, String status, Integer pharmacyId) throws SQLException {
        String sql = "UPDATE Prescription SET Status = ?, Pharmacy_ID = ?, Filled_Date = GETDATE() " +
                     "WHERE Prescription_ID = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            if (pharmacyId != null) {
                pstmt.setInt(2, pharmacyId);
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setInt(3, prescriptionId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // Update prescription item filled quantity
    public boolean updatePrescriptionItemFilled(int prescriptionItemId, int quantityFilled) throws SQLException {
        String sql = "UPDATE Prescription_Item SET Quantity_Filled = ? WHERE Prescription_Item_ID = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quantityFilled);
            pstmt.setInt(2, prescriptionItemId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
}
