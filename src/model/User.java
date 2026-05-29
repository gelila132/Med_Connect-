package model;

import java.util.Date;

/**
 * Abstract base class demonstrating:
 * - Abstraction
 * - Encapsulation
 * - Inheritance (will be extended by Admin, Pharmacist, Customer)
 */
public abstract class User {
    
    // Encapsulated fields (private with public getters/setters)
    private int userId;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private Date createdAt;
    private boolean isActive;
    
    // Constructor
    public User(int userId, String username, String password, String fullName, 
                String email, String phone, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.createdAt = new Date();
        this.isActive = true;
    }
    
    // Getters and Setters (Encapsulation)
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public Date getCreatedAt() { return createdAt; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    // Abstract methods (Abstraction - to be implemented by subclasses)
    public abstract String getDashboardType();
    public abstract String[] getPermissions();
    
    // Concrete method (polymorphism - can be overridden)
    public void displayUserInfo() {
        System.out.println("User: " + fullName + " (" + role + ")");
        System.out.println("Email: " + email);
        System.out.println("Phone: " + phone);
    }
    
    @Override
    public String toString() {
        return fullName + " (" + username + ") - " + role;
    }
}
