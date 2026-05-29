package model;

/**
 * Pharmacist class - extends User
 */
public class Pharmacist extends User {
    
    private String licenseNumber;
    private String pharmacyName;
    private int yearsOfExperience;
    
    public Pharmacist(int userId, String username, String password, String fullName,
                      String email, String phone, String licenseNumber, 
                      String pharmacyName, int yearsOfExperience) {
        super(userId, username, password, fullName, email, phone, "PHARMACIST");
        this.licenseNumber = licenseNumber;
        this.pharmacyName = pharmacyName;
        this.yearsOfExperience = yearsOfExperience;
    }
    
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    
    public String getPharmacyName() { return pharmacyName; }
    public void setPharmacyName(String pharmacyName) { this.pharmacyName = pharmacyName; }
    
    public int getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    
    @Override
    public String getDashboardType() {
        return "PHARMACIST_DASHBOARD";
    }
    
    @Override
    public String[] getPermissions() {
        return new String[]{
            "VIEW_INVENTORY", "UPDATE_INVENTORY", "FILL_PRESCRIPTIONS",
            "VIEW_PRESCRIPTIONS", "CHECK_AVAILABILITY", "GENERATE_REPORTS"
        };
    }
    
    @Override
    public void displayUserInfo() {
        super.displayUserInfo();
        System.out.println("License: " + licenseNumber);
        System.out.println("Pharmacy: " + pharmacyName);
        System.out.println("Experience: " + yearsOfExperience + " years");
    }
}
