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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.cds.api.ClinicalDataSystemService;
import org.openmrs.module.cds.api.dto.CdsActionRecord;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fragment controller for the CDS Dashboard. Provides data for displaying: - Upcoming appointments
 * - Missed appointments - IIT (Interruption in Treatment) patients - Pending actions - Client
 * effort tracking
 */
@Controller
public class CdsFragmentController {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private static final int DEFAULT_UPCOMING_DAYS = 30;
	
	private static final int DEFAULT_MISSED_DAYS = 27;
	
	private static final int DEFAULT_IIT_LOOKBACK_DAYS = 27;
	
	/**
	 * Main dashboard fragment providing statistics and patient lists
	 */
	public void controller(FragmentModel model, @RequestParam(value = "upcomingDays", defaultValue = "" + DEFAULT_UPCOMING_DAYS) int upcomingDays,
	        @RequestParam(value = "missedDays", defaultValue = "" + DEFAULT_MISSED_DAYS) int missedDays,
	        @RequestParam(value = "iitDays", defaultValue = "" + DEFAULT_IIT_LOOKBACK_DAYS) int iitDays) {

		ClinicalDataSystemService cdsService = Context.getService(ClinicalDataSystemService.class);

		// Get patient IDs for each category
		List<Integer> upcomingPatientIds = cdsService.getUpcomingAppointmentPatientIds(upcomingDays);
		List<Integer> missedPatientIds = cdsService.getMissedAppointmentPatientIds(missedDays);
		List<Integer> iitPatientIds = cdsService.getIITPatientIds(iitDays);
		List<CdsActionRecord> pendingActions = cdsService.getPendingCdsActions();

		// Create statistics DTO
		Map<String, Object> stats = new HashMap<>();
		stats.put("upcomingCount", upcomingPatientIds.size());
		stats.put("missedCount", missedPatientIds.size());
		stats.put("iitCount", iitPatientIds.size());
		stats.put("pendingActionsCount", pendingActions.size());
		stats.put("totalActivePatients", upcomingPatientIds.size() + missedPatientIds.size() + iitPatientIds.size());

		model.addAttribute("stats", stats);
		model.addAttribute("upcomingPatientIds", upcomingPatientIds);
		model.addAttribute("missedPatientIds", missedPatientIds);
		model.addAttribute("iitPatientIds", iitPatientIds);
		model.addAttribute("pendingActions", pendingActions);
		model.addAttribute("upcomingDays", upcomingDays);
		model.addAttribute("missedDays", missedDays);
		model.addAttribute("iitDays", iitDays);
	}
	
	/**
	 * Fragment for displaying the IIT (Verge of IIT) patient list
	 */
	public void iitList(FragmentModel model,
	        @RequestParam(value = "iitDays", defaultValue = "" + DEFAULT_IIT_LOOKBACK_DAYS) int iitDays) {
		ClinicalDataSystemService cdsService = Context.getService(ClinicalDataSystemService.class);
		List<Integer> iitPatientIds = cdsService.getIITPatientIds(iitDays);
		
		model.addAttribute("iitPatientIds", iitPatientIds);
		model.addAttribute("lookbackDays", iitDays);
	}
	
	/**
	 * Fragment for displaying missed appointments
	 */
	public void missedList(FragmentModel model,
	        @RequestParam(value = "missedDays", defaultValue = "" + DEFAULT_MISSED_DAYS) int missedDays) {
		ClinicalDataSystemService cdsService = Context.getService(ClinicalDataSystemService.class);
		List<Integer> missedPatientIds = cdsService.getMissedAppointmentPatientIds(missedDays);
		
		model.addAttribute("missedPatientIds", missedPatientIds);
		model.addAttribute("lookbackDays", missedDays);
	}
	
	/**
	 * Fragment for displaying upcoming appointments
	 */
	public void upcomingList(FragmentModel model, @RequestParam(value = "upcomingDays", defaultValue = ""
	        + DEFAULT_UPCOMING_DAYS) int upcomingDays) {
		ClinicalDataSystemService cdsService = Context.getService(ClinicalDataSystemService.class);
		List<Integer> upcomingPatientIds = cdsService.getUpcomingAppointmentPatientIds(upcomingDays);
		
		model.addAttribute("upcomingPatientIds", upcomingPatientIds);
		model.addAttribute("lookbackDays", upcomingDays);
	}
	
	/**
	 * Fragment for displaying pending actions (internal task tracking)
	 */
	public void actionsList(FragmentModel model) {
		ClinicalDataSystemService cdsService = Context.getService(ClinicalDataSystemService.class);
		List<CdsActionRecord> pendingActions = cdsService.getPendingCdsActions();
		
		model.addAttribute("pendingActions", pendingActions);
	}
	
	/**
	 * Fragment for displaying client effort history for a specific patient
	 */
	public void clientEffort(FragmentModel model, @FragmentParam("patientId") Integer patientId) {
		if (patientId == null || patientId <= 0) {
			model.addAttribute("clientEffortEntries", null);
			model.addAttribute("error", "Invalid patient ID");
			return;
		}
		
		ClinicalDataSystemService cdsService = Context.getService(ClinicalDataSystemService.class);
		
		try {
			model.addAttribute("clientEffortEntries", cdsService.getClientEffort(patientId));
			model.addAttribute("patientId", patientId);
		}
		catch (Exception e) {
			log.error("Error retrieving client effort for patient " + patientId, e);
			model.addAttribute("error", "Error retrieving client effort data");
		}
	}
}
