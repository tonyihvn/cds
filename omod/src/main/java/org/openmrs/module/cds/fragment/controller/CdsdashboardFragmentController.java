/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.cds.fragment.controller;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.api.*;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Fragment Controller for Patient Dashboard
 * Handles retrieval of comprehensive patient clinical data and populates the fragment model
 */
@Controller
public class CdsdashboardFragmentController {

    protected final Log log = LogFactory.getLog(this.getClass());

    private static final Integer PEPFAR_ID_TYPE = 4;
    private static final Integer VIRAL_LOAD_CONCEPT = 856;
    private static final Integer CURRENT_LINE_CONCEPT = 165708;
    private static final Integer CURRENT_REGIMEN_CONCEPT = 164506;
    private static final Integer APPOINTMENT_DATE_CONCEPT = 5096;
    private static final Integer EAC_SESSION_CONCEPT = 166097;

    /**
     * Main fragment method to populate patient dashboard data
     * Called when the patientDashboard fragment is included
     *
     * @param model FragmentModel to populate with patient data
     * @param patientId The patient ID to load data for (from URL parameter)
     */
    public void get(FragmentModel model, @RequestParam(value = "patientId", required = false) Integer patientId) {
        System.out.println("[CDS PatientDashboardFragment] patientDashboard() called with patientId: " + patientId);

        try {
            // Get services
            PatientService patientService = Context.getPatientService();
            EncounterService encounterService = Context.getEncounterService();
            ObsService obsService = Context.getObsService();
            UserService userService = Context.getUserService();

            // Load patient
            Patient patient = null;
            if (patientId != null && patientId > 0) {
                patient = patientService.getPatient(patientId);
                System.out.println("[CDS PatientDashboardFragment] Patient loaded: " + (patient != null ? patient.getPatientId() : "NULL"));
            }

            // Get PEPFAR ID
            String pepfarId = getPEPFARId(patient);
            System.out.println("[CDS PatientDashboardFragment] PEPFAR ID: " + pepfarId);

            // Get patient UUID
            String patientUuid = patient != null ? patient.getUuid() : "";
            System.out.println("[CDS PatientDashboardFragment] Patient UUID: " + patientUuid);

            // Get patient names
            String givenName = patient != null && patient.getPersonName() != null
                    ? patient.getPersonName().getGivenName()
                    : "";
            String familyName = patient != null && patient.getPersonName() != null
                    ? patient.getPersonName().getFamilyName()
                    : "";
            System.out.println("[CDS PatientDashboardFragment] Patient Name: " + givenName + " " + familyName);

            // Construct EAC form URL with UUIDs
            String eacFormUrl = "";
            if (patient != null && !patientUuid.isEmpty()) {
                String formUuid = "d42c5cf6-8722-4f32-8767-ec8df0a40094"; // EAC form UUID
                String returnUrl = "/openmrs/coreapps/clinicianfacing/patient.page?patientId=" + patientUuid;
                eacFormUrl = "/openmrs/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId=" + patientUuid
                        + "&visitId=0&formUuid=" + formUuid + "&returnUrl=" + returnUrl;
                System.out.println("[CDS PatientDashboardFragment] EAC Form URL: " + eacFormUrl);
            }

            // Get encounters
            List<Encounter> encounters = new ArrayList<>();
            if (patient != null) {
                try {
                    // { changed code }
                    // Avoid calling an overloaded getEncounters signature that may not exist at runtime.
                    // Use a safe, signature-independent method and filter results as needed.
                    encounters = encounterService.getEncountersByPatient(patient);
                    if (encounters == null) {
                        encounters = new ArrayList<>();
                    } else {
                        // Keep only non-voided encounters (mirror original false for includeVoided)
                        encounters = encounters.stream()
                                .filter(e -> e != null && !e.isVoided())
                                .collect(Collectors.toList());
                    }
                    System.out.println("[CDS PatientDashboardFragment] Total encounters: " + encounters.size());
                } catch (NoSuchMethodError nsme) {
                    System.out.println("[CDS PatientDashboardFragment] EncounterService signature missing, falling back to empty list");
                    encounters = new ArrayList<>();
                } catch (Exception ex) {
                    System.out.println("[CDS PatientDashboardFragment] Warning: Error retrieving encounters - " + ex.getMessage());
                    encounters = new ArrayList<>();
                }
            }

            // Get viral load data
            Map<String, Object> viralLoadData = getViralLoadData(patient, obsService);
            System.out.println("[CDS PatientDashboardFragment] Viral load data retrieved");

            // Get regimen information
            Map<String, Object> regimenData = getRegimenData(patient, obsService);
            System.out.println("[CDS PatientDashboardFragment] Regimen data retrieved");

            // Get next appointment
            String nextAppointmentDate = getNextAppointmentDate(patient, obsService);
            System.out.println("[CDS PatientDashboardFragment] Next appointment: " + nextAppointmentDate);

            // Get users list (with permission error handling)
            List<org.openmrs.User> users = new ArrayList<>();
            try {
                users = userService.getAllUsers();
                System.out.println("[CDS PatientDashboardFragment] Users retrieved: " + users.size());
            } catch (Exception e) {
                System.out.println("[CDS PatientDashboardFragment] Warning: Cannot retrieve users - " + e.getMessage());
                System.out.println("[CDS PatientDashboardFragment] This may be due to insufficient privileges or service unavailability");
                // Continue with empty users list - don't fail the entire dashboard
                users = new ArrayList<>();
            }

            // Check if EAC history exists
            boolean hasEACHistory = hasEACHistory(patient, obsService);
            System.out.println("[CDS PatientDashboardFragment] EAC History exists: " + hasEACHistory);

            // Get pending actions (empty list for now, can be implemented later)
            List<Map<String, Object>> pendingActions = new ArrayList<>();
            System.out.println("[CDS PatientDashboardFragment] Pending actions retrieved: " + pendingActions.size());

            // Get documented actions from database
            List<Map<String, Object>> documentedActions = getDocumentedActions(patient);
            System.out.println("[CDS PatientDashboardFragment] Documented actions retrieved: " + documentedActions.size());

            // Get tracking efforts from database
            Map<String, Object> trackingEffortsData = getTrackingEffortsTable(patient);
            List<Map<String, Object>> trackingEfforts = (List<Map<String, Object>>) trackingEffortsData.get("rows");
            if (trackingEfforts == null) {
                trackingEfforts = new ArrayList<>();
            }
            System.out.println("[CDS PatientDashboardFragment] Tracking efforts retrieved: " + trackingEfforts.size());

            // Populate fragment model
            System.out.println("[CDS PatientDashboardFragment] ========== POPULATING MODEL ==========");
            model.addAttribute("patient", patient);
            System.out.println("[CDS PatientDashboardFragment] Added: patient = " + (patient != null ? patient.getPatientId() : "null"));

            model.addAttribute("patientId", patientId);
            System.out.println("[CDS PatientDashboardFragment] Added: patientId = " + patientId);

            model.addAttribute("patientUuid", patientUuid);
            System.out.println("[CDS PatientDashboardFragment] Added: patientUuid = " + patientUuid);

            model.addAttribute("eacFormUrl", eacFormUrl);
            System.out.println("[CDS PatientDashboardFragment] Added: eacFormUrl = " + eacFormUrl);

            model.addAttribute("pepfarId", pepfarId);
            System.out.println("[CDS PatientDashboardFragment] Added: pepfarId = " + pepfarId);

            model.addAttribute("givenName", givenName);
            System.out.println("[CDS PatientDashboardFragment] Added: givenName = " + givenName);

            model.addAttribute("familyName", familyName);
            System.out.println("[CDS PatientDashboardFragment] Added: familyName = " + familyName);

            model.addAttribute("encounters", encounters);
            System.out.println("[CDS PatientDashboardFragment] Added: encounters = " + encounters.size() + " items");

            model.addAttribute("viralLoadData", viralLoadData);
            System.out.println("[CDS PatientDashboardFragment] Added: viralLoadData = " + viralLoadData);

            model.addAttribute("regimenData", regimenData);
            System.out.println("[CDS PatientDashboardFragment] Added: regimenData = " + regimenData);

            model.addAttribute("nextAppointmentDate", nextAppointmentDate);
            System.out.println("[CDS PatientDashboardFragment] Added: nextAppointmentDate = " + nextAppointmentDate);

            model.addAttribute("users", users);
            System.out.println("[CDS PatientDashboardFragment] Added: users = " + users.size() + " items");

            model.addAttribute("hasEACHistory", hasEACHistory);
            System.out.println("[CDS PatientDashboardFragment] Added: hasEACHistory = " + hasEACHistory);

            model.addAttribute("pendingActions", pendingActions);
            System.out.println("[CDS PatientDashboardFragment] Added: pendingActions = " + pendingActions.size() + " items");

            model.addAttribute("documentedActions", documentedActions);
            System.out.println("[CDS PatientDashboardFragment] Added: documentedActions = " + documentedActions.size() + " items");

            model.addAttribute("trackingEfforts", trackingEfforts);
            System.out.println("[CDS PatientDashboardFragment] Added: trackingEfforts = " + trackingEfforts.size() + " items");

            System.out.println("[CDS PatientDashboardFragment] ========== MODEL POPULATED ==========");

        } catch (Exception e) {
            System.out.println("[CDS PatientDashboardFragment] ======== ERROR IN MAIN METHOD ========");
            System.out.println("[CDS PatientDashboardFragment] ERROR: " + e.getMessage());
            System.out.println("[CDS PatientDashboardFragment] Exception Type: " + e.getClass().getName());
            System.out.println("[CDS PatientDashboardFragment] Stack Trace:");
            e.printStackTrace();
            System.out.println("[CDS PatientDashboardFragment] ====================================");

            // Set default attributes so GSP won't fail with missing properties
            model.addAttribute("error", "Failed to load patient dashboard: " + e.getMessage());
            model.addAttribute("patient", null);
            model.addAttribute("patientId", null);
            model.addAttribute("patientUuid", "");
            model.addAttribute("eacFormUrl", "");
            model.addAttribute("pepfarId", "N/A");
            model.addAttribute("givenName", "");
            model.addAttribute("familyName", "");
            model.addAttribute("encounters", new ArrayList<>());
            model.addAttribute("viralLoadData", new HashMap<>());
            model.addAttribute("regimenData", new HashMap<>());
            model.addAttribute("nextAppointmentDate", "N/A");
            model.addAttribute("users", new ArrayList<>());
            model.addAttribute("hasEACHistory", false);
            model.addAttribute("pendingActions", new ArrayList<>());
            model.addAttribute("documentedActions", new ArrayList<>());
            model.addAttribute("trackingEfforts", new ArrayList<>());
        }
    }

    /**
     * Get PEPFAR ID from patient identifiers (identifier_type_id = 4)
     */
    private String getPEPFARId(Patient patient) {
        System.out.println("[CDS PatientDashboardFragment] getPEPFARId() called");
        try {
            if (patient != null && patient.getIdentifiers() != null) {
                for (PatientIdentifier id : patient.getIdentifiers()) {
                    if (id.getIdentifierType() != null && id.getIdentifierType().getId().equals(PEPFAR_ID_TYPE)) {
                        System.out.println("[CDS PatientDashboardFragment] PEPFAR ID found: " + id.getIdentifier());
                        return id.getIdentifier();
                    }
                }
            }
            System.out.println("[CDS PatientDashboardFragment] No PEPFAR ID found");
            return "N/A";
        } catch (Exception e) {
            System.out.println("[CDS PatientDashboardFragment] ERROR in getPEPFARId(): " + e.getMessage());
            e.printStackTrace();
            return "Error";
        }
    }

    /**
     * Get viral load data from obs
     * Concept IDs:
     * - 856: Viral Load Result
     * - 166296: Viral Load Date
     */
    private Map<String, Object> getViralLoadData(Patient patient, ObsService obsService) {
        System.out.println("[CDS PatientDashboardFragment] getViralLoadData() called");
        Map<String, Object> data = new HashMap<>();

        try {
            data.put("currentViralLoad", "N/A");
            data.put("lastViralLoadDate", "N/A");
            data.put("nextViralLoadDate", "N/A");

            if (patient == null) {
                System.out.println("[CDS PatientDashboardFragment] Patient is null, returning default viral load data");
                return data;
            }

            Concept viralLoadConcept = Context.getConceptService().getConcept(VIRAL_LOAD_CONCEPT);
            if (viralLoadConcept == null) {
                System.out.println("[CDS PatientDashboardFragment] ERROR: Viral load concept not found (ID: " + VIRAL_LOAD_CONCEPT + ")");
                return data;
            }

            List<Obs> viralLoadObs = obsService.getObservationsByPersonAndConcept(patient, viralLoadConcept);
            System.out.println("[CDS PatientDashboardFragment] Found " + (viralLoadObs != null ? viralLoadObs.size() : 0) + " viral load observations");

            if (viralLoadObs != null && !viralLoadObs.isEmpty()) {
                // Get the latest viral load
                Obs latestViralLoad = viralLoadObs.get(viralLoadObs.size() - 1);
                if (latestViralLoad.getValueNumeric() != null) {
                    data.put("currentViralLoad", latestViralLoad.getValueNumeric());

                    // Format last VL date as string
                    if (latestViralLoad.getObsDatetime() != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String lastVLDateStr = sdf.format(latestViralLoad.getObsDatetime());
                        data.put("lastViralLoadDate", lastVLDateStr);
                        System.out.println("[CDS PatientDashboardFragment] Last VL Date: " + lastVLDateStr);
                    }

                    // Calculate and format next viral load date (6 months from last)
                    if (latestViralLoad.getObsDatetime() != null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(latestViralLoad.getObsDatetime());
                        cal.add(Calendar.MONTH, 6);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String nextVLDateStr = sdf.format(cal.getTime());
                        data.put("nextViralLoadDate", nextVLDateStr);
                        System.out.println("[CDS PatientDashboardFragment] Next VL Date: " + nextVLDateStr);
                    }

                    System.out.println("[CDS PatientDashboardFragment] Viral load: " + data.get("currentViralLoad"));
                }
            }
        } catch (Exception e) {
            System.out.println("[CDS PatientDashboardFragment] ERROR in getViralLoadData(): " + e.getMessage());
            e.printStackTrace();
            data.put("error", e.getMessage());
        }
        return data;
    }

    /**
     * Get regimen information
     * Concept IDs:
     * - 165708: Current Line
     * - 164506: Current Regimen
     */
    private Map<String, Object> getRegimenData(Patient patient, ObsService obsService) {
        System.out.println("[CDS PatientDashboardFragment] getRegimenData() called");
        Map<String, Object> data = new HashMap<>();

        try {
            data.put("currentLine", "N/A");
            data.put("currentRegimen", "N/A");
            data.put("lastPickUpDate", "N/A");

            if (patient == null) {
                System.out.println("[CDS PatientDashboardFragment] Patient is null, returning default regimen data");
                return data;
            }

            // Get current regimen line
            Concept lineConcept = Context.getConceptService().getConcept(CURRENT_LINE_CONCEPT);
            if (lineConcept != null) {
                List<Obs> lineObs = obsService.getObservationsByPersonAndConcept(patient, lineConcept);
                System.out.println("[CDS PatientDashboardFragment] Found " + (lineObs != null ? lineObs.size() : 0) + " regimen line observations");
                if (lineObs != null && !lineObs.isEmpty()) {
                    Obs latestLine = lineObs.get(lineObs.size() - 1);
                    if (latestLine.getValueCoded() != null) {
                        data.put("currentLine", latestLine.getValueCoded().getName().getName());
                        System.out.println("[CDS PatientDashboardFragment] Current line: " + data.get("currentLine"));
                    }
                }
            } else {
                System.out.println("[CDS PatientDashboardFragment] ERROR: Current line concept not found (ID: " + CURRENT_LINE_CONCEPT + ")");
            }

            // Get current regimen
            Concept regimenConcept = Context.getConceptService().getConcept(CURRENT_REGIMEN_CONCEPT);
            if (regimenConcept != null) {
                List<Obs> regimenObs = obsService.getObservationsByPersonAndConcept(patient, regimenConcept);
                System.out.println("[CDS PatientDashboardFragment] Found " + (regimenObs != null ? regimenObs.size() : 0) + " regimen observations");
                if (regimenObs != null && !regimenObs.isEmpty()) {
                    Obs latestRegimen = regimenObs.get(regimenObs.size() - 1);
                    if (latestRegimen.getValueCoded() != null) {
                        data.put("currentRegimen", latestRegimen.getValueCoded().getName().getName());

                        // Format last pick up date as string
                        if (latestRegimen.getObsDatetime() != null) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            String lastPickUpStr = sdf.format(latestRegimen.getObsDatetime());
                            data.put("lastPickUpDate", lastPickUpStr);
                            System.out.println("[CDS PatientDashboardFragment] Last Pick Up Date: " + lastPickUpStr);
                        }

                        System.out.println("[CDS PatientDashboardFragment] Current regimen: " + data.get("currentRegimen"));
                    }
                }
            } else {
                System.out.println("[CDS PatientDashboardFragment] ERROR: Current regimen concept not found (ID: " + CURRENT_REGIMEN_CONCEPT + ")");
            }
        } catch (Exception e) {
            System.out.println("[CDS PatientDashboardFragment] ERROR in getRegimenData(): " + e.getMessage());
            e.printStackTrace();
            data.put("error", e.getMessage());
        }
        return data;
    }

    /**
     * Get next appointment date
     * Concept ID 5096: Appointment Date
     */
    private String getNextAppointmentDate(Patient patient, ObsService obsService) {
        System.out.println("[CDS PatientDashboardFragment] getNextAppointmentDate() called");

        try {
            if (patient == null) {
                System.out.println("[CDS PatientDashboardFragment] Patient is null, returning 'Not scheduled'");
                return "Not scheduled";
            }

            Concept appointmentConcept = Context.getConceptService().getConcept(APPOINTMENT_DATE_CONCEPT);
            if (appointmentConcept == null) {
                System.out.println("[CDS PatientDashboardFragment] ERROR: Appointment concept not found (ID: " + APPOINTMENT_DATE_CONCEPT + ")");
                return "Not scheduled";
            }

            List<Obs> appointmentObs = obsService.getObservationsByPersonAndConcept(patient, appointmentConcept);
            System.out.println("[CDS PatientDashboardFragment] Found " + (appointmentObs != null ? appointmentObs.size() : 0) + " appointment observations");

            if (appointmentObs != null && !appointmentObs.isEmpty()) {
                for (Obs obs : appointmentObs) {
                    if (obs.getValueDatetime() != null && obs.getValueDatetime().after(new Date())) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String appointmentDate = sdf.format(obs.getValueDatetime());
                        System.out.println("[CDS PatientDashboardFragment] Next appointment: " + appointmentDate);
                        return appointmentDate;
                    }
                }
            }
            System.out.println("[CDS PatientDashboardFragment] No future appointments found");
            return "Not scheduled";
        } catch (Exception e) {
            System.out.println("[CDS PatientDashboardFragment] ERROR in getNextAppointmentDate(): " + e.getMessage());
            e.printStackTrace();
            return "Error";
        }
    }

    /**
     * Check if patient has EAC history
     * Concept ID 166097: Enhanced Adherence Counselling Session
     */
    private boolean hasEACHistory(Patient patient, ObsService obsService) {
        System.out.println("[CDS PatientDashboardFragment] hasEACHistory() called");

        try {
            if (patient == null) {
                System.out.println("[CDS PatientDashboardFragment] Patient is null, returning false for EAC history");
                return false;
            }

            Concept eacConcept = Context.getConceptService().getConcept(EAC_SESSION_CONCEPT);
            if (eacConcept == null) {
                System.out.println("[CDS PatientDashboardFragment] ERROR: EAC concept not found (ID: " + EAC_SESSION_CONCEPT + ")");
                return false;
            }

            List<Obs> eacObs = obsService.getObservationsByPersonAndConcept(patient, eacConcept);

            boolean hasHistory = eacObs != null && !eacObs.isEmpty();
            System.out.println("[CDS PatientDashboardFragment] EAC History exists: " + hasHistory);
            return hasHistory;
        } catch (Exception e) {
            System.out.println("[CDS PatientDashboardFragment] ERROR in hasEACHistory(): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get documented actions for the patient
     * This is a placeholder implementation, replace with real data retrieval logic
     */
    private List<Map<String, Object>> getDocumentedActions(Patient patient) {
        System.out.println("[CDS PatientDashboardFragment] getDocumentedActions() called");
        List<Map<String, Object>> actions = new ArrayList<>();

        try {
            // TODO: Replace with real implementation
            if (patient != null) {
                Map<String, Object> action = new HashMap<>();
                action.put("date", new Date());
                action.put("description", "Documented action for patient " + patient.getPatientId());
                actions.add(action);
            }
        } catch (Exception e) {
            System.out.println("[CDS PatientDashboardFragment] ERROR in getDocumentedActions(): " + e.getMessage());
            e.printStackTrace();
        }
        return actions;
    }

    /**
     * Get tracking efforts for the patient
     * This is a placeholder implementation, replace with real data retrieval logic
     */
    private Map<String, Object> getTrackingEffortsTable(Patient patient) {

        System.out.println("[CDS PatientDashboardFragment] getTrackingEffortsTable() called - returning MOCK data");

        Map<String, Object> result = new HashMap<>();
        List<String> headers = new ArrayList<>();
        List<Map<String, Object>> rows = new ArrayList<>();

        try {
            // Mock headers
            headers.add("Contact Date");
            headers.add("Contact Method");
            headers.add("Outcome");
            headers.add("Contacted By");
            headers.add("Notes");

            // Prepare a date formatter
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            // Mock row 1 - today
            Map<String, Object> row1 = new LinkedHashMap<>();
            row1.put("Contact Date", sdf.format(new Date()));
            row1.put("Contact Method", "Phone");
            row1.put("Outcome", "Reached");
            row1.put("Contacted By", "Nurse A");
            row1.put("Notes", "Patient confirmed next appointment");
            rows.add(row1);

            // Mock row 2 - 7 days ago
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -7);
            Map<String, Object> row2 = new LinkedHashMap<>();
            row2.put("Contact Date", sdf.format(cal.getTime()));
            row2.put("Contact Method", "Home Visit");
            row2.put("Outcome", "Not at home");
            row2.put("Contacted By", "Community Worker B");
            row2.put("Notes", "Left message with neighbor");
            rows.add(row2);

            // Mock row 3 - 30 days ago
            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -30);
            Map<String, Object> row3 = new LinkedHashMap<>();
            row3.put("Contact Date", sdf.format(cal.getTime()));
            row3.put("Contact Method", "SMS");
            row3.put("Outcome", "Reached");
            row3.put("Contacted By", "Automated System");
            row3.put("Notes", "Reminder sent for refill");
            rows.add(row3);

            // If patient is null, add a mock informational row
            if (patient == null) {
                Map<String, Object> info = new LinkedHashMap<>();
                info.put("Contact Date", "");
                info.put("Contact Method", "");
                info.put("Outcome", "No patient selected");
                info.put("Contacted By", "");
                info.put("Notes", "Mock data displayed because no patient was provided");
                rows.clear();
                rows.add(info);
            }

        } catch (Exception e) {
            System.out.println("[CDS PatientDashboardFragment] ERROR while building mock tracking efforts: " + e.getMessage());
            e.printStackTrace();
        }

        result.put("headers", headers);
        result.put("rows", rows);
        return result;
    }

    private String getObsValueAsString(Obs obs) {

        if (obs.getValueCoded() != null) {
            return obs.getValueCoded()
                    .getName(Context.getLocale())
                    .getName();
        }
        if (obs.getValueText() != null) {
            return obs.getValueText();
        }
        if (obs.getValueNumeric() != null) {
            return obs.getValueNumeric().toString();
        }
        if (obs.getValueDatetime() != null) {
            return obs.getValueDatetime().toString();
        }
        if (obs.getValueBoolean() != null) {
            return obs.getValueBoolean() ? "Yes" : "No";
        }
        return "";
    }


}
