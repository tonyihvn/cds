# ✅ QUICK REFERENCE - EAC Form UUID Navigation

## What Was Done

Updated EAC form navigation to use UUIDs instead of numeric IDs.

---

## The Generated URL

```
/openmrs/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId=9e1e7811-e957-47af-b3db-c5c11fc1eaca&visitId=0&formUuid=d42c5cf6-8722-4f32-8767-ec8df0a40094&returnUrl=/openmrs/coreapps/clinicianfacing/patient.page?patientId=9e1e7811-e957-47af-b3db-c5c11fc1eaca
```

---

## Key Components

| Part | Value | Purpose |
|------|-------|---------|
| App | htmlformentryui | HTML Form Entry UI module |
| Page | enterHtmlFormWithStandardUi.page | Standard form entry page |
| patientId | {patient.uuid} | Patient UUID (not numeric ID) |
| visitId | 0 | No specific visit |
| formUuid | d42c5cf6-8722-4f32-8767-ec8df0a40094 | EAC form UUID |
| returnUrl | /openmrs/coreapps/clinicianfacing/patient.page?patientId={patient.uuid} | Return to patient dashboard |

---

## How to Test

```bash
# 1. Build
mvn clean package -DskipTests

# 2. Deploy
cp omod/target/cds-1.0.0-SNAPSHOT.omod /path/to/openmrs/modules/

# 3. Restart
systemctl restart openmrs

# 4. Access
http://localhost:8080/openmrs/cds/patientDashboard.page?patientId=9

# 5. Click EAC Button
# Should navigate to form with UUID parameters
```

---

## Customization

### Change EAC Form UUID

**File:** `PatientDashboardFragmentController.java`  
**Line:** ~95  
**Current:** `String formUuid = "d42c5cf6-8722-4f32-8767-ec8df0a40094";`  
**Change to:** Your EAC form UUID

---

## Key Files

1. **PatientDashboardFragmentController.java**
   - Lines 80-82: Get patient UUID
   - Lines 88-98: Construct EAC form URL
   - Lines 145-147: Add to model

2. **patientDashboard.gsp**
   - Lines 433-438: Store variables in JavaScript
   - Lines 459-466: openEACForm() function

---

## Console Output to Verify

**Server Logs:**
```
[CDS PatientDashboardFragment] Patient UUID: 9e1e7811-e957-47af-b3db-c5c11fc1eaca
[CDS PatientDashboardFragment] EAC Form URL: /openmrs/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?...
[CDS PatientDashboardFragment] Added: patientUuid = 9e1e7811-e957-47af-b3db-c5c11fc1eaca
[CDS PatientDashboardFragment] Added: eacFormUrl = /openmrs/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?...
```

**Browser Console:**
```
[CDS PatientDashboard] Patient UUID in script: 9e1e7811-e957-47af-b3db-c5c11fc1eaca
[CDS PatientDashboard] EAC Form URL: /openmrs/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?...
[CDS PatientDashboard] Navigating to EAC form: /openmrs/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?...
```

---

## Implementation Details

✅ **In Controller:**
- Get patient UUID: `patient.getUuid()`
- Construct full URL with form UUID and return URL
- Add to model: `model.addAttribute("eacFormUrl", eacFormUrl)`

✅ **In GSP:**
- Store URL in JavaScript variable: `var eacFormUrl = '${eacFormUrl ?: ''}'`
- Navigate in function: `window.location.href = eacFormUrl`

---

## Status

✅ **BUILD SUCCESS**  
✅ **READY TO DEPLOY**

---

**Date:** January 12, 2026

