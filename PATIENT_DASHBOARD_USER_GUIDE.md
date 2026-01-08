# 📊 Patient CDS Dashboard - Quick Start Guide

## Overview

The Patient CDS Dashboard provides clinicians with a comprehensive view of each patient's clinical data, treatment status, and pending actions all in one place.

---

## Accessing the Dashboard

### Method 1: From Patient Lists
1. Go to **CDS Dashboard** in the main menu
2. Navigate to any of these tabs:
   - **Verge of IIT** (Red)
   - **Missed Appointments** (Yellow)
   - **Upcoming Appointments** (Green)
3. Click the **"📊 Dashboard"** button next to any patient
4. The patient's detailed dashboard will open

### Method 2: Direct URL
```
http://your-openmrs/cds/patientDashboard?patientId=123
```

---

## Dashboard Sections

### 1️⃣ **Patient Header**
```
┌─────────────────────────────────────┐
│ PEPFAR ID: PE-12345                 │
│ Name: John Doe                      │
│ Age: 35 years  Gender: Male         │
└─────────────────────────────────────┘
```
- Displays patient's unique PEPFAR identifier
- Shows patient's full name (given + family)
- Age and gender information

### 2️⃣ **Viral Load Status**
```
┌──────────────────────────────────────┐
│ Current VL: 45 c/ml ✓ SUPPRESSED    │
│ Last VL Date: 2025-07-15            │
│ Next VL Due: 2026-01-15             │
└──────────────────────────────────────┘
```
- Shows current viral load (status indicator)
  - ✓ Green: Suppressed (< 1000)
  - ✗ Red: Unsuppressed (≥ 1000)
- Last viral load collection date
- Next viral load due date (6 months from last)

### 3️⃣ **Current Treatment**
```
┌──────────────────────────────────────┐
│ Regimen Line: First Line            │
│ Current Regimen: TDF/3TC/EFV        │
│ Last Pick Up: 2026-01-05            │
│ Next Appointment: 2026-02-05        │
└──────────────────────────────────────┘
```
- ARV regimen line (1st, 2nd, 3rd)
- Current antiretroviral regimen
- Last pharmacy pick-up date
- Scheduled next appointment

### 4️⃣ **Encounter History Table**
```
┌──────────────┬──────────────┬──────────┬──────────────┐
│ Date         │ Enc. Type    │ Provider │ Location     │
├──────────────┼──────────────┼──────────┼──────────────┤
│ 2026-01-05   │ ART Clinic   │ Dr. Smith│ Main Clinic  │
│ 2025-12-15   │ Lab Test     │ Lab Tech │ Lab          │
│ 2025-11-20   │ Counselling  │ John D.  │ Main Clinic  │
└──────────────┴──────────────┴──────────┴──────────────┘
```
- Recent patient encounters
- Encounter type and provider information
- Visit location

### 5️⃣ **Clinical Actions Panel**
```
┌─────────────────────────────────────┐
│ 📋 Enhanced Adherence Counselling   │ (if no history)
│ ➕ Document New Action              │
│ 📤 Send Report to API               │
└─────────────────────────────────────┘
```

#### EAC (Enhanced Adherence Counselling)
- **Shows if**: Patient has NO EAC session recorded
- **Click to**: Open EAC form (form_id 69)
- **Shows "✓ EAC Done"** if patient already has EAC records

#### Document Action
- Click to open action documentation modal
- Record clinical decisions and next steps
- Assign responsibility to team members

#### Send Report
- Click to open API integration modal
- Send patient summary to external systems
- Include recommendations and findings

### 6️⃣ **Pending Actions Table**
```
┌──────────────────┬──────────────┬─────────┬────────────┐
│ Action           │ Assigned To  │ Status  │ Date       │
├──────────────────┼──────────────┼─────────┼────────────┤
│ Intensified AE   │ Mary Johnson │ PENDING │ 2026-01-08 │
│ Conduct CD4 test │ Lab Tech     │ PENDING │ 2026-01-07 │
└──────────────────┴──────────────┴─────────┴────────────┘
```
- All pending action items for the patient
- Who the action is assigned to
- Action status (PENDING/IN_PROGRESS/COMPLETED)
- When the action was created

---

## Modals & Forms

### Document New Action Modal
```
┌─────────────────────────────────────┐
│ Document New Action                 │
├─────────────────────────────────────┤
│ Action Description:                 │
│ [Long description of the action] ✏️ │
│                                     │
│ Assign To:                          │
│ [Select User from Dropdown] ▼       │
│                                     │
│ Next Step:                          │
│ [Specific next step action] ✏️      │
│                                     │
│ Notes:                              │
│ [Additional notes] ✏️               │
│                                     │
│ [ Save Action ]                     │
└─────────────────────────────────────┘
```

**Steps to Use:**
1. Click "Document New Action" button
2. Fill in all required fields
3. Select which team member to assign the action to
4. Add any additional notes
5. Click "Save Action"
6. Action will be recorded in the system

### Send Report to API Modal
```
┌─────────────────────────────────────┐
│ Send Report to API                  │
├─────────────────────────────────────┤
│ API Endpoint:                       │
│ [www.example.com/api] ✏️            │
│                                     │
│ Report Notes:                       │
│ [Clinical recommendations] ✏️       │
│                                     │
│ Include Attachments:                │
│ ☐ Viral Load & Regimen Data         │
│                                     │
│ [ Send Report ]                     │
└─────────────────────────────────────┘
```

**Steps to Use:**
1. Click "Send Report to API" button
2. Enter the API endpoint URL
3. Add clinical recommendations/notes
4. Optionally check to include viral load and regimen data
5. Click "Send Report"
6. Report will be sent to external system

---

## Using the EAC Form

### When to Open
- Click **"📋 EAC Form"** button when patient has missed appointments
- Only available if patient has NO previous EAC records

### What It Records (from eacform.html)
- Enhanced Adherence Counselling session type (1st, 2nd, 3rd)
- Missed pharmacy pick-ups assessment
- Adherence level (Good, Fair, Poor)
- **Barriers to Adherence**:
  - Forgot
  - Side Effects
  - Transport
  - Stigma
  - Drug stock out
  - And 11 others...
- **Interventions**:
  - Education
  - Counselling (Individual/Group)
  - Peer Support
  - Treatment Buddy
  - And 5 others...
- **Tools Used**:
  - Pill Box
  - Calendar
  - SMS Reminders
  - Alarms
  - And 5 others...
- **Next Steps**: Repeat VL, Extend counselling, Regimen change
- **Comments**: Document barriers and planned interventions

---

## Key Concept IDs Used

For reference, the dashboard pulls data from these concepts:

| Concept ID | Data Point |
|-----------|-----------|
| 856 | Viral Load |
| 164506 | Current Regimen |
| 165708 | Regimen Line |
| 5096 | Appointment Date |
| 166097 | EAC Session |
| 165457 | Barriers |
| 165501 | Interventions |

---

## Tips & Best Practices

✅ **Check viral load status first** - Know if patient is suppressed or not  
✅ **Review encounter history** - Understand patient's visit pattern  
✅ **Document actions** - Never rely on memory; record all decisions  
✅ **Assign to right person** - Make sure actions are assigned to who will do them  
✅ **Use EAC early** - Start enhanced adherence counselling when issues appear  
✅ **Send reports** - Keep external systems updated with patient status  

---

## Troubleshooting

### **PEPFAR ID Not Showing**
- Check identifier type is set to 4 in OpenMRS
- Ensure patient has this identifier entered

### **Viral Load Shows "N/A"**
- Patient may not have viral load test recorded
- Check obs table for concept 856
- May need to enter lab results

### **EAC Button Always Shows**
- Verify concept 166097 is being saved correctly
- Check encounter and obs tables for EAC records

### **Actions Not Saving**
- Check cds_actions_table exists in database
- Verify user has necessary permissions
- Check browser console for JavaScript errors

---

## Keyboard Shortcuts

| Action | Shortcut |
|--------|----------|
| Save Action (in modal) | `Ctrl + Enter` |
| Close Modal | `Esc` |
| Go Back | `Alt + ←` |

---

## Data Sent to External API

When you click "Send Report to API", the following data is sent:

```json
{
  "patientId": 123,
  "pepfarId": "PE-12345",
  "patientName": "John Doe",
  "viralLoad": 45,
  "viralLoadStatus": "SUPPRESSED",
  "regimen": "TDF/3TC/EFV",
  "regimenLine": "First Line",
  "nextAppointment": "2026-02-05",
  "lastVLDate": "2025-07-15",
  "nextVLDue": "2026-01-15",
  "notes": "Clinical recommendations here",
  "timestamp": "2026-01-08T14:15:23Z"
}
```

---

## Support

For issues or questions:
1. Check the troubleshooting section above
2. Review browser console for errors
3. Contact system administrator
4. Check OpenMRS logs for detailed errors

---

**Last Updated**: January 8, 2026  
**Version**: 1.0

