# PatientDashboardFragmentController - Implementation Guide ✅

## Overview

The `PatientDashboardFragmentController` is a Spring Fragment Controller that handles all the business logic for populating patient dashboard data. This controller retrieves comprehensive patient clinical information and passes it to the `patientDashboard.gsp` fragment through the FragmentModel.

## Architecture

### Files

1. **Controller**
   - Location: `omod/src/main/java/org/openmrs/module/cds/web/controller/PatientDashboardFragmentController.java`
   - Type: Spring Fragment Controller (no @RequestMapping needed, uses method name)

2. **Fragment View**
   - Location: `omod/src/main/webapp/fragments/patientDashboard.gsp`
   - Type: Groovy Server Page
   - Method: `patientDashboard` (matches controller method name)

## Controller Implementation

### Main Method: `patientDashboard`

```java
public void patientDashboard(FragmentModel model, @FragmentParam("patientId") Integer patientId)
```

**Parameters:**
- `FragmentModel model`: The model to populate with data
- `@FragmentParam("patientId")`: The patient ID passed from the view

**Data Populated:**

The controller retrieves and populates the following attributes:

1. **Patient Information**
   - `patient`: The Patient object
   - `patientId`: The numeric patient ID
   - `pepfarId`: PEPFAR ID (Identifier Type 4)
   - `givenName`: Patient's first name
   - `familyName`: Patient's last name

2. **Vital Load Data** (Map with keys)
   - `currentViralLoad`: Current VL count or "N/A"
   - `lastViralLoadDate`: Date of last VL test
   - `nextViralLoadDate`: Projected next VL due date (6 months from last)

3. **Regimen Data** (Map with keys)
   - `currentLine`: Current antiretroviral line
   - `currentRegimen`: Current ARV regimen
   - `lastPickUpDate`: Last regimen pickup date

4. **Appointment Information**
   - `nextAppointmentDate`: Formatted next appointment date or "Not scheduled"

5. **Clinical History**
   - `encounters`: List of recent encounters
   - `hasEACHistory`: Boolean indicating EAC (Enhanced Adherence Counselling) history

6. **System Data**
   - `users`: List of all system users (for assignment in action forms)

7. **Error Handling**
   - `error`: Error message if data retrieval failed (null if successful)

## Concept IDs Used

```java
private static final Integer PEPFAR_ID_TYPE = 4;           // Identifier type for PEPFAR ID
private static final Integer VIRAL_LOAD_CONCEPT = 856;     // Viral Load Result
private static final Integer CURRENT_LINE_CONCEPT = 165708;     // Current treatment line
private static final Integer CURRENT_REGIMEN_CONCEPT = 164506;  // Current regimen
private static final Integer APPOINTMENT_DATE_CONCEPT = 5096;   // Appointment date
private static final Integer EAC_SESSION_CONCEPT = 166097;      // EAC session
```

## Helper Methods

### 1. `getPEPFARId(Patient patient)`
- Retrieves PEPFAR identifier from patient
- Loops through patient identifiers looking for type ID = 4
- Returns "N/A" if not found

### 2. `getViralLoadData(Patient patient, ObsService obsService)`
- Retrieves latest viral load observation (Concept 856)
- Calculates next due date (6 months from last)
- Returns Map with viral load information

### 3. `getRegimenData(Patient patient, ObsService obsService)`
- Retrieves current regimen line (Concept 165708) and regimen (Concept 164506)
- Gets latest regimen pickup date
- Returns Map with regimen information

### 4. `getNextAppointmentDate(Patient patient, ObsService obsService)`
- Retrieves next future appointment (Concept 5096)
- Filters for dates after today
- Returns formatted date string or "Not scheduled"

### 5. `hasEACHistory(Patient patient, ObsService obsService)`
- Checks if patient has any EAC session records (Concept 166097)
- Returns boolean true/false

## Usage in GSP Fragment

### Include the Fragment

In your main page or dashboard, include the fragment:

```gsp
${ ui.includeFragment("cds", "patientDashboard", [patientId: patientId]) }
```

### Access the Data

In the `patientDashboard.gsp` file, simply use the populated attributes:

```gsp
<!-- Patient Name -->
<div>${givenName} ${familyName}</div>

<!-- PEPFAR ID -->
<div>${pepfarId}</div>

<!-- Age and Gender (safe navigation) -->
<div>${patient?.age ?: 'N/A'} years</div>
<div>${patient?.gender ?: 'N/A'}</div>

<!-- Viral Load -->
<div>${viralLoadData.currentViralLoad ?: 'N/A'} c/ml</div>

<!-- Regimen -->
<div>${regimenData.currentRegimen ?: 'N/A'}</div>

<!-- Appointment -->
<div>${nextAppointmentDate}</div>

<!-- Encounters -->
<% encounters.each { encounter -> %>
    <tr>
        <td>${encounter?.encounterType?.name ?: 'N/A'}</td>
        <td><fmt:formatDate value="${encounter?.encounterDatetime}" pattern="yyyy-MM-dd"/></td>
    </tr>
<% } %>

<!-- Users List -->
<% users.each { user -> %>
    <option value="${user.userId}">${user.personName}</option>
<% } %>
```

## Error Handling

The controller includes comprehensive error handling:

1. **Try-Catch Blocks**: All methods wrapped with exception handling
2. **Null Checks**: Safe navigation operators used throughout
3. **Logging**: Detailed INFO and WARN level logging for debugging
4. **Graceful Fallbacks**: Returns sensible defaults (e.g., "N/A") on errors
5. **Model.addAttribute("error", ...)**: Errors passed to view for display

### Example Error Scenario

If a patient doesn't have viral load data:
```java
// viralLoadData will contain:
{
  "currentViralLoad": "N/A",
  "lastViralLoadDate": null,
  "nextViralLoadDate": null
}
```

## Service Dependencies

The controller uses these OpenMRS services (autowired via Context):

1. **PatientService**: Load patient by ID
2. **EncounterService**: Get patient encounters
3. **ObsService**: Retrieve observations (VL, regimen, appointments)
4. **ConceptService**: Load concepts by ID
5. **UserService**: Get list of system users

## Fragment Configuration

The fragment is discovered automatically by the UI Framework:

- **Fragment ID**: `cds.patientDashboard` 
- **Controller**: `PatientDashboardFragmentController`
- **Method**: `patientDashboard`
- **View**: `patientDashboard.gsp`
- **Required Parameter**: `patientId`

## Logging

All methods include comprehensive logging with prefix `[CDS PatientDashboardFragment]`:

```
[CDS PatientDashboardFragment] patientDashboard() called with patientId: 123
[CDS PatientDashboardFragment] Patient loaded: 123
[CDS PatientDashboardFragment] PEPFAR ID: HV-123-456
[CDS PatientDashboardFragment] Viral load data retrieved
[CDS PatientDashboardFragment] Dashboard data populated successfully
```

## Benefits

✅ **Separation of Concerns**: Business logic separated from UI template  
✅ **Reusability**: Fragment can be included in multiple pages  
✅ **Maintainability**: Easy to update data retrieval logic  
✅ **Error Handling**: Comprehensive exception handling  
✅ **Performance**: Lazy loading of data only when needed  
✅ **Testing**: Controller methods can be unit tested independently  
✅ **Consistency**: All patient data retrieval in one place  

## Example Implementation

### Main Dashboard Page (CDS.gsp)
```gsp
<div class="dashboard-container">
    <!-- Include patient dashboard fragment -->
    ${ ui.includeFragment("cds", "patientDashboard", [patientId: selectedPatientId]) }
</div>
```

### Fragment View (patientDashboard.gsp)
```gsp
<!-- All data is available from FragmentModel -->
<div class="patient-info">
    <h2>${givenName} ${familyName}</h2>
    <p>PEPFAR: ${pepfarId}</p>
    <p>Next Appointment: ${nextAppointmentDate}</p>
</div>
```

## Build Status

✅ **BUILD SUCCESS**
- Compilation: All errors fixed
- OMOD Module: Created successfully
- All unit tests: Pass
- Ready for deployment

---

**Status**: COMPLETE ✅  
**Date**: January 12, 2026  
**Version**: CDS 1.0.0-SNAPSHOT

