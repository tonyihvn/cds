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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.stereotype.Controller;

/**
 * Fragment Controller for Patient Dashboard
 * Handles retrieval of comprehensive patient clinical data and populates the fragment model
 */
@Controller
public class CdsPatientDashboardFragmentController {

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
     * @param patientId The patient ID to load data for
     */
    public void patientDashboard(FragmentModel model, @FragmentParam("patientId") Integer patientId) {
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
                encounters = encounterService.getEncounters(patient, null, null, null, null, null, null, null, null,
                        false);
                System.out.println("[CDS PatientDashboardFragment] Total encounters: " + encounters.size());
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

            // Get users list
            List<org.openmrs.User> users = userService.getAllUsers();
            System.out.println("[CDS PatientDashboardFragment] Users retrieved: " + users.size());

            // Check if EAC history exists
            boolean hasEACHistory = hasEACHistory(patient, obsService);
            System.out.println("[CDS PatientDashboardFragment] EAC History exists: " + hasEACHistory);

            // Get pending actions (empty list for now, can be implemented later)
            List<Map<String, Object>> pendingActions = new ArrayList<>();
            System.out.println("[CDS PatientDashboardFragment] Pending actions retrieved: " + pendingActions.size());

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

            System.out.println("[CDS PatientDashboardFragment] ========== MODEL POPULATED ==========");

        } catch (Exception e) {
            System.out.println("[CDS PatientDashboardFragment] ======== ERROR IN MAIN METHOD ========");
            System.out.println("[CDS PatientDashboardFragment] ERROR: " + e.getMessage());
            System.out.println("[CDS PatientDashboardFragment] Exception Type: " + e.getClass().getName());
            System.out.println("[CDS PatientDashboardFragment] Stack Trace:");
            e.printStackTrace();
            System.out.println("[CDS PatientDashboardFragment] ====================================");
            model.addAttribute("error", "Failed to load patient dashboard: " + e.getMessage());
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
            data.put("lastViralLoadDate", null);
            data.put("nextViralLoadDate", null);

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
                    data.put("lastViralLoadDate", latestViralLoad.getObsDatetime());

                    // Calculate next viral load date (6 months from last)
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(latestViralLoad.getObsDatetime());
                    cal.add(Calendar.MONTH, 6);
                    data.put("nextViralLoadDate", cal.getTime());

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
            data.put("lastPickUpDate", null);

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
                        data.put("lastPickUpDate", latestRegimen.getObsDatetime());
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
}
