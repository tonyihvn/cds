# Quick Reference: Fix for MissingPropertyException

## The Error
```
groovy.lang.MissingPropertyException: No such property: stats for class: SimpleTemplateScript11
```

## The Problem
The GSP template was accessing `${stats.iitCount}` but the Spring controller wasn't providing the `stats` object.

## The Solution
Added a `@ModelAttribute("stats")` method to the controller that computes and provides statistics to the view.

## Files Changed

### 1. omod/src/main/java/org/openmrs/module/cds/web/controller/ClinicalDataSystemController.java

**Added 3 imports:**
```java
import org.openmrs.module.cds.api.ClinicalDataSystemService;
import org.openmrs.module.cds.api.dto.CdsActionRecord;
import org.openmrs.module.cds.api.dto.DashboardStats;
```

**Added 1 field:**
```java
@Autowired
ClinicalDataSystemService cdsService;
```

**Added 1 method:**
```java
@ModelAttribute("stats")
protected DashboardStats getDashboardStats() {
    try {
        int iitCount = cdsService.getIITPatientIds(90).size();
        int missedCount = cdsService.getMissedAppointmentPatientIds(30).size();
        int upcomingCount = cdsService.getUpcomingAppointmentPatientIds(30).size();
        List<CdsActionRecord> pendingActions = cdsService.getPendingCdsActions();
        int pendingActionsCount = pendingActions != null ? pendingActions.size() : 0;
        
        return new DashboardStats(iitCount, missedCount, upcomingCount, pendingActionsCount);
    } catch (Exception e) {
        log.error("Error fetching dashboard statistics", e);
        return new DashboardStats(0, 0, 0, 0);
    }
}
```

### 2. api/src/main/java/org/openmrs/module/cds/api/dto/DashboardStats.java (NEW FILE)

Simple DTO with 4 fields:
- `iitCount` (int)
- `missedCount` (int)
- `upcomingCount` (int)
- `pendingActionsCount` (int)

Plus getters, setters, and constructors.

## Build & Deploy

```bash
# Build
mvn clean install -DskipTests

# Deploy the OMOD file
# Location: omod/target/cds-1.0.0-SNAPSHOT.omod
```

## Verify

1. Navigate to CDS Dashboard
2. Should display statistics without errors
3. Check logs for "MissingPropertyException" - should be gone

## That's It!

The error is now resolved. The GSP template can access the stats object with all its properties.

