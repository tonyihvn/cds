## CDS Dashboard Implementation Guide

### Overview
The Clinical Data System (CDS) module now includes a comprehensive dashboard with tabbed interface for tracking:
- **Verge of IIT (Interruption in Treatment)**: Patients at risk of treatment discontinuation
- **Missed Appointments**: Patients who missed appointments in the last 27 days
- **Upcoming Appointments**: Patients with scheduled appointments in the next 30 days
- **Pending Actions**: Internal task tracking for staff

### Architecture

#### Fragment Controller: `CdsFragmentController.java`
Located in: `omod/src/main/java/org/openmrs/module/cds/fragment/controller/`

**Main Methods:**
- `controller()` - Main dashboard with statistics and patient lists
- `iitList()` - Fragment for IIT patient list
- `missedList()` - Fragment for missed appointments
- `upcomingList()` - Fragment for upcoming appointments
- `actionsList()` - Fragment for pending actions
- `clientEffort()` - Fragment for patient tracking history

#### Fragment Views
Located in: `omod/src/main/webapp/fragments/cds/`

1. **iitList.gsp** - Verge of IIT patients
   - Red status indicator (table-danger)
   - "Assign Action" and "View History" buttons per patient

2. **missedList.gsp** - Missed appointments
   - Yellow status indicator (table-warning)
   - "Assign Action" and "View History" buttons per patient

3. **upcomingList.gsp** - Upcoming appointments
   - Green status indicator (table-success)
   - "View Patient" and "View History" buttons

4. **actionsList.gsp** - Pending actions
   - Displays action ID, patient, action type, call report
   - Shows assigned user
   - "Mark Complete" button for status updates

5. **clientEffort.gsp** - Patient tracking history
   - Shows all tracking encounters (form 13)
   - Displays verification status and comments
   - Sorted by date descending

#### Main Dashboard Page
Located in: `omod/src/main/webapp/pages/cds.gsp`

**Features:**
- Statistics boxes showing counts for each category
- Color-coded boxes:
  - Red: Verge of IIT (dc3545)
  - Yellow: Missed Appointments (ffc107)
  - Green: Upcoming Appointments (28a745)
  - Blue: Pending Actions (007bff)
- Bootstrap tab navigation
- Responsive grid layout

### Service Integration

The dashboard integrates with `ClinicalDataSystemService` which provides:
- `getIITPatientIds(lookbackDays)` - Patients on verge of IIT
- `getMissedAppointmentPatientIds(lastDays)` - Missed appointments
- `getUpcomingAppointmentPatientIds(withinDays)` - Upcoming appointments
- `getPendingCdsActions()` - Pending staff actions
- `getClientEffort(patientId)` - Patient tracking history

### Database Queries Used

The DAO layer (`ClinicalDataSystemDao`) implements the following SQL queries:

1. **Upcoming Appointments Query**
   ```sql
   SELECT distinct e.patient_id FROM obs o
   JOIN encounter e ON o.encounter_id = e.encounter_id
   WHERE o.concept_id = 5096 AND e.form_id = 27
   AND o.value_datetime BETWEEN :now AND :until
   AND o.voided = 0 AND e.voided = 0
   ```

2. **Missed Appointments Query**
   ```sql
   SELECT distinct e.patient_id FROM obs o
   JOIN encounter e ON o.encounter_id = e.encounter_id
   WHERE o.concept_id = 5096 AND e.form_id = 27
   AND o.value_datetime < :now AND o.value_datetime > :fromDate
   AND NOT EXISTS (...)
   AND o.voided = 0 AND e.voided = 0
   ```

3. **IIT Query**
   ```sql
   SELECT distinct e.patient_id FROM encounter e
   JOIN obs o_appt ON o_appt.person_id = e.patient_id
   WHERE e.form_id = 13 AND o_appt.concept_id = 5096
   AND NOT EXISTS (SELECT 1 FROM obs o_disc WHERE ... AND concept_id = 165470)
   ```

4. **Client Effort Query**
   ```sql
   SELECT e.encounter_datetime, o_status.value_coded, o_comment.value_text
   FROM encounter e
   LEFT JOIN obs o_status ON e.encounter_id = o_status.encounter_id
   LEFT JOIN obs o_comment ON e.encounter_id = o_comment.encounter_id
   WHERE e.patient_id = :patientId AND e.form_id = 13
   ORDER BY e.encounter_datetime DESC
   ```

### Custom Database Table

The module uses a custom table for action tracking:

```sql
CREATE TABLE cds_actions_table (
    action_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    encounter_id INT,
    call_report TEXT,
    next_step_action VARCHAR(255),
    assigned_to_user_id INT,
    status ENUM('PENDING', 'COMPLETED', 'REPORTED_TO_HQ') DEFAULT 'PENDING',
    date_created DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patient(patient_id),
    FOREIGN KEY (assigned_to_user_id) REFERENCES users(user_id)
);
```

### Removed Files

The following files have been removed as they are no longer needed:
- `UsersFragmentController.java`
- `users.gsp`

These have been replaced with the CDS-specific functionality.

### CSS Styling

The dashboard includes custom CSS for:
- Dashboard statistics boxes with color coding
- Responsive grid layout
- Table styling with status colors
- Bootstrap integration
- Empty state messages with icons
- Button groups for actions

### Future Enhancements

1. **Modal for Assigning Actions** - Currently shows alert; should implement proper modal dialog
2. **AJAX Status Updates** - "Mark Complete" button should update via AJAX without page reload
3. **Export to HQ** - REST endpoint for syncing pending actions to headquarters
4. **Advanced Filtering** - Filter by date range, assigned user, action type
5. **Bulk Actions** - Select multiple patients for bulk action assignment
6. **Search and Sort** - Patient search and column sorting
7. **Client Detail View** - Detailed patient card with more information

### Configuration

**Default Time Periods:**
- Upcoming Appointments: 30 days
- Missed Appointments: 27 days
- IIT Lookback: 27 days

These can be customized via query parameters:
```
/cds/cds.page?upcomingDays=7&missedDays=14&iitDays=14
```

### Navigation

The dashboard is accessible at: `/cds/cds.page`

From the main CDS page, users can:
1. View summary statistics
2. Navigate between tabs for different patient categories
3. Click on patient names to view their full records
4. View tracking history for individual patients
5. Assign and manage actions for specific patients

### Error Handling

The controller includes error handling for:
- Invalid patient IDs
- Missing service data
- Null or empty patient lists
- Database connection issues

All errors are logged and displayed to the user with appropriate messages.

