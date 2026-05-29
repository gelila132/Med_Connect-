package dao;

import model.Pharmacy;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Pharmacy operations
 */
public class PharmacyDAO {
    
    private DatabaseConnection dbConnection;
    
    public PharmacyDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    // Get all pharmacies
    public List<Pharmacy> getAllPharmacies() throws SQLException {
        List<Pharmacy> pharmacies = new ArrayList<>();
        String sql = "SELECT * FROM Pharmacy WHERE Is_Active = 1";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Pharmacy pharmacy = new Pharmacy();
                pharmacy.setPharmacyId(rs.getInt("Pharmacy_ID"));
                pharmacy.setPharmacyCode(rs.getString("Pharmacy_Code"));
                pharmacy.setName(rs.getString("Name"));
                pharmacy.setLocation(rs.getString("Location"));
                pharmacy.setLicenseNo(rs.getString("License_No"));
                pharmacy.setContactInfo(rs.getString("Contact_Info"));
                pharmacy.setEmail(rs.getString("Email"));
                pharmacy.setOperatingHours(rs.getString("Operating_Hours"));
                pharmacy.setIs24Hours(rs.getBoolean("Is_24_Hours"));
                pharmacy.setRegisteredDate(rs.getString("Registered_Date"));
                pharmacy.setActive(rs.getBoolean("Is_Active"));
                pharmacies.add(pharmacy);
            }
        }
        return pharmacies;
    }
    
    // Get pharmacy by ID
    public Pharmacy getPharmacyById(int pharmacyId) throws SQLException {
        String sql = "SELECT * FROM Pharmacy WHERE Pharmacy_ID = ? AND Is_Active = 1";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, pharmacyId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Pharmacy pharmacy = new Pharmacy();
                    pharmacy.setPharmacyId(rs.getInt("Pharmacy_ID"));
                    pharmacy.setPharmacyCode(rs.getString("Pharmacy_Code"));
                    pharmacy.setName(rs.getString("Name"));
                    pharmacy.setLocation(rs.getString("Location"));
                    pharmacy.setLicenseNo(rs.getString("License_No"));
                    pharmacy.setContactInfo(rs.getString("Contact_Info"));
                    pharmacy.setEmail(rs.getString("Email"));
                    pharmacy.setOperatingHours(rs.getString("Operating_Hours"));
                    pharmacy.setIs24Hours(rs.getBoolean("Is_24_Hours"));
                    pharmacy.setRegisteredDate(rs.getString("Registered_Date"));
                    pharmacy.setActive(rs.getBoolean("Is_Active"));
                    return pharmacy;
                }
            }
        }
        return null;
    }
    
    // Add new pharmacy
    public boolean addPharmacy(Pharmacy pharmacy) throws SQLException {
        String sql = "INSERT INTO Pharmacy (Pharmacy_Code, Name, Location, License_No, " +
                     "Contact_Info, Email, Operating_Hours, Is_24_Hours) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, pharmacy.getPharmacyCode());
            pstmt.setString(2, pharmacy.getName());
            pstmt.setString(3, pharmacy.getLocation());
            pstmt.setString(4, pharmacy.getLicenseNo());
            pstmt.setString(5, pharmacy.getContactInfo());
            pstmt.setString(6, pharmacy.getEmail());
            pstmt.setString(7, pharmacy.getOperatingHours());
            pstmt.setBoolean(8, pharmacy.isIs24Hours());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pharmacy.setPharmacyId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    // Update pharmacy
    public boolean updatePharmacy(Pharmacy pharmacy) throws SQLException {
        String sql = "UPDATE Pharmacy SET Name = ?, Location = ?, Contact_Info = ?, " +
                     "Email = ?, Operating_Hours = ?, Is_24_Hours = ? WHERE Pharmacy_ID = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, pharmacy.getName());
            pstmt.setString(2, pharmacy.getLocation());
            pstmt.setString(3, pharmacy.getContactInfo());
            pstmt.setString(4, pharmacy.getEmail());
            pstmt.setString(5, pharmacy.getOperatingHours());
            pstmt.setBoolean(6, pharmacy.isIs24Hours());
            pstmt.setInt(7, pharmacy.getPharmacyId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
}
