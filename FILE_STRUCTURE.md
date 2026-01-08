# CDS Module - Updated File Structure

## Summary of Changes

### Created Files

#### Fragment Controller
```
omod/src/main/java/org/openmrs/module/cds/fragment/controller/
└── CdsFragmentController.java (NEW - 6,084 bytes)
    ├── controller() - Main dashboard
    ├── iitList() - IIT patient list
    ├── missedList() - Missed appointments list
    ├── upcomingList() - Upcoming appointments list
    ├── actionsList() - Pending actions list
    └── clientEffort() - Patient tracking history
```

#### Fragment Views (GSP Templates)
```
omod/src/main/webapp/fragments/cds/
├── iitList.gsp (NEW - 2,194 bytes)
├── missedList.gsp (NEW - 2,192 bytes)
├── upcomingList.gsp (NEW - 2,013 bytes)
├── actionsList.gsp (NEW - 3,171 bytes)
└── clientEffort.gsp (NEW - 1,375 bytes)
```

#### Main Dashboard Page
```
omod/src/main/webapp/pages/
└── cds.gsp (UPDATED - 221 lines)
    ├── CSS Styling (responsive grid, color scheme)
    ├── Statistics Dashboard (4 colored boxes)
    ├── Bootstrap Tab Navigation
    └── Fragment Includes
```

#### Documentation
```
Root Project Directory/
├── CDS_DASHBOARD_GUIDE.md (NEW)
│   └── Comprehensive implementation guide with architecture details
└── IMPLEMENTATION_SUMMARY.md (NEW)
    └── Summary of all work completed with testing recommendations
```

### Deleted Files

```
omod/src/main/java/org/openmrs/module/cds/fragment/controller/
└── UsersFragmentController.java (DELETED)

omod/src/main/webapp/fragments/
└── users.gsp (DELETED)
```

---

## Complete Module Structure

```
cds/
├── api/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/org/openmrs/module/cds/
│       │   │   ├── ClinicalDataSystemActivator.java
│       │   │   ├── ClinicalDataSystemConfig.java
│       │   │   ├── Item.java
│       │   │   └── api/
│       │   │       ├── ClinicalDataSystemService.java (interface)
│       │   │       ├── dao/
│       │   │       │   └── ClinicalDataSystemDao.java (queries implementation)
│       │   │       ├── dto/
│       │   │       │   ├── CdsActionRecord.java
│       │   │       │   └── ClientEffortEntry.java
│       │   │       └── impl/
│       │   │           └── ClinicalDataSystemServiceImpl.java
│       │   └── resources/
│       │       └── moduleApplicationContext.xml (Spring configuration)
│       └── test/
│           └── java/org/openmrs/module/cds/
│               └── ClinicalDataSystemServiceTest.java
│
├── omod/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/org/openmrs/module/cds/
│       │   │   ├── extension/
│       │   │   ├── fragment/
│       │   │   │   └── controller/
│       │   │   │       └── CdsFragmentController.java ✅ NEW
│       │   │   └── web/
│       │   │       └── controller/
│       │   ├── resources/
│       │   │   ├── config.xml
│       │   │   └── webModuleApplicationContext.xml
│       │   └── webapp/
│       │       ├── cds.jsp
│       │       ├── fragments/
│       │       │   └── cds/
│       │       │       ├── iitList.gsp ✅ NEW
│       │       │       ├── missedList.gsp ✅ NEW
│       │       │       ├── upcomingList.gsp ✅ NEW
│       │       │       ├── actionsList.gsp ✅ NEW
│       │       │       └── clientEffort.gsp ✅ NEW
│       │       └── pages/
│       │           └── cds.gsp ✅ UPDATED
│       └── test/
│           └── java/org/openmrs/module/cds/
│               └── AdminListExtensionTest.java
│
├── pom.xml
├── README.md
├── CDS_DASHBOARD_GUIDE.md ✅ NEW
└── IMPLEMENTATION_SUMMARY.md ✅ NEW
```

---

## Feature Overview

### Dashboard Statistics (4-Box Design)
| Box | Color | Purpose | Data Source |
|-----|-------|---------|-------------|
| IIT | Red (#dc3545) | Patients verge of treatment interruption | `getIITPatientIds()` |
| Missed | Yellow (#ffc107) | Patients with missed appointments | `getMissedAppointmentPatientIds()` |
| Upcoming | Green (#28a745) | Patients with upcoming appointments | `getUpcomingAppointmentPatientIds()` |
| Actions | Blue (#007bff) | Pending staff actions/tasks | `getPendingCdsActions()` |

### Tabbed Interface
Each tab contains a patient list with the following columns:
- Patient ID
- Patient Name (clickable link to patient chart)
- Status badge
- Action buttons

### Fragment Components

#### iitList.gsp
- Displays patients on verge of IIT
- Red warning indicators
- "Assign Action" and "View History" buttons

#### missedList.gsp
- Displays patients with missed appointments
- Yellow warning indicators
- "Assign Action" and "View History" buttons

#### upcomingList.gsp
- Displays patients with upcoming appointments
- Green success indicators
- "View Patient" and "View History" buttons

#### actionsList.gsp
- Displays pending internal actions/tasks
- Shows assigned staff member
- "Mark Complete" button for status updates

#### clientEffort.gsp
- Shows tracking history for a specific patient
- Displays encounter date, verification status, and comments
- Sorted by most recent first
- Reusable fragment with parameter-driven content

---

## Data Flow

```
User Access
    ↓
/cds/cds.page
    ↓
CdsFragmentController.controller()
    ↓
ClinicalDataSystemService
    ├→ getIITPatientIds()
    ├→ getMissedAppointmentPatientIds()
    ├→ getUpcomingAppointmentPatientIds()
    ├→ getPendingCdsActions()
    └→ getClientEffort()
    ↓
ClinicalDataSystemDao
    ├→ getIITPatientIds() [SQL query]
    ├→ getMissedAppointmentPatientIds() [SQL query]
    ├→ getUpcomingAppointmentPatientIds() [SQL query]
    ├→ getPendingCdsActions() [SQL query]
    └→ getClientEffort() [SQL query]
    ↓
Database
    ├→ obs table
    ├→ encounter table
    ├→ patient table
    ├→ person_name table
    └→ cds_actions_table
    ↓
cds.gsp Main Dashboard
    ├→ Display statistics
    ├→ Include cds/iitList.gsp
    ├→ Include cds/missedList.gsp
    ├→ Include cds/upcomingList.gsp
    └→ Include cds/actionsList.gsp
```

---

## Code Statistics

| Component | Type | Lines | Size |
|-----------|------|-------|------|
| CdsFragmentController.java | Java | 148 | 6,084 bytes |
| cds.gsp | GSP | 221 | ~8KB |
| iitList.gsp | GSP | 53 | 2,194 bytes |
| missedList.gsp | GSP | 53 | 2,192 bytes |
| upcomingList.gsp | GSP | 48 | 2,013 bytes |
| actionsList.gsp | GSP | 68 | 3,171 bytes |
| clientEffort.gsp | GSP | 31 | 1,375 bytes |
| **Total** | | **622** | **~25KB** |

---

## Integration Points

### Service Layer
- Implements `ClinicalDataSystemService` interface
- Calls methods for patient retrieval
- Handles exceptions and logging

### Data Access Layer
- `ClinicalDataSystemDao` provides SQL query implementations
- Uses Hibernate SQLQuery for native SQL
- Implements parameter binding for security

### Spring Framework
- Fragment controller annotated with `@Controller`
- Uses `@RequestParam` for query parameters
- Integrated with OpenMRS Fragment Framework

### OpenMRS UI Components
- Uses `ui.decorateWith()` for page decoration
- Uses `ui.pageLink()` for navigation
- Uses `ui.includeFragment()` for fragment composition

---

## Configuration

### Default Parameters
```
upcomingDays: 30
missedDays: 27
iitDays: 27
```

### URL Access
```
/cds/cds.page                                    # Main dashboard
/cds/cds.page?upcomingDays=7                     # Custom upcoming days
/cds/cds.page?missedDays=14&iitDays=14          # Multiple parameters
```

### Fragment Includes
```gsp
${ui.includeFragment("cds", "cds/iitList")}
${ui.includeFragment("cds", "cds/missedList")}
${ui.includeFragment("cds", "cds/upcomingList")}
${ui.includeFragment("cds", "cds/actionsList")}
${ui.includeFragment("cds", "cds/clientEffort", [patientId: 123])}
```

---

## Testing Checklist

- [ ] Dashboard loads without errors
- [ ] Statistics display correct counts
- [ ] All tabs are clickable and show correct data
- [ ] Patient links navigate to patient chart
- [ ] Empty state displays when no data
- [ ] Color indicators match status
- [ ] Responsive design works on mobile
- [ ] Controller methods handle null values
- [ ] Error messages display properly
- [ ] Fragment includes work correctly

---

## Next Steps

1. **Compile & Build**
   ```bash
   mvn clean install
   ```

2. **Deploy**
   - Copy JAR to OpenMRS modules directory
   - Restart OpenMRS

3. **Test**
   - Access `/cds/cds.page`
   - Verify all components load
   - Test with real patient data

4. **Future Enhancements**
   - Implement action assignment modal
   - Add AJAX status updates
   - Create REST endpoint for HQ sync
   - Add scheduled tasks for reporting

---

**Last Updated**: January 8, 2026
**Status**: ✅ READY FOR COMPILATION AND TESTING

