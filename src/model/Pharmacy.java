package model;

/**
 * Pharmacy entity class
 * Demonstrates: Encapsulation, POJO pattern
 */
public class Pharmacy {
    
    private int pharmacyId;
    private String pharmacyCode;
    private String name;
    private String location;
    private String licenseNo;
    private String contactInfo;
    private String email;
    private String operatingHours;
    private boolean is24Hours;
    private String registeredDate;
    private boolean isActive;
    
    // Constructors
    public Pharmacy() {}
    
    public Pharmacy(int pharmacyId, String pharmacyCode, String name, String location,
                    String licenseNo, String contactInfo, String email, 
                    String operatingHours, boolean is24Hours) {
        this.pharmacyId = pharmacyId;
        this.pharmacyCode = pharmacyCode;
        this.name = name;
        this.location = location;
        this.licenseNo = licenseNo;
        this.contactInfo = contactInfo;
        this.email = email;
        this.operatingHours = operatingHours;
        this.is24Hours = is24Hours;
        this.isActive = true;
    }
    
    // Getters and Setters
    public int getPharmacyId() { return pharmacyId; }
    public void setPharmacyId(int pharmacyId) { this.pharmacyId = pharmacyId; }
    
    public String getPharmacyCode() { return pharmacyCode; }
    public void setPharmacyCode(String pharmacyCode) { this.pharmacyCode = pharmacyCode; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getLicenseNo() { return licenseNo; }
    public void setLicenseNo(String licenseNo) { this.licenseNo = licenseNo; }
    
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getOperatingHours() { return operatingHours; }
    public void setOperatingHours(String operatingHours) { this.operatingHours = operatingHours; }
    
    public boolean isIs24Hours() { return is24Hours; }
    public void setIs24Hours(boolean is24Hours) { this.is24Hours = is24Hours; }
    
    public String getRegisteredDate() { return registeredDate; }
    public void setRegisteredDate(String registeredDate) { this.registeredDate = registeredDate; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    @Override
    public String toString() {
        return name + " - " + location;
    }
}
