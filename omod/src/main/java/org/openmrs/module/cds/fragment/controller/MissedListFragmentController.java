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
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Fragment controller for displaying the missed appointments patient list
 */
@Controller
public class MissedListFragmentController {

	protected final Log log = LogFactory.getLog(this.getClass());

	private static final int DEFAULT_MISSED_DAYS = 30;

	public void controller(FragmentModel model,
	        @RequestParam(value = "missedDays", defaultValue = "" + DEFAULT_MISSED_DAYS) int missedDays) {
		try {
			ClinicalDataSystemService cdsService = Context.getService(ClinicalDataSystemService.class);
			List<Integer> missedPatientIds = cdsService.getMissedAppointmentPatientIds(missedDays);

			model.addAttribute("missedPatientIds", missedPatientIds);
			model.addAttribute("lookbackDays", missedDays);
		} catch (Exception e) {
			log.error("Error loading missed appointments list", e);
			model.addAttribute("error", "Error loading missed appointments data");
		}
	}
}

