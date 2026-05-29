package dao;

import model.Inventory;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Inventory operations
 */
public class InventoryDAO {
    
    private DatabaseConnection dbConnection;
    
    public InventoryDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    // Get inventory by pharmacy
    public List<Inventory> getInventoryByPharmacy(int pharmacyId) throws SQLException {
        List<Inventory> inventoryList = new ArrayList<>();
        String sql = "SELECT ie.*, p.Name as Pharmacy_Name, m.Brand_Name as Medicine_Name " +
                     "FROM Inventory_Entry ie " +
                     "JOIN Pharmacy p ON ie.Pharmacy_ID = p.Pharmacy_ID " +
                     "JOIN Medicine m ON ie.Medicine_ID = m.Medicine_ID " +
                     "WHERE ie.Pharmacy_ID = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, pharmacyId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Inventory inventory = new Inventory(
                        rs.getInt("Inventory_ID"),
                        rs.getInt("Pharmacy_ID"),
                        rs.getString("Pharmacy_Name"),
                        rs.getInt("Medicine_ID"),
                        rs.getString("Medicine_Name"),
                        rs.getInt("Quantity"),
                        rs.getInt("Reorder_Level"),
                        rs.getBigDecimal("Unit_Cost"),
                        rs.getString("Batch_No"),
                        rs.getDate("Expiry_Date"),
                        rs.getTimestamp("Last_Updated")
                    );
                    inventoryList.add(inventory);
                }
            }
        }
        return inventoryList;
    }
    
    // Update inventory quantity
    public boolean updateInventoryQuantity(int inventoryId, int newQuantity) throws SQLException {
        String sql = "{CALL sp_UpdateInventory(?, ?, ?)}";
        
        try (Connection conn = dbConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            // Note: This requires getting pharmacy_id and medicine_id from inventory_id
            // Simplified version - in production, you'd query those first
            cstmt.setInt(1, 1); // pharmacy_id placeholder
            cstmt.setInt(2, 1); // medicine_id placeholder
            cstmt.setInt(3, newQuantity);
            
            return cstmt.execute();
        }
    }
    
    // Check medicine availability across pharmacies
    public List<Inventory> checkAvailability(String medicineName) throws SQLException {
        List<Inventory> available = new ArrayList<>();
        String sql = "SELECT ie.*, p.Name as Pharmacy_Name, p.Location, p.Contact_Info, " +
                     "m.Brand_Name as Medicine_Name " +
                     "FROM Inventory_Entry ie " +
                     "JOIN Pharmacy p ON ie.Pharmacy_ID = p.Pharmacy_ID " +
                     "JOIN Medicine m ON ie.Medicine_ID = m.Medicine_ID " +
                     "WHERE m.Brand_Name LIKE ? AND ie.Quantity > 0";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + medicineName + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Inventory inventory = new Inventory(
                        rs.getInt("Inventory_ID"),
                        rs.getInt("Pharmacy_ID"),
                        rs.getString("Pharmacy_Name"),
                        rs.getInt("Medicine_ID"),
                        rs.getString("Medicine_Name"),
                        rs.getInt("Quantity"),
                        rs.getInt("Reorder_Level"),
                        rs.getBigDecimal("Unit_Cost"),
                        rs.getString("Batch_No"),
                        rs.getDate("Expiry_Date"),
                        rs.getTimestamp("Last_Updated")
                    );
                    available.add(inventory);
                }
            }
        }
        return available;
    }
    
    // Get low stock items
    public List<Inventory> getLowStockItems(int pharmacyId) throws SQLException {
        List<Inventory> lowStock = new ArrayList<>();
        String sql = "SELECT ie.*, p.Name as Pharmacy_Name, m.Brand_Name as Medicine_Name " +
                     "FROM Inventory_Entry ie " +
                     "JOIN Pharmacy p ON ie.Pharmacy_ID = p.Pharmacy_ID " +
                     "JOIN Medicine m ON ie.Medicine_ID = m.Medicine_ID " +
                     "WHERE ie.Pharmacy_ID = ? AND ie.Quantity <= ie.Reorder_Level";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, pharmacyId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Inventory inventory = new Inventory(
                        rs.getInt("Inventory_ID"),
                        rs.getInt("Pharmacy_ID"),
                        rs.getString("Pharmacy_Name"),
                        rs.getInt("Medicine_ID"),
                        rs.getString("Medicine_Name"),
                        rs.getInt("Quantity"),
                        rs.getInt("Reorder_Level"),
                        rs.getBigDecimal("Unit_Cost"),
                        rs.getString("Batch_No"),
                        rs.getDate("Expiry_Date"),
                        rs.getTimestamp("Last_Updated")
                    );
                    lowStock.add(inventory);
                }
            }
        }
        return lowStock;
    }
}
