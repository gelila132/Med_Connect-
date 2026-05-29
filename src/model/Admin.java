package model;

/**
 * Admin class - extends User
 * Demonstrates: Inheritance, Polymorphism
 */
public class Admin extends User {
    
    private String adminLevel;
    private String department;
    
    public Admin(int userId, String username, String password, String fullName,
                 String email, String phone, String adminLevel, String department) {
        super(userId, username, password, fullName, email, phone, "ADMIN");
        this.adminLevel = adminLevel;
        this.department = department;
    }
    
    // Getters and setters
    public String getAdminLevel() { return adminLevel; }
    public void setAdminLevel(String adminLevel) { this.adminLevel = adminLevel; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    @Override
    public String getDashboardType() {
        return "ADMIN_DASHBOARD";
    }
    
    @Override
    public String[] getPermissions() {
        return new String[]{
            "MANAGE_USERS", "MANAGE_MEDICINES", "MANAGE_PHARMACIES",
            "VIEW_ALL_REPORTS", "MANAGE_SUPPLIERS", "SYSTEM_CONFIG"
        };
    }
    
    @Override
    public void displayUserInfo() {
        super.displayUserInfo();
        System.out.println("Admin Level: " + adminLevel);
        System.out.println("Department: " + department);
    }
}
