package dao;

import model.User;
import model.Admin;
import model.Pharmacist;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User operations
 * Demonstrates: CRUD operations, SQL integration
 */
public class UserDAO {
    
    private DatabaseConnection dbConnection;
    
    public UserDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    // Authenticate user (login)
    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ? AND is_active = 1";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    
                    if ("ADMIN".equals(role)) {
                        return new Admin(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("admin_level"),
                            rs.getString("department")
                        );
                    } else if ("PHARMACIST".equals(role)) {
                        return new Pharmacist(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("license_number"),
                            rs.getString("pharmacy_name"),
                            rs.getInt("years_experience")
                        );
                    }
                }
            }
        }
        return null;
    }
    
    // Create new user
    public boolean createUser(User user) throws SQLException {
        String sql = "INSERT INTO Users (username, password, full_name, email, phone, role, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, 1)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPhone());
            pstmt.setString(6, user.getRole());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    // Get all users
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE is_active = 1";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String role = rs.getString("role");
                User user;
                
                if ("ADMIN".equals(role)) {
                    user = new Admin(
                        rs.getInt("user_id"), rs.getString("username"),
                        rs.getString("password"), rs.getString("full_name"),
                        rs.getString("email"), rs.getString("phone"),
                        rs.getString("admin_level"), rs.getString("department")
                    );
                } else {
                    user = new Pharmacist(
                        rs.getInt("user_id"), rs.getString("username"),
                        rs.getString("password"), rs.getString("full_name"),
                        rs.getString("email"), rs.getString("phone"),
                        rs.getString("license_number"), rs.getString("pharmacy_name"),
                        rs.getInt("years_experience")
                    );
                }
                users.add(user);
            }
        }
        return users;
    }
    
    // Update user
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE Users SET full_name = ?, email = ?, phone = ? WHERE user_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPhone());
            pstmt.setInt(4, user.getUserId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // Delete user (soft delete)
    public boolean deleteUser(int userId) throws SQLException {
        String sql = "UPDATE Users SET is_active = 0 WHERE user_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        }
    }
}
