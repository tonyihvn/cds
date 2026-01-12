# Quick Reference - PatientDashboardFragmentController ✅

## TL;DR

You now have a clean separation between:
- **Controller Logic**: `PatientDashboardFragmentController.java` - Handles all data retrieval
- **Fragment View**: `patientDashboard.gsp` - Handles UI rendering

## How to Use

### Include the Fragment

```gsp
${ ui.includeFragment("cds", "patientDashboard", [patientId: 123]) }
```

### Available Data in Fragment

All of these are automatically available in your `patientDashboard.gsp`:

```groovy
${patient}                      // Patient object
${patientId}                    // Patient ID (integer)
${pepfarId}                     // PEPFAR ID string
${givenName}                    // First name
${familyName}                   // Last name

${viralLoadData.currentViralLoad}         // Current VL or "N/A"
${viralLoadData.lastViralLoadDate}        // Date object or null
${viralLoadData.nextViralLoadDate}        // Date object or null

${regimenData.currentLine}      // Regimen line name
${regimenData.currentRegimen}   // Regimen name
${regimenData.lastPickUpDate}   // Date object or null

${nextAppointmentDate}          // String: formatted date or "Not scheduled"
${encounters}                   // List of Encounter objects
${users}                        // List of User objects
${hasEACHistory}                // Boolean: true/false
${error}                        // Error message or null
```

## What the Controller Does

```java
// Gets called automatically when fragment is included
public void patientDashboard(FragmentModel model, @FragmentParam("patientId") Integer patientId) {
    // 1. Load patient from database
    // 2. Get PEPFAR ID from patient identifiers
    // 3. Retrieve viral load observations
    // 4. Retrieve current regimen information
    // 5. Get next appointment date
    // 6. Fetch recent encounters
    // 7. Get list of all users
    // 8. Check for EAC history
    // 9. Add all data to model
    // 10. Return (data automatically available in GSP)
}
```

## Common Patterns

### Display Patient Name with Safety
```gsp
<h1>${givenName} ${familyName}</h1>
```

### Display Viral Load with Formatting
```gsp
<% if (viralLoadData.currentViralLoad && viralLoadData.currentViralLoad != 'N/A') { %>
    <% if (viralLoadData.currentViralLoad > 1000) { %>
        <div class="alert alert-danger">
            Unsuppressed: ${viralLoadData.currentViralLoad} c/ml
        </div>
    <% } else { %>
        <div class="alert alert-success">
            Suppressed: ${viralLoadData.currentViralLoad} c/ml
        </div>
    <% } %>
<% } %>
```

### Display Appointments
```gsp
<p>Next Appointment: ${nextAppointmentDate}</p>
```

### Loop Through Encounters
```gsp
<% encounters.each { encounter -> %>
    <tr>
        <td><fmt:formatDate value="${encounter?.encounterDatetime}" pattern="yyyy-MM-dd HH:mm"/></td>
        <td>${encounter?.encounterType?.name ?: 'N/A'}</td>
        <td>${encounter?.provider?.personName ?: 'N/A'}</td>
    </tr>
<% } %>
```

### Populate User Dropdown
```gsp
<select>
    <option value="">-- Select User --</option>
    <% users.each { user -> %>
        <option value="${user.userId}">${user.personName}</option>
    <% } %>
</select>
```

## Safety Notes

All data is already null-safe! The controller handles:
- Null patients
- Missing identifiers
- Missing observations
- Missing concepts
- Missing users

Use safe navigation in GSP if needed:
```gsp
${patient?.age ?: 'N/A'}           <!-- Safe with fallback -->
${encounter?.encounterType?.name}   <!-- Deep property access -->
```

## Files Changed

| File | Change | Status |
|------|--------|--------|
| `PatientDashboardFragmentController.java` | NEW | ✅ Created |
| `patientDashboard.gsp` | Simplified | ✅ Updated |

## Build Status

```
[INFO] BUILD SUCCESS
[INFO] Clinical Data System API ........................... SUCCESS
[INFO] Clinical Data System OMOD .......................... SUCCESS
```

## Testing the Fragment

### In Your Main Page/JSP:
```gsp
<%@ page import="org.openmrs.ui.framework.UiUtils" %>
${ ui.includeFragment("cds", "patientDashboard", [patientId: 50]) }
```

### Result:
- Fragment renders with all patient data populated
- No initialization code needed in GSP
- All data retrieved from database automatically
- Error handling built-in

## Debugging

Add this to see all available data:

```gsp
<!-- DEBUG: Show all model attributes -->
<% model.each { k, v -> %>
    <div>${k}: ${v}</div>
<% } %>
```

Or in logs, look for:
```
[CDS PatientDashboardFragment] patientDashboard() called with patientId: 123
[CDS PatientDashboardFragment] Dashboard data populated successfully
```

## Next Steps

1. Deploy the OMOD module to OpenMRS
2. Include the fragment in your main dashboard page
3. Verify patient data displays correctly
4. Check logs for any issues

---

**Ready to Use**: ✅  
**Build Status**: SUCCESS ✅  
**Date**: January 12, 2026

