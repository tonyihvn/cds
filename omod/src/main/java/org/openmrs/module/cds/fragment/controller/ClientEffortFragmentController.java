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
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Fragment controller for displaying client effort history for a specific patient
 */
@Controller
public class ClientEffortFragmentController {

	protected final Log log = LogFactory.getLog(this.getClass());

	public void controller(FragmentModel model, @FragmentParam("patientId") Integer patientId) {
		if (patientId == null || patientId <= 0) {
			model.addAttribute("clientEffortEntries", null);
			model.addAttribute("error", "Invalid patient ID");
			return;
		}

		try {
			ClinicalDataSystemService cdsService = Context.getService(ClinicalDataSystemService.class);
			model.addAttribute("clientEffortEntries", cdsService.getClientEffort(patientId));
			model.addAttribute("patientId", patientId);
		} catch (Exception e) {
			log.error("Error retrieving client effort for patient " + patientId, e);
			model.addAttribute("error", "Error retrieving client effort data");
		}
	}
}

