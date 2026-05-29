-- =============================================================================
-- DATABASE: Med-Connect (A Relational Health-Inventory & Logistics Engine)
-- VERSION: 2.0 (Enhanced Edition)
-- AUTHOR: [Your Name]
-- DATE: [Current Date]
-- DESCRIPTION: Complete pharmaceutical inventory management system
-- =============================================================================

-- =============================================================================
-- SECTION 1: DATABASE INITIALIZATION
-- =============================================================================

-- Create the database (uncomment if needed)
-- CREATE DATABASE MedConnect;
-- GO
-- USE MedConnect;
-- GO

-- Drop existing tables if they exist (for clean re-runs, in correct dependency order)
IF OBJECT_ID('Inventory_Entry', 'U') IS NOT NULL DROP TABLE Inventory_Entry;
IF OBJECT_ID('Medicine', 'U') IS NOT NULL DROP TABLE Medicine;
IF OBJECT_ID('Pharmacy', 'U') IS NOT NULL DROP TABLE Pharmacy;
IF OBJECT_ID('Category', 'U') IS NOT NULL DROP TABLE Category;
IF OBJECT_ID('Supplier', 'U') IS NOT NULL DROP TABLE Supplier;
IF OBJECT_ID('Medicine_Supplier', 'U') IS NOT NULL DROP TABLE Medicine_Supplier;
IF OBJECT_ID('Prescription', 'U') IS NOT NULL DROP TABLE Prescription;
IF OBJECT_ID('Audit_Log', 'U') IS NOT NULL DROP TABLE Audit_Log;
IF OBJECT_ID('LowStockAlert', 'U') IS NOT NULL DROP TABLE LowStockAlert;
GO

-- =============================================================================
-- SECTION 2: TABLE CREATION (ENHANCED SCHEMA)
-- =============================================================================

-- ---------------------------------------------
-- Table 1: Category (Medicine classification)
-- ---------------------------------------------
CREATE TABLE Category (
    Category_ID INT PRIMARY KEY IDENTITY(1,1),
    Category_Code VARCHAR(20) NOT NULL UNIQUE,
    Category_Name VARCHAR(100) NOT NULL,
    [Description] VARCHAR(500) NULL,
    Parent_Category_ID INT NULL FOREIGN KEY REFERENCES Category(Category_ID),
    Is_Active BIT NOT NULL DEFAULT 1,
    Created_At DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT CHK_Category_Code CHECK (Category_Code LIKE 'CAT_[A-Z][0-9][0-9]')
);
-- Note: CHK pattern expects format like "CAT_A01", "CAT_B12"

-- ---------------------------------------------
-- Table 2: Supplier (NEW - Enhanced feature)
-- ---------------------------------------------
CREATE TABLE Supplier (
    Supplier_ID INT PRIMARY KEY IDENTITY(1,1),
    Supplier_Name VARCHAR(150) NOT NULL,
    Contact_Person VARCHAR(100) NULL,
    Phone VARCHAR(20) NOT NULL,
    Email VARCHAR(100) NOT NULL UNIQUE,
    [Address] VARCHAR(255) NULL,
    Tax_ID VARCHAR(50) UNIQUE,
    Payment_Terms VARCHAR(50) DEFAULT 'Net 30',
    Rating DECIMAL(2,1) CHECK (Rating BETWEEN 0 AND 5),
    Is_Active BIT DEFAULT 1
);

-- ---------------------------------------------
-- Table 3: Medicine (Core product entity)
-- ---------------------------------------------
CREATE TABLE Medicine (
    Medicine_ID INT PRIMARY KEY IDENTITY(100,1),
    Medicine_Code VARCHAR(30) NOT NULL UNIQUE,
    Brand_Name VARCHAR(150) NOT NULL,
    Generic_Name VARCHAR(150) NOT NULL,
    Manufacturer VARCHAR(150) NOT NULL,
    Category_ID INT NOT NULL FOREIGN KEY REFERENCES Category(Category_ID),
    Unit_Price DECIMAL(10,2) NOT NULL CHECK (Unit_Price >= 0),
    Dosage_Form VARCHAR(50) NULL, -- Tablet, Capsule, Syrup, Injection
    Strength VARCHAR(50) NULL,    -- 500mg, 10ml, etc.
    Requires_Prescription BIT NOT NULL DEFAULT 1,
    Expiry_Warning_Days INT DEFAULT 90,
    Created_At DATETIME NOT NULL DEFAULT GETDATE()
);

-- ---------------------------------------------
-- Table 4: Pharmacy (Store/Location entity)
-- ---------------------------------------------
CREATE TABLE Pharmacy (
    Pharmacy_ID INT PRIMARY KEY IDENTITY(1,1),
    Pharmacy_Code VARCHAR(20) NOT NULL UNIQUE,
    [Name] VARCHAR(150) NOT NULL,
    [Location] VARCHAR(255) NOT NULL,
    License_No VARCHAR(50) NOT NULL UNIQUE,
    Contact_Info VARCHAR(100) NOT NULL,
    Email VARCHAR(100) NULL,
    Operating_Hours VARCHAR(100) NULL,
    Is_24_Hours BIT DEFAULT 0,
    Registered_Date DATE NOT NULL DEFAULT GETDATE(),
    Is_Active BIT NOT NULL DEFAULT 1
);

-- ---------------------------------------------
-- Table 5: Inventory_Entry (Association table)
-- ---------------------------------------------
CREATE TABLE Inventory_Entry (
    Inventory_ID INT PRIMARY KEY IDENTITY(1,1),
    Pharmacy_ID INT NOT NULL FOREIGN KEY REFERENCES Pharmacy(Pharmacy_ID),
    Medicine_ID INT NOT NULL FOREIGN KEY REFERENCES Medicine(Medicine_ID),
    Quantity INT NOT NULL CHECK (Quantity >= 0),
    Reorder_Level INT DEFAULT 10 CHECK (Reorder_Level >= 0),
    Unit_Cost DECIMAL(10,2) NOT NULL CHECK (Unit_Cost >= 0),
    Batch_No VARCHAR(50) NULL,
    Expiry_Date DATE NOT NULL,
    Last_Updated DATETIME NOT NULL DEFAULT GETDATE(),
    Updated_By VARCHAR(50) DEFAULT SYSTEM_USER,
    
    -- Composite unique constraint to prevent duplicate entries
    CONSTRAINT UQ_Pharmacy_Medicine UNIQUE (Pharmacy_ID, Medicine_ID, Batch_No),
    
    -- Check that expiry date is in the future (for new entries)
    CONSTRAINT CHK_Expiry_Date CHECK (Expiry_Date > GETDATE())
);

-- ---------------------------------------------
-- Table 6: Medicine_Supplier (M:N between Medicine & Supplier) - NEW
-- ---------------------------------------------
CREATE TABLE Medicine_Supplier (
    Medicine_ID INT NOT NULL FOREIGN KEY REFERENCES Medicine(Medicine_ID),
    Supplier_ID INT NOT NULL FOREIGN KEY REFERENCES Supplier(Supplier_ID),
    Is_Primary BIT DEFAULT 0,
    Contract_Start_Date DATE NULL,
    Contract_End_Date DATE NULL,
    PRIMARY KEY (Medicine_ID, Supplier_ID)
);

-- ---------------------------------------------
-- Table 7: Prescription (NEW - Patient prescriptions)
-- ---------------------------------------------
CREATE TABLE Prescription (
    Prescription_ID INT PRIMARY KEY IDENTITY(1,1),
    Prescription_No VARCHAR(50) UNIQUE NOT NULL,
    Patient_Name VARCHAR(150) NOT NULL,
    Patient_Phone VARCHAR(20) NULL,
    Doctor_Name VARCHAR(150) NOT NULL,
    Issue_Date DATE NOT NULL DEFAULT GETDATE(),
    Valid_Until DATE NOT NULL,
    Status VARCHAR(20) DEFAULT 'Pending' CHECK (Status IN ('Pending', 'Filled', 'Partially Filled', 'Expired', 'Cancelled')),
    Pharmacy_ID INT NULL FOREIGN KEY REFERENCES Pharmacy(Pharmacy_ID),
    Filled_Date DATE NULL
);

-- ---------------------------------------------
-- Table 8: Prescription_Item (Line items for prescriptions) - NEW
-- ---------------------------------------------
CREATE TABLE Prescription_Item (
    Prescription_Item_ID INT PRIMARY KEY IDENTITY(1,1),
    Prescription_ID INT NOT NULL FOREIGN KEY REFERENCES Prescription(Prescription_ID),
    Medicine_ID INT NOT NULL FOREIGN KEY REFERENCES Medicine(Medicine_ID),
    Quantity_Prescribed INT NOT NULL CHECK (Quantity_Prescribed > 0),
    Quantity_Filled INT DEFAULT 0 CHECK (Quantity_Filled >= 0),
    Instructions VARCHAR(500) NULL
);

-- ---------------------------------------------
-- Table 9: Audit_Log (For tracking all changes) - NEW (Advanced feature)
-- ---------------------------------------------
CREATE TABLE Audit_Log (
    Log_ID INT PRIMARY KEY IDENTITY(1,1),
    Table_Name VARCHAR(100) NOT NULL,
    Record_ID INT NOT NULL,
    Action_Type VARCHAR(20) NOT NULL CHECK (Action_Type IN ('INSERT', 'UPDATE', 'DELETE')),
    Old_Value XML NULL,
    New_Value XML NULL,
    Changed_By VARCHAR(100) DEFAULT SYSTEM_USER,
    Change_Date DATETIME NOT NULL DEFAULT GETDATE()
);

-- ---------------------------------------------
-- Table 10: LowStockAlert (Automated alerts) - NEW
-- ---------------------------------------------
CREATE TABLE LowStockAlert (
    Alert_ID INT PRIMARY KEY IDENTITY(1,1),
    Inventory_ID INT NOT NULL FOREIGN KEY REFERENCES Inventory_Entry(Inventory_ID),
    Alert_Date DATETIME NOT NULL DEFAULT GETDATE(),
    Alert_Type VARCHAR(50) CHECK (Alert_Type IN ('Low Stock', 'Expiring Soon', 'Out of Stock')),
    Is_Resolved BIT DEFAULT 0,
    Resolved_Date DATETIME NULL
);

-- =============================================================================
-- SECTION 3: INDEXES (Performance Optimization)
-- =============================================================================

-- Indexes for faster searching
CREATE INDEX IX_Medicine_Category ON Medicine(Category_ID);
CREATE INDEX IX_Medicine_Generic ON Medicine(Generic_Name);
CREATE INDEX IX_Inventory_Pharmacy ON Inventory_Entry(Pharmacy_ID);
CREATE INDEX IX_Inventory_Medicine ON Inventory_Entry(Medicine_ID);
CREATE INDEX IX_Inventory_Expiry ON Inventory_Entry(Expiry_Date);
CREATE INDEX IX_Prescription_Pharmacy ON Prescription(Pharmacy_ID);
CREATE INDEX IX_Prescription_Status ON Prescription(Status);
CREATE INDEX IX_LowStockAlert_Status ON LowStockAlert(Is_Resolved);

-- Full-text search capable index suggestion (for advanced searching)
-- CREATE FULLTEXT CATALOG ft_Catalog AS DEFAULT;
-- CREATE FULLTEXT INDEX ON Medicine(Brand_Name, Generic_Name) KEY INDEX PK_Medicine;

-- =============================================================================
-- SECTION 4: STORED PROCEDURES
-- =============================================================================

-- ---------------------------------------------
-- SP 1: Check medicine availability across pharmacies
-- ---------------------------------------------
GO
CREATE OR ALTER PROCEDURE sp_CheckMedicineAvailability
    @MedicineName VARCHAR(150)
AS
BEGIN
    SELECT 
        p.Name AS Pharmacy_Name,
        p.Location,
        p.Contact_Info,
        ie.Quantity AS Stock_Available,
        ie.Unit_Cost,
        ie.Batch_No,
        ie.Expiry_Date,
        CASE 
            WHEN ie.Quantity <= ie.Reorder_Level THEN 'Low Stock - Reorder Needed'
            WHEN ie.Expiry_Date <= DATEADD(DAY, 30, GETDATE()) THEN 'Expiring Soon'
            ELSE 'In Stock'
        END AS Stock_Status
    FROM Inventory_Entry ie
    INNER JOIN Pharmacy p ON ie.Pharmacy_ID = p.Pharmacy_ID
    INNER JOIN Medicine m ON ie.Medicine_ID = m.Medicine_ID
    WHERE m.Brand_Name LIKE '%' + @MedicineName + '%' 
       OR m.Generic_Name LIKE '%' + @MedicineName + '%'
    ORDER BY ie.Quantity DESC;
END;
GO

-- ---------------------------------------------
-- SP 2: Update inventory with automatic alert generation
-- ---------------------------------------------
GO
CREATE OR ALTER PROCEDURE sp_UpdateInventory
    @Pharmacy_ID INT,
    @Medicine_ID INT,
    @NewQuantity INT,
    @Batch_No VARCHAR(50) = NULL
AS
BEGIN
    DECLARE @OldQuantity INT;
    DECLARE @ReorderLevel INT;
    DECLARE @Inventory_ID INT;
    
    -- Get current values
    SELECT @OldQuantity = Quantity, @ReorderLevel = Reorder_Level, @Inventory_ID = Inventory_ID
    FROM Inventory_Entry
    WHERE Pharmacy_ID = @Pharmacy_ID AND Medicine_ID = @Medicine_ID;
    
    -- Update inventory
    UPDATE Inventory_Entry
    SET Quantity = @NewQuantity,
        Last_Updated = GETDATE(),
        Batch_No = ISNULL(@Batch_No, Batch_No)
    WHERE Pharmacy_ID = @Pharmacy_ID AND Medicine_ID = @Medicine_ID;
    
    -- Insert low stock alert if needed
    IF @NewQuantity <= @ReorderLevel AND @NewQuantity > 0
    BEGIN
        INSERT INTO LowStockAlert (Inventory_ID, Alert_Type)
        VALUES (@Inventory_ID, 'Low Stock');
    END
    
    -- Insert out of stock alert if needed
    IF @NewQuantity = 0 AND @OldQuantity > 0
    BEGIN
        INSERT INTO LowStockAlert (Inventory_ID, Alert_Type)
        VALUES (@Inventory_ID, 'Out of Stock');
    END
    
    -- Return status
    SELECT 'Inventory updated successfully' AS Message, @NewQuantity AS New_Quantity;
END;
GO

-- ---------------------------------------------
-- SP 3: Generate restock report for a pharmacy
-- ---------------------------------------------
GO
CREATE OR ALTER PROCEDURE sp_RestockReport
    @Pharmacy_ID INT
AS
BEGIN
    SELECT 
        m.Medicine_ID,
        m.Brand_Name,
        m.Generic_Name,
        ie.Quantity AS Current_Stock,
        ie.Reorder_Level,
        (ie.Reorder_Level - ie.Quantity) AS Quantity_To_Order,
        s.Supplier_Name AS Recommended_Supplier,
        m.Unit_Price,
        (ie.Reorder_Level - ie.Quantity) * m.Unit_Price AS Estimated_Cost
    FROM Inventory_Entry ie
    INNER JOIN Medicine m ON ie.Medicine_ID = m.Medicine_ID
    LEFT JOIN Medicine_Supplier ms ON m.Medicine_ID = ms.Medicine_ID AND ms.Is_Primary = 1
    LEFT JOIN Supplier s ON ms.Supplier_ID = s.Supplier_ID
    WHERE ie.Pharmacy_ID = @Pharmacy_ID 
      AND ie.Quantity <= ie.Reorder_Level
      AND ie.Quantity > 0
    ORDER BY (ie.Reorder_Level - ie.Quantity) DESC;
END;
GO

-- =============================================================================
-- SECTION 5: TRIGGERS (Automatic auditing & validation)
-- =============================================================================

-- ---------------------------------------------
-- Trigger 1: Auto-update Last_Updated on inventory changes
-- ---------------------------------------------
GO
CREATE OR ALTER TRIGGER trg_Inventory_LastUpdated
ON Inventory_Entry
AFTER UPDATE
AS
BEGIN
    IF UPDATE(Quantity) OR UPDATE(Unit_Cost)
    BEGIN
        UPDATE ie
        SET Last_Updated = GETDATE()
        FROM Inventory_Entry ie
        INNER JOIN inserted i ON ie.Inventory_ID = i.Inventory_ID;
    END
END;
GO

-- ---------------------------------------------
-- Trigger 2: Audit log for medicine deletions
-- ---------------------------------------------
GO
CREATE OR ALTER TRIGGER trg_Medicine_Audit
ON Medicine
AFTER DELETE
AS
BEGIN
    INSERT INTO Audit_Log (Table_Name, Record_ID, Action_Type, Old_Value, Changed_By)
    SELECT 'Medicine', Medicine_ID, 'DELETE', 
           (SELECT * FROM deleted FOR XML AUTO), SYSTEM_USER
    FROM deleted;
END;
GO

-- ---------------------------------------------
-- Trigger 3: Prevent deletion of medicines with active inventory
-- ---------------------------------------------
GO
CREATE OR ALTER TRIGGER trg_PreventMedicineDeletion
ON Medicine
INSTEAD OF DELETE
AS
BEGIN
    IF EXISTS (
        SELECT 1 FROM deleted d
        INNER JOIN Inventory_Entry ie ON d.Medicine_ID = ie.Medicine_ID
        WHERE ie.Quantity > 0
    )
    BEGIN
        RAISERROR('Cannot delete medicine with existing inventory stock. Archive instead.', 16, 1);
        RETURN;
    END
    
    DELETE FROM Medicine WHERE Medicine_ID IN (SELECT Medicine_ID FROM deleted);
END;
GO

-- =============================================================================
-- SECTION 6: VIEWS (Simplified data access)
-- =============================================================================

-- ---------------------------------------------
-- View 1: Current inventory status summary
-- ---------------------------------------------
GO
CREATE OR ALTER VIEW vw_CurrentInventory AS
SELECT 
    p.Name AS Pharmacy,
    p.Location,
    m.Brand_Name AS Medicine,
    m.Generic_Name,
    c.Category_Name,
    ie.Quantity,
    ie.Unit_Cost,
    ie.Expiry_Date,
    DATEDIFF(DAY, GETDATE(), ie.Expiry_Date) AS Days_Until_Expiry,
    CASE 
        WHEN ie.Quantity = 0 THEN 'OUT OF STOCK'
        WHEN ie.Quantity <= ie.Reorder_Level THEN 'LOW STOCK'
        WHEN DATEDIFF(DAY, GETDATE(), ie.Expiry_Date) < 30 THEN 'EXPIRING SOON'
        ELSE 'OK'
    END AS Status
FROM Inventory_Entry ie
JOIN Pharmacy p ON ie.Pharmacy_ID = p.Pharmacy_ID
JOIN Medicine m ON ie.Medicine_ID = m.Medicine_ID
JOIN Category c ON m.Category_ID = c.Category_ID;
GO

-- ---------------------------------------------
-- View 2: Pharmacy performance dashboard
-- ---------------------------------------------
GO
CREATE OR ALTER VIEW vw_PharmacyDashboard AS
SELECT 
    p.Pharmacy_ID,
    p.Name AS Pharmacy_Name,
    COUNT(DISTINCT ie.Medicine_ID) AS Unique_Medicines,
    SUM(ie.Quantity) AS Total_Stock_Units,
    SUM(ie.Quantity * ie.Unit_Cost) AS Total_Inventory_Value,
    COUNT(CASE WHEN ie.Quantity <= ie.Reorder_Level THEN 1 END) AS Low_Stock_Items,
    COUNT(CASE WHEN ie.Quantity = 0 THEN 1 END) AS Out_Of_Stock_Items,
    COUNT(CASE WHEN ie.Expiry_Date <= DATEADD(DAY, 90, GETDATE()) THEN 1 END) AS Expiring_90_Days
FROM Pharmacy p
LEFT JOIN Inventory_Entry ie ON p.Pharmacy_ID = ie.Pharmacy_ID
GROUP BY p.Pharmacy_ID, p.Name;
GO

-- =============================================================================
-- SECTION 7: FUNCTIONS (Reusable logic)
-- =============================================================================

-- ---------------------------------------------
-- Function 1: Calculate total inventory value for a pharmacy
-- ---------------------------------------------
GO
CREATE OR ALTER FUNCTION fn_TotalInventoryValue(@Pharmacy_ID INT)
RETURNS DECIMAL(12,2)
AS
BEGIN
    DECLARE @Total DECIMAL(12,2);
    
    SELECT @Total = SUM(Quantity * Unit_Cost)
    FROM Inventory_Entry
    WHERE Pharmacy_ID = @Pharmacy_ID;
    
    RETURN ISNULL(@Total, 0);
END;
GO

-- ---------------------------------------------
-- Function 2: Check if a medicine is available at a pharmacy
-- ---------------------------------------------
GO
CREATE OR ALTER FUNCTION fn_IsMedicineAvailable(
    @Pharmacy_ID INT, 
    @Medicine_ID INT, 
    @RequiredQuantity INT
)
RETURNS BIT
AS
BEGIN
    DECLARE @Available BIT = 0;
    DECLARE @CurrentQuantity INT;
    
    SELECT @CurrentQuantity = Quantity
    FROM Inventory_Entry
    WHERE Pharmacy_ID = @Pharmacy_ID AND Medicine_ID = @Medicine_ID;
    
    IF @CurrentQuantity >= @RequiredQuantity
        SET @Available = 1;
    
    RETURN @Available;
END;
GO

-- =============================================================================
-- SECTION 8: SAMPLE DATA (Meaningful and realistic)
-- =============================================================================

-- Insert Categories (with hierarchy)
INSERT INTO Category (Category_Code, Category_Name, Description, Parent_Category_ID) VALUES
('CAT_A01', 'Antibiotics', 'Medicines that fight bacterial infections', NULL),
('CAT_A02', 'Penicillins', 'Beta-lactam antibiotics', 1),
('CAT_A03', 'Cephalosporins', 'Broad-spectrum antibiotics', 1),
('CAT_B01', 'Cardiovascular', 'Heart and blood pressure medications', NULL),
('CAT_B02', 'Antihypertensives', 'Blood pressure lowering drugs', 4),
('CAT_C01', 'Analgesics', 'Pain relievers', NULL),
('CAT_C02', 'Opioids', 'Strong prescription pain relievers', 6),
('CAT_C03', 'NSAIDs', 'Non-steroidal anti-inflammatory drugs', 6),
('CAT_D01', 'Pediatric', 'Medicines formulated for children', NULL),
('CAT_E01', 'Vitamins', 'Dietary supplements', NULL);

-- Insert Suppliers
INSERT INTO Supplier (Supplier_Name, Contact_Person, Phone, Email, Address, Tax_ID, Rating) VALUES
('PharmaDistro Inc.', 'John Smith', '800-555-0101', 'orders@pharmadistro.com', '123 Distribution Way, Chicago, IL', 'TAX-1001', 4.8),
('MedSupply Global', 'Sarah Johnson', '800-555-0102', 'contact@medsupply.com', '456 Logistics Ave, Dallas, TX', 'TAX-1002', 4.5),
('HealthLogistics LLC', 'Mike Chen', '800-555-0103', 'sales@healthlogistics.com', '789 Commerce Blvd, Los Angeles, CA', 'TAX-1003', 4.2),
('DirectMed Pharma', 'Lisa Wong', '800-555-0104', 'info@directmed.com', '321 Medical Mile, Boston, MA', 'TAX-1004', 4.9);

-- Insert Medicines
INSERT INTO Medicine (Medicine_Code, Brand_Name, Generic_Name, Manufacturer, Category_ID, Unit_Price, Dosage_Form, Strength, Requires_Prescription) VALUES
('MED-001', 'Amoxil', 'Amoxicillin', 'GSK', 2, 15.50, 'Capsule', '500mg', 1),
('MED-002', 'Augmentin', 'Co-amoxiclav', 'GSK', 2, 28.75, 'Tablet', '875mg', 1),
('MED-003', 'Cipro', 'Ciprofloxacin', 'Bayer', 3, 22.30, 'Tablet', '500mg', 1),
('MED-004', 'Lipitor', 'Atorvastatin', 'Pfizer', 5, 45.00, 'Tablet', '20mg', 1),
('MED-005', 'Norvasc', 'Amlodipine', 'Pfizer', 5, 32.50, 'Tablet', '10mg', 1),
('MED-006', 'Tylenol', 'Acetaminophen', 'J&J', 7, 8.99, 'Tablet', '500mg', 0),
('MED-007', 'Advil', 'Ibuprofen', 'Pfizer', 8, 9.50, 'Capsule', '200mg', 0),
('MED-008', 'Claritin', 'Loratadine', 'Bayer', 6, 12.75, 'Tablet', '10mg', 0),
('MED-009', 'Pedialyte', 'Electrolyte Solution', 'Abbott', 9, 7.50, 'Liquid', '1L', 0),
('MED-010', 'Vitamin D3', 'Cholecalciferol', 'NatureMade', 10, 11.25, 'Capsule', '1000 IU', 0),
('MED-011', 'Ventolin', 'Albuterol', 'GSK', 1, 55.00, 'Inhaler', '90mcg', 1),
('MED-012', 'Metformin', 'Metformin HCl', 'Merck', 5, 18.50, 'Tablet', '500mg', 1);

-- Insert Pharmacies
INSERT INTO Pharmacy (Pharmacy_Code, Name, Location, License_No, Contact_Info, Email, Operating_Hours, Is_24_Hours) VALUES
('PH-001', 'City Health Pharmacy', '123 Main St, Anytown, ST 12345', 'LIC-001-ABC', '555-0100', 'cityhealth@pharmacy.com', 'Mon-Fri 8am-8pm, Sat 9am-5pm', 0),
('PH-002', 'Wellness Rx', '456 Oak Ave, Sometown, ST 67890', 'LIC-002-DEF', '555-0200', 'wellness@pharmacy.com', 'Mon-Sun 7am-11pm', 0),
('PH-003', 'Downtown Medicals', '789 Pine Blvd, Yourcity, ST 11223', 'LIC-003-GHI', '555-0300', 'downtown@pharmacy.com', '24/7', 1),
('PH-004', 'Family Care Pharmacy', '321 Elm Street, Othertown, ST 44556', 'LIC-004-JKL', '555-0400', 'familycare@pharmacy.com', 'Mon-Fri 9am-7pm, Sat 10am-3pm', 0);

-- Insert Inventory Entries (Realistic stock data)
INSERT INTO Inventory_Entry (Pharmacy_ID, Medicine_ID, Quantity, Reorder_Level, Unit_Cost, Batch_No, Expiry_Date) VALUES
-- City Health Pharmacy
(1, 100, 150, 20, 12.50, 'BATCH-A001', '2025-12-31'),
(1, 101, 75, 15, 24.00, 'BATCH-A002', '2025-10-15'),
(1, 104, 45, 10, 38.00, 'BATCH-B001', '2025-08-20'),
(1, 106, 200, 50, 6.50, 'BATCH-C001', '2026-03-10'),

-- Wellness Rx
(2, 102, 50, 10, 18.75, 'BATCH-D001', '2025-09-30'),
(2, 106, 120, 30, 6.50, 'BATCH-C002', '2026-02-28'),
(2, 107, 30, 15, 7.50, 'BATCH-E001', '2025-11-15'),
(2, 108, 80, 20, 10.00, 'BATCH-F001', '2025-12-01'),
(2, 109, 0, 5, 5.50, 'BATCH-G001', '2024-12-31'), -- Expired/out of stock
(2, 111, 25, 8, 42.00, 'BATCH-H001', '2025-07-15'),

-- Downtown Medicals (24-hour pharmacy)
(3, 100, 80, 25, 12.50, 'BATCH-A003', '2026-01-15'),
(3, 103, 35, 10, 19.00, 'BATCH-I001', '2025-10-10'),
(3, 107, 210, 40, 7.50, 'BATCH-E002', '2026-04-20'),
(3, 108, 15, 10, 10.00, 'BATCH-F002', '2025-11-30'),
(3, 110, 60, 15, 9.00, 'BATCH-J001', '2026-05-01'),
(3, 111, 40, 10, 42.00, 'BATCH-H002', '2025-08-20'),

-- Family Care Pharmacy
(4, 101, 90, 20, 24.00, 'BATCH-A004', '2025-12-15'),
(4, 104, 30, 10, 38.00, 'BATCH-B002', '2025-09-05'),
(4, 105, 55, 15, 28.00, 'BATCH-K001', '2025-10-25'),
(4, 109, 120, 25, 5.50, 'BATCH-G002', '2026-02-01'),
(4, 112, 150, 30, 14.00, 'BATCH-L001', '2026-03-15');

-- Insert Medicine-Supplier relationships
INSERT INTO Medicine_Supplier (Medicine_ID, Supplier_ID, Is_Primary, Contract_Start_Date, Contract_End_Date) VALUES
(100, 1, 1, '2024-01-01', '2025-12-31'),
(101, 1, 1, '2024-01-01', '2025-12-31'),
(102, 2, 1, '2024-03-01', '2025-12-31'),
(103, 2, 0, '2024-03-01', '2025-12-31'),
(104, 3, 1, '2024-02-01', '2025-12-31'),
(105, 3, 0, '2024-02-01', '2025-12-31'),
(106, 4, 1, '2024-01-01', '2025-12-31'),
(107, 4, 1, '2024-01-01', '2025-12-31'),
(108, 1, 0, '2024-04-01', '2025-12-31'),
(109, 2, 1, '2024-05-01', '2025-12-31'),
(110, 3, 1, '2024-01-01', '2025-12-31'),
(111, 1, 1, '2024-06-01', '2025-12-31');

-- Insert Prescriptions
INSERT INTO Prescription (Prescription_No, Patient_Name, Patient_Phone, Doctor_Name, Issue_Date, Valid_Until, Status, Pharmacy_ID) VALUES
('RX-001', 'John Doe', '555-1000', 'Dr. Smith', '2025-05-01', '2025-08-01', 'Filled', 1),
('RX-002', 'Jane Smith', '555-1001', 'Dr. Jones', '2025-05-10', '2025-08-10', 'Pending', 2),
('RX-003', 'Robert Johnson', '555-1002', 'Dr. Brown', '2025-05-15', '2025-08-15', 'Partially Filled', 3),
('RX-004', 'Maria Garcia', '555-1003', 'Dr. Wilson', '2025-05-20', '2025-08-20', 'Pending', 1),
('RX-005', 'David Lee', '555-1004', 'Dr. Taylor', '2025-04-01', '2025-07-01', 'Expired', NULL);

-- Insert Prescription Items
INSERT INTO Prescription_Item (Prescription_ID, Medicine_ID, Quantity_Prescribed, Quantity_Filled, Instructions) VALUES
(1, 100, 30, 30, 'Take one capsule twice daily for 15 days'),
(1, 106, 20, 20, 'Take one tablet every 6 hours as needed'),
(2, 107, 30, 0, 'Take one tablet every 8 hours for pain'),
(2, 108, 15, 0, 'Take one tablet daily for allergies'),
(3, 104, 90, 45, 'Take one tablet daily for cholesterol'),
(3, 105, 60, 30, 'Take one tablet daily for blood pressure'),
(4, 111, 2, 2, 'Use as needed for breathing difficulties'),
(5, 100, 20, 0, 'Expired prescription');

-- =============================================================================
-- SECTION 9: DEMONSTRATION QUERIES (For the documentation)
-- =============================================================================

-- Query 1: Find all pharmacies with a specific medicine in stock
-- EXEC sp_CheckMedicineAvailability 'Amoxicillin';

-- Query 2: Generate restock report for a pharmacy
-- EXEC sp_RestockReport 1;

-- Query 3: View current inventory summary
-- SELECT * FROM vw_CurrentInventory;

-- Query 4: View pharmacy dashboard
-- SELECT * FROM vw_PharmacyDashboard;

-- Query 5: Calculate total inventory value for a pharmacy
-- SELECT dbo.fn_TotalInventoryValue(1) AS Total_Inventory_Value;

-- Query 6: Complex report - Pharmacies with expiring medicines
/*
SELECT 
    p.Name,
    m.Brand_Name,
    ie.Batch_No,
    ie.Expiry_Date,
    DATEDIFF(DAY, GETDATE(), ie.Expiry_Date) AS Days_Left,
    ie.Quantity
FROM Inventory_Entry ie
JOIN Pharmacy p ON ie.Pharmacy_ID = p.Pharmacy_ID
JOIN Medicine m ON ie.Medicine_ID = m.Medicine_ID
WHERE ie.Expiry_Date <= DATEADD(DAY, 90, GETDATE())
  AND ie.Quantity > 0
ORDER BY ie.Expiry_Date ASC;
*/

-- Query 7: Supplier performance summary
/*
SELECT 
    s.Supplier_Name,
    COUNT(DISTINCT ms.Medicine_ID) AS Medicines_Supplied,
    AVG(s.Rating) AS Avg_Rating
FROM Supplier s
JOIN Medicine_Supplier ms ON s.Supplier_ID = ms.Supplier_ID
GROUP BY s.Supplier_Name;
*/

-- =============================================================================
-- END OF SCRIPT
-- =============================================================================
