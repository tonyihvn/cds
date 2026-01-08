package org.openmrs.module.cds.api;

import org.openmrs.module.cds.Item;
import org.openmrs.module.cds.api.dto.CdsActionRecord;
import org.openmrs.module.cds.api.dto.ClientEffortEntry;
import org.openmrs.api.OpenmrsService;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Extended ClinicalDataSystemService with additional methods for actions and reports
 */
public interface IClinicalDataSystemActionService extends OpenmrsService {

    /**
     * Save a CDS action record
     * @param action the action to save
     * @return the saved action
     */
    CdsActionRecord saveCdsAction(CdsActionRecord action);

    /**
     * Get pending actions for a patient
     * @param patientId the patient ID
     * @return list of pending actions
     */
    List<CdsActionRecord> getPendingActions(Integer patientId);

    /**
     * Update action status
     * @param actionId the action ID
     * @param status the new status
     */
    void updateActionStatus(Integer actionId, String status);

    /**
     * Get all pending actions system-wide
     * @return list of all pending actions
     */
    List<CdsActionRecord> getAllPendingActions();

    /**
     * Mark action as completed
     * @param actionId the action ID
     */
    void completeAction(Integer actionId);

    /**
     * Get patient's treatment history including encounters and observations
     * @param patientId the patient ID
     * @return list of treatment history records
     */
    List<Map<String, Object>> getPatientTreatmentHistory(Integer patientId);

    /**
     * Send patient report to external API
     * @param patientId the patient ID
     * @param apiEndpoint the API endpoint URL
     * @param reportData the report data to send
     * @return true if sent successfully
     */
    boolean sendPatientReport(Integer patientId, String apiEndpoint, Map<String, Object> reportData);
}

