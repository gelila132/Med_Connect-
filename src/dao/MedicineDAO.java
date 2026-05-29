package dao;

import model.Medicine;
import util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Medicine operations
 */
public class MedicineDAO {
    
    private DatabaseConnection dbConnection;
    
    public MedicineDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    // Get all medicines
    public List<Medicine> getAllMedicines() throws SQLException {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT m.*, c.Category_Name FROM Medicine m " +
                     "JOIN Category c ON m.Category_ID = c.Category_ID";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Medicine medicine = new Medicine();
                medicine.setMedicineId(rs.getInt("Medicine_ID"));
                medicine.setMedicineCode(rs.getString("Medicine_Code"));
                medicine.setBrandName(rs.getString("Brand_Name"));
                medicine.setGenericName(rs.getString("Generic_Name"));
                medicine.setManufacturer(rs.getString("Manufacturer"));
                medicine.setCategoryId(rs.getInt("Category_ID"));
                medicine.setCategoryName(rs.getString("Category_Name"));
                medicine.setUnitPrice(rs.getBigDecimal("Unit_Price"));
                medicine.setDosageForm(rs.getString("Dosage_Form"));
                medicine.setStrength(rs.getString("Strength"));
                medicine.setRequiresPrescription(rs.getBoolean("Requires_Prescription"));
                medicines.add(medicine);
            }
        }
        return medicines;
    }
    
    // Search medicines by name
    public List<Medicine> searchMedicines(String keyword) throws SQLException {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT m.*, c.Category_Name FROM Medicine m " +
                     "JOIN Category c ON m.Category_ID = c.Category_ID " +
                     "WHERE m.Brand_Name LIKE ? OR m.Generic_Name LIKE ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Medicine medicine = new Medicine();
                    medicine.setMedicineId(rs.getInt("Medicine_ID"));
                    medicine.setMedicineCode(rs.getString("Medicine_Code"));
                    medicine.setBrandName(rs.getString("Brand_Name"));
                    medicine.setGenericName(rs.getString("Generic_Name"));
                    medicine.setManufacturer(rs.getString("Manufacturer"));
                    medicine.setCategoryId(rs.getInt("Category_ID"));
                    medicine.setCategoryName(rs.getString("Category_Name"));
                    medicine.setUnitPrice(rs.getBigDecimal("Unit_Price"));
                    medicine.setDosageForm(rs.getString("Dosage_Form"));
                    medicine.setStrength(rs.getString("Strength"));
                    medicine.setRequiresPrescription(rs.getBoolean("Requires_Prescription"));
                    medicines.add(medicine);
                }
            }
        }
        return medicines;
    }
    
    // Get medicine by ID
    public Medicine getMedicineById(int medicineId) throws SQLException {
        String sql = "SELECT m.*, c.Category_Name FROM Medicine m " +
                     "JOIN Category c ON m.Category_ID = c.Category_ID " +
                     "WHERE m.Medicine_ID = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, medicineId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Medicine medicine = new Medicine();
                    medicine.setMedicineId(rs.getInt("Medicine_ID"));
                    medicine.setMedicineCode(rs.getString("Medicine_Code"));
                    medicine.setBrandName(rs.getString("Brand_Name"));
                    medicine.setGenericName(rs.getString("Generic_Name"));
                    medicine.setManufacturer(rs.getString("Manufacturer"));
                    medicine.setCategoryId(rs.getInt("Category_ID"));
                    medicine.setCategoryName(rs.getString("Category_Name"));
                    medicine.setUnitPrice(rs.getBigDecimal("Unit_Price"));
                    medicine.setDosageForm(rs.getString("Dosage_Form"));
                    medicine.setStrength(rs.getString("Strength"));
                    medicine.setRequiresPrescription(rs.getBoolean("Requires_Prescription"));
                    return medicine;
                }
            }
        }
        return null;
    }
    
    // Add new medicine
    public boolean addMedicine(Medicine medicine) throws SQLException {
        String sql = "INSERT INTO Medicine (Medicine_Code, Brand_Name, Generic_Name, " +
                     "Manufacturer, Category_ID, Unit_Price, Dosage_Form, Strength, " +
                     "Requires_Prescription) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, medicine.getMedicineCode());
            pstmt.setString(2, medicine.getBrandName());
            pstmt.setString(3, medicine.getGenericName());
            pstmt.setString(4, medicine.getManufacturer());
            pstmt.setInt(5, medicine.getCategoryId());
            pstmt.setBigDecimal(6, medicine.getUnitPrice());
            pstmt.setString(7, medicine.getDosageForm());
            pstmt.setString(8, medicine.getStrength());
            pstmt.setBoolean(9, medicine.isRequiresPrescription());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        medicine.setMedicineId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    // Update medicine
    public boolean updateMedicine(Medicine medicine) throws SQLException {
        String sql = "UPDATE Medicine SET Brand_Name = ?, Generic_Name = ?, " +
                     "Manufacturer = ?, Unit_Price = ?, Dosage_Form = ?, Strength = ? " +
                     "WHERE Medicine_ID = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, medicine.getBrandName());
            pstmt.setString(2, medicine.getGenericName());
            pstmt.setString(3, medicine.getManufacturer());
            pstmt.setBigDecimal(4, medicine.getUnitPrice());
            pstmt.setString(5, medicine.getDosageForm());
            pstmt.setString(6, medicine.getStrength());
            pstmt.setInt(7, medicine.getMedicineId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
}
