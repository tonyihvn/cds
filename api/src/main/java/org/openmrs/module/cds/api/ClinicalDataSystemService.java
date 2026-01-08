/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.cds.api;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.cds.ClinicalDataSystemConfig;
import org.openmrs.module.cds.Item;
import org.openmrs.module.cds.api.dto.CdsActionRecord;
import org.openmrs.module.cds.api.dto.ClientEffortEntry;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * The main service of this module, which is exposed for other modules. See
 * moduleApplicationContext.xml on how it is wired up.
 */
public interface ClinicalDataSystemService extends OpenmrsService {
	
	/**
	 * Returns an item by uuid. It can be called by any authenticated user. It is fetched in read
	 * only transaction.
	 * 
	 * @param uuid
	 * @return
	 * @throws APIException
	 */
	@Authorized()
	@Transactional(readOnly = true)
	Item getItemByUuid(String uuid) throws APIException;
	
	/**
	 * Saves an item. Sets the owner to superuser, if it is not set. It can be called by users with
	 * this module's privilege. It is executed in a transaction.
	 * 
	 * @param item
	 * @return
	 * @throws APIException
	 */
	@Authorized(ClinicalDataSystemConfig.MODULE_PRIVILEGE)
	@Transactional
	Item saveItem(Item item) throws APIException;
	
	// --- CDS features (no controllers/REST; service-only) ---
	
	/**
	 * Returns patient IDs with upcoming appointments (concept 5096 on form 27) within the next N
	 * days.
	 */
	@Authorized()
	@Transactional(readOnly = true)
	List<Integer> getUpcomingAppointmentPatientIds(int withinDays) throws APIException;
	
	/**
	 * Returns patient IDs with missed appointments within the last N days and no follow-up
	 * encounter.
	 */
	@Authorized()
	@Transactional(readOnly = true)
	List<Integer> getMissedAppointmentPatientIds(int lastDays) throws APIException;
	
	/**
	 * Returns patient IDs on the verge of IIT based on tracking form (13) and no discontinuation
	 * reason (165470).
	 */
	@Authorized()
	@Transactional(readOnly = true)
	List<Integer> getIITPatientIds(int lookbackDays) throws APIException;
	
	/**
	 * Returns client effort entries (tracking history) for a patient from tracking form (13).
	 */
	@Authorized()
	@Transactional(readOnly = true)
	List<ClientEffortEntry> getClientEffort(Integer patientId) throws APIException;
	
	/**
	 * Adds a new action record to cds_actions_table.
	 */
	@Authorized(ClinicalDataSystemConfig.MODULE_PRIVILEGE)
	@Transactional
	void addCdsAction(CdsActionRecord action) throws APIException;
	
	/**
	 * Returns all pending actions from cds_actions_table.
	 */
	@Authorized()
	@Transactional(readOnly = true)
	List<CdsActionRecord> getPendingCdsActions() throws APIException;
	
	/**
	 * Updates the status for a given action in cds_actions_table.
	 */
	@Authorized(ClinicalDataSystemConfig.MODULE_PRIVILEGE)
	@Transactional
	void updateCdsActionStatus(Integer actionId, String status) throws APIException;
}
