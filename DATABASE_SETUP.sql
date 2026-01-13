 -- CDS Dashboard - Database Setup Script
-- Execute these SQL statements in your OpenMRS database

-- ============================================================================
-- OPTION 1: Single Unified Table (Simpler for smaller use cases)
-- ============================================================================

DROP TABLE IF EXISTS cds_actions_table;

CREATE TABLE cds_actions_table (
    action_id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Unique action identifier',
    patient_id INT NOT NULL COMMENT 'Reference to patient',
    action_type VARCHAR(50) NOT NULL COMMENT 'Type: DOCUMENTED_ACTION or TRACKING_EFFORT',

    -- Fields for Documented Actions
    call_report LONGTEXT COMMENT 'Description of action taken',
    assigned_to_user_id INT COMMENT 'User assigned to this action',
    assigned_to_user_name VARCHAR(255) COMMENT 'Name of assigned user',
    next_step_action VARCHAR(500) COMMENT 'Next action to be taken',

    -- Fields for Tracking Efforts
    verification_type VARCHAR(100) COMMENT 'Type of verification: Client Verification, Records Verification, LTFU, etc.',
    contact_method VARCHAR(100) COMMENT 'How contact was made: Phone Call, Home Visit, CHW, etc.',
    attempted_contact_date DATE COMMENT 'When contact was attempted',
    tracking_outcome LONGTEXT COMMENT 'Outcome of tracking effort',

    -- Common Fields
    status VARCHAR(50) DEFAULT 'PENDING' COMMENT 'Status: PENDING, COMPLETED, ONGOING, DISCONTINUED, etc.',
    notes LONGTEXT COMMENT 'Additional notes',
    created_by_user_id INT COMMENT 'User who created this record',
    created_by_user_name VARCHAR(255) COMMENT 'Name of user who created record',
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'When record was created',
    date_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When record was last modified',
    is_voided TINYINT DEFAULT 0 COMMENT '0=active, 1=deleted/voided',

    -- Indexes for better query performance
    INDEX idx_patient_id (patient_id),
    INDEX idx_action_type (action_type),
    INDEX idx_status (status),
    INDEX idx_date_created (date_created),
    INDEX idx_patient_type (patient_id, action_type),

    -- Foreign key constraint
    CONSTRAINT fk_cds_patient FOREIGN KEY (patient_id)
        REFERENCES patient(patient_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='Stores documented actions and tracking efforts for patients';

-- ============================================================================
-- OPTION 2: Separate Tables (Recommended for larger implementations)
-- ============================================================================

DROP TABLE IF EXISTS cds_documented_actions;
DROP TABLE IF EXISTS cds_tracking_efforts;

CREATE TABLE cds_documented_actions (
    action_id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Unique action identifier',
    patient_id INT NOT NULL COMMENT 'Reference to patient',
    call_report LONGTEXT COMMENT 'Description of action taken',
    assigned_to_user_id INT COMMENT 'User assigned to this action',
    assigned_to_user_name VARCHAR(255) COMMENT 'Name of assigned user',
    next_step_action VARCHAR(500) COMMENT 'Next action to be taken',
    status VARCHAR(50) DEFAULT 'PENDING' COMMENT 'Status: PENDING, COMPLETED, IN_PROGRESS, etc.',
    notes LONGTEXT COMMENT 'Additional notes',
    created_by_user_id INT COMMENT 'User who created this record',
    created_by_user_name VARCHAR(255) COMMENT 'Name of user who created record',
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'When record was created',
    date_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When record was last modified',
    is_voided TINYINT DEFAULT 0 COMMENT '0=active, 1=deleted/voided',

    -- Indexes
    INDEX idx_patient_id (patient_id),
    INDEX idx_status (status),
    INDEX idx_date_created (date_created),
    INDEX idx_assigned_to (assigned_to_user_id),

    -- Foreign key
    CONSTRAINT fk_cds_doc_action_patient FOREIGN KEY (patient_id)
        REFERENCES patient(patient_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='Stores documented clinical actions for patients';

CREATE TABLE cds_tracking_efforts (
    tracking_id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Unique tracking identifier',
    patient_id INT NOT NULL COMMENT 'Reference to patient',
    verification_type VARCHAR(100) COMMENT 'Type: Client Verification, Records Verification, LTFU, Transferred Out, etc.',
    contact_method VARCHAR(100) COMMENT 'Method: Phone Call, Home Visit, Contact via CHW, Community Outreach, Records Review',
    attempted_contact_date DATE COMMENT 'When contact was attempted',
    status VARCHAR(50) COMMENT 'Status: Ongoing, Discontinued, Records Verified, Successfully Located, Unable to Locate',
    outcome LONGTEXT COMMENT 'Outcome of tracking effort',
    notes LONGTEXT COMMENT 'Additional notes',
    created_by_user_id INT COMMENT 'User who created this record',
    created_by_user_name VARCHAR(255) COMMENT 'Name of user who created record',
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'When record was created',
    date_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When record was last modified',
    is_voided TINYINT DEFAULT 0 COMMENT '0=active, 1=deleted/voided',

    -- Indexes
    INDEX idx_patient_id (patient_id),
    INDEX idx_verification_type (verification_type),
    INDEX idx_status (status),
    INDEX idx_contact_method (contact_method),
    INDEX idx_date_created (date_created),
    INDEX idx_attempted_date (attempted_contact_date),

    -- Foreign key
    CONSTRAINT fk_cds_tracking_patient FOREIGN KEY (patient_id)
        REFERENCES patient(patient_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='Stores client tracking and follow-up efforts';

-- ============================================================================
-- VIEW: Get all actions and tracking efforts for a patient (for OPTION 1)
-- ============================================================================

DROP VIEW IF EXISTS cds_patient_actions_view;

CREATE VIEW cds_patient_actions_view AS
SELECT
    patient_id,
    action_type,
    date_created,
    status,
    CASE
        WHEN action_type = 'DOCUMENTED_ACTION' THEN call_report
        WHEN action_type = 'TRACKING_EFFORT' THEN CONCAT(verification_type, ' - ', outcome)
        ELSE 'Unknown'
    END AS description,
    CASE
        WHEN action_type = 'DOCUMENTED_ACTION' THEN assigned_to_user_name
        ELSE created_by_user_name
    END AS responsible_person
FROM cds_actions_table
WHERE is_voided = 0
ORDER BY date_created DESC;

-- ============================================================================
-- SAMPLE DATA FOR TESTING
-- ============================================================================

-- Clear existing test data (optional)
-- DELETE FROM cds_actions_table WHERE patient_id = 271;

-- Insert sample documented action (OPTION 1 - Unified Table)
INSERT INTO cds_actions_table (
    patient_id,
    action_type,
    call_report,
    assigned_to_user_id,
    assigned_to_user_name,
    next_step_action,
    status,
    notes,
    created_by_user_id,
    created_by_user_name,
    date_created
) VALUES (
    271,
    'DOCUMENTED_ACTION',
    'Patient called and counseled about medication adherence. Patient reported missing doses due to work schedule.',
    5,
    'John Doe',
    'Schedule follow-up call in 2 weeks',
    'PENDING',
    'Patient seemed receptive to counseling. Provided contact information for clinic support.',
    1,
    'Admin User',
    NOW()
);

-- Insert sample tracking effort (OPTION 1 - Unified Table)
INSERT INTO cds_actions_table (
    patient_id,
    action_type,
    verification_type,
    contact_method,
    attempted_contact_date,
    tracking_outcome,
    status,
    notes,
    created_by_user_id,
    created_by_user_name,
    date_created
) VALUES (
    271,
    'TRACKING_EFFORT',
    'Client Verification',
    'Phone Call',
    '2026-01-13',
    'Patient confirmed alive and currently on ART. Taking medications as prescribed.',
    'Ongoing',
    'Called patient and spoke with family member. Patient doing well.',
    1,
    'Admin User',
    NOW()
);

-- Insert sample documented action (OPTION 2 - Separate Table)
-- INSERT INTO cds_documented_actions (
--     patient_id,
--     call_report,
--     assigned_to_user_id,
--     assigned_to_user_name,
--     next_step_action,
--     status,
--     notes,
--     created_by_user_id,
--     created_by_user_name
-- ) VALUES (
--     271,
--     'Patient called and counseled about medication adherence',
--     5,
--     'John Doe',
--     'Schedule follow-up visit',
--     'PENDING',
--     'Patient seemed compliant',
--     1,
--     'Admin User'
-- );

-- Insert sample tracking effort (OPTION 2 - Separate Table)
-- INSERT INTO cds_tracking_efforts (
--     patient_id,
--     verification_type,
--     contact_method,
--     attempted_contact_date,
--     status,
--     outcome,
--     notes,
--     created_by_user_id,
--     created_by_user_name
-- ) VALUES (
--     271,
--     'Client Verification',
--     'Phone Call',
--     '2026-01-13',
--     'Ongoing',
--     'Patient confirmed alive and taking medications',
--     'Called patient mother who confirmed',
--     1,
--     'Admin User'
-- );

-- ============================================================================
-- VERIFICATION QUERIES
-- ============================================================================

-- Verify tables created successfully
-- SHOW TABLES LIKE 'cds_%';

-- Check table structure (OPTION 1)
-- DESCRIBE cds_actions_table;

-- Check table structure (OPTION 2)
-- DESCRIBE cds_documented_actions;
-- DESCRIBE cds_tracking_efforts;

-- Get all documented actions for patient 271 (OPTION 1)
-- SELECT * FROM cds_actions_table
-- WHERE patient_id = 271 AND action_type = 'DOCUMENTED_ACTION' AND is_voided = 0
-- ORDER BY date_created DESC;

-- Get all tracking efforts for patient 271 (OPTION 1)
-- SELECT * FROM cds_actions_table
-- WHERE patient_id = 271 AND action_type = 'TRACKING_EFFORT' AND is_voided = 0
-- ORDER BY date_created DESC;

-- Get all actions and tracking for patient 271 (OPTION 1 with view)
-- SELECT * FROM cds_patient_actions_view WHERE patient_id = 271;

-- Get recent 10 actions (OPTION 1)
-- SELECT * FROM cds_actions_table
-- WHERE is_voided = 0
-- ORDER BY date_created DESC
-- LIMIT 10;

-- Count actions by type for a patient
-- SELECT action_type, COUNT(*) as count
-- FROM cds_actions_table
-- WHERE patient_id = 271 AND is_voided = 0
-- GROUP BY action_type;

-- Count actions by status
-- SELECT status, COUNT(*) as count
-- FROM cds_actions_table
-- WHERE patient_id = 271 AND is_voided = 0
-- GROUP BY status;

-- ============================================================================
-- MAINTENANCE QUERIES
-- ============================================================================

-- Update status of an action
-- UPDATE cds_actions_table
-- SET status = 'COMPLETED', date_modified = NOW()
-- WHERE action_id = 1;

-- Void (soft delete) an action
-- UPDATE cds_actions_table
-- SET is_voided = 1, date_modified = NOW()
-- WHERE action_id = 1;

-- Delete all test data for patient 271 (HARD DELETE - use with caution!)
-- DELETE FROM cds_actions_table WHERE patient_id = 271;

-- Get actions created in last 30 days
-- SELECT * FROM cds_actions_table
-- WHERE is_voided = 0
-- AND date_created >= DATE_SUB(NOW(), INTERVAL 30 DAY)
-- ORDER BY date_created DESC;

-- ============================================================================
-- NOTES
-- ============================================================================
/*
IMPORTANT CONSIDERATIONS:

1. Choose OPTION 1 (unified table) if:
   - You have a small to medium patient population
   - You want simpler queries and management
   - You don't need separate permissions for actions vs tracking

2. Choose OPTION 2 (separate tables) if:
   - You have a large patient population
   - You need better data organization
   - You want separate reports for actions vs tracking
   - You plan to have different SLAs for different action types

3. Backup your database before running these scripts in production

4. Test all queries in a development environment first

5. After creating tables, update the CdsActionsService implementation
   to query the appropriate table(s)

6. Add appropriate audit logging for compliance requirements

7. Consider encryption for sensitive data in call_report and outcome fields

8. Set up regular data exports for reporting and analysis

9. Implement data retention policy (e.g., archive old records after 2 years)

10. Monitor table sizes and optimize indexes as needed
*/

