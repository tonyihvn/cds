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
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Fragment controller for displaying pending CDS actions
 */
@Controller
public class ActionsListFragmentController {

	protected final Log log = LogFactory.getLog(this.getClass());

	public void controller(FragmentModel model) {
		try {
			ClinicalDataSystemService cdsService = Context.getService(ClinicalDataSystemService.class);
			List<CdsActionRecord> pendingActions = cdsService.getPendingCdsActions();

			model.addAttribute("pendingActions", pendingActions);
		} catch (Exception e) {
			log.error("Error loading pending actions list", e);
			model.addAttribute("error", "Error loading pending actions data");
		}
	}
}

