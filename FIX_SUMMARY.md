## Fix for: groovy.lang.MissingPropertyException: No such property: stats

### Problem
The GSP template (`cds.gsp`) was trying to access a `stats` object with properties like `iitCount`, `missedCount`, `upcomingCount`, and `pendingActionsCount`, but the controller was not providing this object to the view, causing a `MissingPropertyException`.

### Root Cause
The `ClinicalDataSystemController.java` was missing:
1. A `@ModelAttribute("stats")` method to populate the stats object
2. The necessary imports for `ClinicalDataSystemService` and the stats DTO
3. An autowired reference to `ClinicalDataSystemService`

### Solution Implemented

#### 1. Created DashboardStats DTO
**File**: `api/src/main/java/org/openmrs/module/cds/api/dto/DashboardStats.java`

A new Data Transfer Object class that holds the dashboard statistics:
- `iitCount` - Count of patients on verge of IIT (Interruption in Treatment)
- `missedCount` - Count of missed appointments
- `upcomingCount` - Count of upcoming appointments
- `pendingActionsCount` - Count of pending actions

#### 2. Updated ClinicalDataSystemController
**File**: `omod/src/main/java/org/openmrs/module/cds/web/controller/ClinicalDataSystemController.java`

Made the following changes:

**Added imports:**
```java
import org.openmrs.module.cds.api.ClinicalDataSystemService;
import org.openmrs.module.cds.api.dto.CdsActionRecord;
import org.openmrs.module.cds.api.dto.DashboardStats;
```

**Added autowired service:**
```java
@Autowired
ClinicalDataSystemService cdsService;
```

**Added ModelAttribute method:**
```java
@ModelAttribute("stats")
protected DashboardStats getDashboardStats() {
    try {
        // Get counts from service
        int iitCount = cdsService.getIITPatientIds(90).size();
        int missedCount = cdsService.getMissedAppointmentPatientIds(30).size();
        int upcomingCount = cdsService.getUpcomingAppointmentPatientIds(30).size();
        List<CdsActionRecord> pendingActions = cdsService.getPendingCdsActions();
        int pendingActionsCount = pendingActions != null ? pendingActions.size() : 0;
        
        return new DashboardStats(iitCount, missedCount, upcomingCount, pendingActionsCount);
    } catch (Exception e) {
        log.error("Error fetching dashboard statistics", e);
        // Return empty stats if error occurs
        return new DashboardStats(0, 0, 0, 0);
    }
}
```

### How It Works
1. When the controller handles a request to `/module/cds/cds.form`, Spring automatically calls all `@ModelAttribute` methods
2. The `getDashboardStats()` method queries the `ClinicalDataSystemService` to get patient counts
3. It creates a `DashboardStats` object with these counts and adds it to the model with the name "stats"
4. The GSP template can now access the stats object as `${stats.iitCount}`, `${stats.missedCount}`, etc.
5. Error handling ensures the application doesn't crash if the service is unavailable - it returns zeros instead

### Default Parameters Used
- **IIT Lookback Days**: 90 days
- **Missed Appointment Days**: 30 days
- **Upcoming Appointment Days**: 30 days

These can be customized by adjusting the method call parameters in `getDashboardStats()`.

### Testing
The fix can be tested by:
1. Compiling the project: `mvn clean install -DskipTests`
2. Deploying the OMOD file to an OpenMRS instance
3. Navigating to the CDS dashboard - it should now display statistics without throwing a `MissingPropertyException`

