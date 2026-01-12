# 🎯 DEBUGGING OUTPUT REFERENCE GUIDE

## Console Output Map

When you access the patient dashboard, you'll see output like this:

```
████████████████████████████████████████████████████████████████
█  ENTRY POINT
████████████████████████████████████████████████████████████████
[CDS PatientDashboardFragment] patientDashboard() called with patientId: 271

████████████████████████████████████████████████████████████████
█  SERVICE RETRIEVAL & PATIENT LOADING
████████████████████████████████████████████████████████████████
[CDS PatientDashboardFragment] Patient loaded: 271

████████████████████████████████████████████████████████████████
█  DATA RETRIEVAL PHASE
████████████████████████████████████████████████████████████████
[CDS PatientDashboardFragment] getPEPFARId() called
[CDS PatientDashboardFragment] PEPFAR ID found: HV-123-456

[CDS PatientDashboardFragment] getViralLoadData() called
[CDS PatientDashboardFragment] Found 5 viral load observations
[CDS PatientDashboardFragment] Viral load: 850

[CDS PatientDashboardFragment] getRegimenData() called
[CDS PatientDashboardFragment] Found 3 regimen line observations
[CDS PatientDashboardFragment] Current line: First Line
[CDS PatientDashboardFragment] Found 2 regimen observations
[CDS PatientDashboardFragment] Current regimen: TDF+3TC+EFV

[CDS PatientDashboardFragment] getNextAppointmentDate() called
[CDS PatientDashboardFragment] Found 4 appointment observations
[CDS PatientDashboardFragment] Next appointment: 2026-02-15

[CDS PatientDashboardFragment] Users retrieved: 15
[CDS PatientDashboardFragment] EAC History exists: true
[CDS PatientDashboardFragment] Pending actions retrieved: 0

████████████████████████████████████████████████████████████████
█  MODEL POPULATION PHASE
████████████████████████████████████████████████████████████████
[CDS PatientDashboardFragment] ========== POPULATING MODEL ==========
[CDS PatientDashboardFragment] Added: patient = 271
[CDS PatientDashboardFragment] Added: patientId = 271          ← CHECK THIS!
[CDS PatientDashboardFragment] Added: pepfarId = HV-123-456
[CDS PatientDashboardFragment] Added: givenName = John
[CDS PatientDashboardFragment] Added: familyName = Doe
[CDS PatientDashboardFragment] Added: encounters = 5 items
[CDS PatientDashboardFragment] Added: viralLoadData = {...}
[CDS PatientDashboardFragment] Added: regimenData = {...}
[CDS PatientDashboardFragment] Added: nextAppointmentDate = 2026-02-15
[CDS PatientDashboardFragment] Added: users = 15 items
[CDS PatientDashboardFragment] Added: hasEACHistory = true
[CDS PatientDashboardFragment] Added: pendingActions = 0 items
[CDS PatientDashboardFragment] ========== MODEL POPULATED ==========

█  SUCCESS! All attributes added. Fragment should work!
```

---

## Error Scenario Output

If something goes wrong, you'll see:

```
████████████████████████████████████████████████████████████████
█  ERROR SCENARIO
████████████████████████████████████████████████████████████████
[CDS PatientDashboardFragment] patientDashboard() called with patientId: 271
[CDS PatientDashboardFragment] Patient loaded: 271
[CDS PatientDashboardFragment] getViralLoadData() called
[CDS PatientDashboardFragment] ERROR: Viral load concept not found (ID: 856)
[CDS PatientDashboardFragment] ========== POPULATING MODEL ==========
...
[CDS PatientDashboardFragment] ========== MODEL POPULATED ==========

█  Note: Still added all attributes with defaults where needed
```

---

## Critical Failure Scenario

```
████████████████████████████████████████████████████████████████
█  CRITICAL ERROR
████████████████████████████████████████████████████████████████
[CDS PatientDashboardFragment] patientDashboard() called with patientId: 271
[CDS PatientDashboardFragment] Patient loaded: NULL

[CDS PatientDashboardFragment] ======== ERROR IN MAIN METHOD ========
[CDS PatientDashboardFragment] ERROR: NullPointerException: Cannot read field "name" because "obs.getValueCoded()" is null
[CDS PatientDashboardFragment] Exception Type: java.lang.NullPointerException
[CDS PatientDashboardFragment] Stack Trace:
java.lang.NullPointerException: Cannot read field "name" because "obs.getValueCoded()" is null
	at org.openmrs.module.cds.web.controller.PatientDashboardFragmentController.getRegimenData(PatientDashboardFragmentController.java:243)
	at org.openmrs.module.cds.web.controller.PatientDashboardFragmentController.patientDashboard(PatientDashboardFragmentController.java:98)
	...
[CDS PatientDashboardFragment] ====================================

█  ERROR! Problem found: Line 243 in getRegimenData()
█  Issue: getValueCoded() returning null when expected to have a value
```

---

## Quick Diagnostic Guide

### Problem: `MissingPropertyException: No such property: patientId`

**Check the logs for:**
```
[CDS PatientDashboardFragment] Added: patientId = 271
```

- ✅ **If present** → patientId IS in model, check GSP syntax
- ❌ **If missing** → patientId NOT added to model, check controller logic

---

### Problem: Patient data not showing

**Check for:**
```
[CDS PatientDashboardFragment] Patient loaded: NULL
```

- ✅ **Shows: 271** → Patient loaded successfully
- ❌ **Shows: NULL** → Patient ID doesn't exist in system

---

### Problem: Concept not found errors

**Check logs for:**
```
[CDS PatientDashboardFragment] ERROR: Viral load concept not found (ID: 856)
```

- **What it means**: Concept ID 856 doesn't exist in OpenMRS
- **Fix**: Update concept ID to one that exists in your system

---

### Problem: Full crash/exception

**Check for:**
```
[CDS PatientDashboardFragment] ======== ERROR IN MAIN METHOD ========
[CDS PatientDashboardFragment] Exception Type: java.lang.NullPointerException
```

- **Stack Trace shows**: Exact file and line number of problem
- **Fix**: Look at that line number and handle the null value

---

## Log Searching Cheat Sheet

```bash
# Find all CDS messages
grep "[CDS PatientDashboardFragment]" catalina.out

# Find just errors
grep "ERROR" catalina.out | grep "PatientDashboard"

# Find MODEL POPULATED status
grep "MODEL POPULATED\|POPULATING MODEL" catalina.out

# Find patientId being added
grep "Added: patientId" catalina.out

# Find all exceptions
grep "Exception Type\|ERROR IN MAIN" catalina.out

# Find specific concept errors
grep "concept not found" catalina.out

# Follow logs in real-time
tail -f catalina.out | grep "PatientDashboardFragment"
```

---

## Attributes Being Logged

| # | Attribute | What It Is |
|---|-----------|-----------|
| 1 | `patient` | Patient object (null or patient ID) |
| 2 | `patientId` | Integer patient ID ⭐ **KEY** |
| 3 | `pepfarId` | PEPFAR identifier string |
| 4 | `givenName` | Patient first name |
| 5 | `familyName` | Patient last name |
| 6 | `encounters` | List of patient encounters |
| 7 | `viralLoadData` | Map with VL info |
| 8 | `regimenData` | Map with regimen info |
| 9 | `nextAppointmentDate` | String with next appointment |
| 10 | `users` | List of system users |
| 11 | `hasEACHistory` | Boolean for EAC existence |
| 12 | `pendingActions` | List of pending actions |

---

## Expected Success Pattern

```
✅ patientDashboard() called with patientId: 271
✅ Patient loaded: 271
✅ [All data retrieval methods called and logged]
✅ ========== POPULATING MODEL ==========
✅ Added: patientId = 271                          ← CRITICAL
✅ Added: [10 more attributes]
✅ ========== MODEL POPULATED ==========
```

If you see this pattern → **Fragment should render successfully!**

---

## Expected Error Pattern

```
✅ patientDashboard() called with patientId: 271
✅ Patient loaded: 271
⚠️ [Some data retrieval logged with error]
✅ ========== POPULATING MODEL ==========
✅ Added: patientId = 271                          ← STILL ADDED
✅ Added: [10 more attributes with defaults]
✅ ========== MODEL POPULATED ==========
```

If you see this pattern → **Fragment should still render with defaults!**

---

## Expected Failure Pattern

```
✅ patientDashboard() called with patientId: 271
❌ ======== ERROR IN MAIN METHOD ========
❌ ERROR: <exception message>
❌ Exception Type: <exception class>
❌ Stack Trace: <full trace>
```

If you see this pattern → **Model NOT populated, exception thrown!**

---

## Next Actions Based on Output

| Output | Action |
|--------|--------|
| SUCCESS pattern | ✅ Fragment should work |
| ERROR with defaults pattern | ⚠️ Works but with defaults |
| FAILURE pattern | ❌ Need to fix exception |
| Missing patientId | ❌ Check if being added to model |
| Concept not found | ⚠️ Update concept ID |
| NULL patient | ❌ Verify patient ID exists |

---

**Print this guide and keep it handy when checking logs!**

---

**Status**: ✅ **DEBUGGING FULLY IMPLEMENTED**
**Ready to Deploy**: YES ✅
**Date**: January 12, 2026

