package model;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Prescription entity class
 */
public class Prescription {
    
    private int prescriptionId;
    private String prescriptionNo;
    private String patientName;
    private String patientPhone;
    private String doctorName;
    private Date issueDate;
    private Date validUntil;
    private String status;
    private Integer pharmacyId;
    private String pharmacyName;
    private Date filledDate;
    private List<PrescriptionItem> items;
    
    public Prescription() {
        this.items = new ArrayList<>();
    }
    
    public Prescription(int prescriptionId, String prescriptionNo, String patientName,
                        String patientPhone, String doctorName, Date issueDate,
                        Date validUntil, String status, Integer pharmacyId) {
        this.prescriptionId = prescriptionId;
        this.prescriptionNo = prescriptionNo;
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.doctorName = doctorName;
        this.issueDate = issueDate;
        this.validUntil = validUntil;
        this.status = status;
        this.pharmacyId = pharmacyId;
        this.items = new ArrayList<>();
    }
    
    // Getters and Setters
    public int getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(int prescriptionId) { this.prescriptionId = prescriptionId; }
    
    public String getPrescriptionNo() { return prescriptionNo; }
    public void setPrescriptionNo(String prescriptionNo) { this.prescriptionNo = prescriptionNo; }
    
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    
    public String getPatientPhone() { return patientPhone; }
    public void setPatientPhone(String patientPhone) { this.patientPhone = patientPhone; }
    
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    
    public Date getIssueDate() { return issueDate; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }
    
    public Date getValidUntil() { return validUntil; }
    public void setValidUntil(Date validUntil) { this.validUntil = validUntil; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Integer getPharmacyId() { return pharmacyId; }
    public void setPharmacyId(Integer pharmacyId) { this.pharmacyId = pharmacyId; }
    
    public String getPharmacyName() { return pharmacyName; }
    public void setPharmacyName(String pharmacyName) { this.pharmacyName = pharmacyName; }
    
    public Date getFilledDate() { return filledDate; }
    public void setFilledDate(Date filledDate) { this.filledDate = filledDate; }
    
    public List<PrescriptionItem> getItems() { return items; }
    public void setItems(List<PrescriptionItem> items) { this.items = items; }
    
    public void addItem(PrescriptionItem item) {
        this.items.add(item);
    }
    
    public boolean isExpired() {
        Date today = new Date();
        return validUntil != null && validUntil.before(today);
    }
    
    @Override
    public String toString() {
        return "Rx #" + prescriptionNo + " - " + patientName + " (" + status + ")";
    }
}
