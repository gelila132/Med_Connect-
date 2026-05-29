package service;

import model.User;
import dao.UserDAO;
import util.DatabaseConnection;

import java.sql.SQLException;

/**
 * Authentication Service
 * Demonstrates: Business logic encapsulation
 */
public class AuthService {
    
    private UserDAO userDAO;
    private User currentUser;
    
    public AuthService() {
        this.userDAO = new UserDAO();
    }
    
    public boolean login(String username, String password) {
        try {
            User user = userDAO.authenticate(username, password);
            if (user != null) {
                currentUser = user;
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
        }
        return false;
    }
    
    public void logout() {
        currentUser = null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public boolean hasPermission(String permission) {
        if (currentUser == null) return false;
        
        String[] permissions = currentUser.getPermissions();
        for (String p : permissions) {
            if (p.equals(permission)) {
                return true;
            }
        }
        return false;
    }
    
    public String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }
}
