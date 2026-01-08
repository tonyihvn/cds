package org.openmrs.module.cds.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.PatientService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cds.api.ClinicalDataSystemService;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class PatientDashboardController {

    protected final Log log = LogFactory.getLog(this.getClass());

    /**
     * Get patient dashboard data including encounters, vital load, appointments, and regimen information
     */
    public void patientDashboard(FragmentModel model, @FragmentParam("patientId") Integer patientId) {
        log.info("[CDS PatientDashboard] patientDashboard() called with patientId: " + patientId);
        try {
            PatientService patientService = Context.getPatientService();
            EncounterService encounterService = Context.getEncounterService();
            ObsService obsService = Context.getObsService();
            ClinicalDataSystemService cdsService = Context.getService(ClinicalDataSystemService.class);

            Patient patient = patientService.getPatient(patientId);
            log.info("[CDS PatientDashboard] Patient retrieved: " + (patient != null ? patient.getPatientId() : "NULL"));

            // Get PEPFAR ID (Identifier Type 4)
            String pepfarId = getPEPFARId(patient);
            log.info("[CDS PatientDashboard] PEPFAR ID: " + pepfarId);

            // Get patient names
            String givenName = patient.getPersonName() != null ? patient.getPersonName().getGivenName() : "";
            String familyName = patient.getPersonName() != null ? patient.getPersonName().getFamilyName() : "";
            log.info("[CDS PatientDashboard] Patient Name: " + givenName + " " + familyName);

            // Get last encounters
            List<Encounter> encounters = encounterService.getEncounters(patient, null, null, null, null, null, null, null, null, false);
            log.info("[CDS PatientDashboard] Total encounters: " + encounters.size());

            // Get viral load data
            Map<String, Object> viralLoadData = getViralLoadData(patient, obsService);
            log.info("[CDS PatientDashboard] Viral load data retrieved");

            // Get regimen information
            Map<String, Object> regimenData = getRegimenData(patient, obsService);
            log.info("[CDS PatientDashboard] Regimen data retrieved");

            // Get next appointment
            String nextAppointmentDate = getNextAppointmentDate(patient, obsService);
            log.info("[CDS PatientDashboard] Next appointment: " + nextAppointmentDate);

            // Get pending actions
            List<Map<String, Object>> pendingActions = getPendingActions(patientId);
            log.info("[CDS PatientDashboard] Pending actions retrieved: " + pendingActions.size());

            // Check if EAC history exists
            boolean hasEACHistory = hasEACHistory(patient, obsService);
            log.info("[CDS PatientDashboard] EAC History exists: " + hasEACHistory);

            // Set model attributes
            model.addAttribute("pepfarId", pepfarId);
            model.addAttribute("givenName", givenName);
            model.addAttribute("familyName", familyName);
            model.addAttribute("patient", patient);
            model.addAttribute("encounters", encounters);
            model.addAttribute("viralLoadData", viralLoadData);
            model.addAttribute("regimenData", regimenData);
            model.addAttribute("nextAppointmentDate", nextAppointmentDate);
            model.addAttribute("pendingActions", pendingActions);
            model.addAttribute("hasEACHistory", hasEACHistory);
            model.addAttribute("users", Context.getUserService().getAllUsers());

            log.info("[CDS PatientDashboard] Dashboard data prepared successfully");

        } catch (Exception e) {
            log.error("[CDS PatientDashboard] ERROR: " + e.getMessage(), e);
            model.addAttribute("error", "Failed to load patient dashboard: " + e.getMessage());
        }
    }

    /**
     * Get PEPFAR ID from patient identifiers (identifier_type_id = 4)
     */
    private String getPEPFARId(Patient patient) {
        log.info("[CDS PatientDashboard] getPEPFARId() called for patientId: " + patient.getPatientId());
        try {
            if (patient.getIdentifiers() != null) {
                for (PatientIdentifier id : patient.getIdentifiers()) {
                    // Assuming PEPFAR ID has identifier_type_id = 4
                    if (id.getIdentifierType() != null && id.getIdentifierType().getId() == 4) {
                        log.info("[CDS PatientDashboard] PEPFAR ID found: " + id.getIdentifier());
                        return id.getIdentifier();
                    }
                }
            }
            log.warn("[CDS PatientDashboard] No PEPFAR ID found for patient: " + patient.getPatientId());
            return "N/A";
        } catch (Exception e) {
            log.error("[CDS PatientDashboard] ERROR in getPEPFARId(): " + e.getMessage(), e);
            return "Error";
        }
    }

    /**
     * Get viral load data from obs
     * Concept IDs from eacform.html:
     * - 856: Viral Load Result
     * - 166296: Viral Load Date
     */
    private Map<String, Object> getViralLoadData(Patient patient, ObsService obsService) {
        log.info("[CDS PatientDashboard] getViralLoadData() called for patientId: " + patient.getPatientId());
        Map<String, Object> data = new HashMap<>();
        try {
            List<Obs> viralLoadObs = obsService.getObservationsByPersonAndConcept(patient,
                Context.getConceptService().getConcept(856));

            if (viralLoadObs != null && !viralLoadObs.isEmpty()) {
                // Get the latest viral load
                Obs latestViralLoad = viralLoadObs.get(viralLoadObs.size() - 1);
                data.put("currentViralLoad", latestViralLoad.getValueNumeric());
                data.put("lastViralLoadDate", latestViralLoad.getObsDatetime());

                // Calculate next viral load date (6 months from last)
                Calendar cal = Calendar.getInstance();
                cal.setTime(latestViralLoad.getObsDatetime());
                cal.add(Calendar.MONTH, 6);
                data.put("nextViralLoadDate", cal.getTime());

                log.info("[CDS PatientDashboard] Viral load data: " + data.get("currentViralLoad") + ", Date: " + data.get("lastViralLoadDate"));
            } else {
                log.warn("[CDS PatientDashboard] No viral load data found");
                data.put("currentViralLoad", "N/A");
                data.put("lastViralLoadDate", null);
                data.put("nextViralLoadDate", null);
            }
        } catch (Exception e) {
            log.error("[CDS PatientDashboard] ERROR in getViralLoadData(): " + e.getMessage(), e);
            data.put("error", e.getMessage());
        }
        return data;
    }

    /**
     * Get regimen information
     * Concept IDs from eacform.html:
     * - 165708: Current Line
     * - 164506: Current Regimen
     */
    private Map<String, Object> getRegimenData(Patient patient, ObsService obsService) {
        log.info("[CDS PatientDashboard] getRegimenData() called for patientId: " + patient.getPatientId());
        Map<String, Object> data = new HashMap<>();
        try {
            // Get current regimen line
            List<Obs> lineObs = obsService.getObservationsByPersonAndConcept(patient,
                Context.getConceptService().getConcept(165708));
            if (lineObs != null && !lineObs.isEmpty()) {
                Obs latestLine = lineObs.get(lineObs.size() - 1);
                data.put("currentLine", latestLine.getValueCoded() != null ? latestLine.getValueCoded().getName() : "N/A");
                log.info("[CDS PatientDashboard] Current line: " + data.get("currentLine"));
            }

            // Get current regimen
            List<Obs> regimenObs = obsService.getObservationsByPersonAndConcept(patient,
                Context.getConceptService().getConcept(164506));
            if (regimenObs != null && !regimenObs.isEmpty()) {
                Obs latestRegimen = regimenObs.get(regimenObs.size() - 1);
                data.put("currentRegimen", latestRegimen.getValueCoded() != null ? latestRegimen.getValueCoded().getName() : "N/A");
                data.put("lastPickUpDate", latestRegimen.getObsDatetime());
                log.info("[CDS PatientDashboard] Current regimen: " + data.get("currentRegimen"));
            }

            if (!data.containsKey("currentLine")) data.put("currentLine", "N/A");
            if (!data.containsKey("currentRegimen")) data.put("currentRegimen", "N/A");
            if (!data.containsKey("lastPickUpDate")) data.put("lastPickUpDate", null);

        } catch (Exception e) {
            log.error("[CDS PatientDashboard] ERROR in getRegimenData(): " + e.getMessage(), e);
            data.put("error", e.getMessage());
        }
        return data;
    }

    /**
     * Get next appointment date
     * Concept ID 5096: Appointment Date
     */
    private String getNextAppointmentDate(Patient patient, ObsService obsService) {
        log.info("[CDS PatientDashboard] getNextAppointmentDate() called for patientId: " + patient.getPatientId());
        try {
            List<Obs> appointmentObs = obsService.getObservationsByPersonAndConcept(patient,
                Context.getConceptService().getConcept(5096));

            if (appointmentObs != null && !appointmentObs.isEmpty()) {
                for (Obs obs : appointmentObs) {
                    if (obs.getValueDatetime() != null && obs.getValueDatetime().after(new Date())) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        log.info("[CDS PatientDashboard] Next appointment: " + sdf.format(obs.getValueDatetime()));
                        return sdf.format(obs.getValueDatetime());
                    }
                }
            }
            log.warn("[CDS PatientDashboard] No future appointments found");
            return "Not scheduled";
        } catch (Exception e) {
            log.error("[CDS PatientDashboard] ERROR in getNextAppointmentDate(): " + e.getMessage(), e);
            return "Error";
        }
    }

    /**
     * Get pending actions from the CDS actions table
     */
    private List<Map<String, Object>> getPendingActions(Integer patientId) {
        log.info("[CDS PatientDashboard] getPendingActions() called for patientId: " + patientId);
        List<Map<String, Object>> actions = new ArrayList<>();
        try {
            // This would query from the cds_actions_table
            // For now, returning empty list
            log.info("[CDS PatientDashboard] Pending actions retrieved: " + actions.size());
        } catch (Exception e) {
            log.error("[CDS PatientDashboard] ERROR in getPendingActions(): " + e.getMessage(), e);
        }
        return actions;
    }

    /**
     * Check if patient has EAC history
     * Concept ID 166097: Enhanced Adherence Counselling Session
     */
    private boolean hasEACHistory(Patient patient, ObsService obsService) {
        log.info("[CDS PatientDashboard] hasEACHistory() called for patientId: " + patient.getPatientId());
        try {
            List<Obs> eacObs = obsService.getObservationsByPersonAndConcept(patient,
                Context.getConceptService().getConcept(166097));

            boolean hasHistory = eacObs != null && !eacObs.isEmpty();
            log.info("[CDS PatientDashboard] EAC History exists: " + hasHistory);
            return hasHistory;
        } catch (Exception e) {
            log.error("[CDS PatientDashboard] ERROR in hasEACHistory(): " + e.getMessage(), e);
            return false;
        }
    }
}

