/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.cds.api.impl;

import org.openmrs.api.APIException;
import org.openmrs.api.UserService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cds.Item;
import org.openmrs.module.cds.api.ClinicalDataSystemService;
import org.openmrs.module.cds.api.dao.ClinicalDataSystemDao;
import org.openmrs.module.cds.api.dto.CdsActionRecord;
import org.openmrs.module.cds.api.dto.ClientEffortEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ClinicalDataSystemServiceImpl extends BaseOpenmrsService implements ClinicalDataSystemService {
	
	ClinicalDataSystemDao dao;
	
	UserService userService;
	
	/**
	 * Injected in moduleApplicationContext.xml
	 */
	public void setDao(ClinicalDataSystemDao dao) {
		this.dao = dao;
	}
	
	/**
	 * Injected in moduleApplicationContext.xml
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@Override
	public Item getItemByUuid(String uuid) throws APIException {
		return dao.getItemByUuid(uuid);
	}
	
	@Override
	public Item saveItem(Item item) throws APIException {
		if (item.getOwner() == null) {
			item.setOwner(userService.getUser(1));
		}
		
		return dao.saveItem(item);
	}
	
	// ---- CDS features implementation ----
	
	@Override
	public List<Integer> getUpcomingAppointmentPatientIds(int withinDays) throws APIException {
		Date now = new Date();
		Date until = addDays(now, withinDays);
		return dao.getUpcomingAppointmentPatientIds(now, until);
	}
	
	@Override
	public List<Integer> getMissedAppointmentPatientIds(int lastDays) throws APIException {
		Date now = new Date();
		Date from = addDays(now, -lastDays);
		return dao.getMissedAppointmentPatientIds(from, now);
	}
	
	@Override
	public List<Integer> getIITPatientIds(int lookbackDays) throws APIException {
		Date now = new Date();
		Date from = addDays(now, -lookbackDays);
		return dao.getIITPatientIds(from, now);
	}
	
	@Override
	public List<ClientEffortEntry> getClientEffort(Integer patientId) throws APIException {
		if (patientId == null)
			return new ArrayList<ClientEffortEntry>();
		return dao.getClientEffort(patientId);
	}
	
	@Override
	public void addCdsAction(CdsActionRecord action) throws APIException {
		if (action == null || action.getPatientId() == null) {
			throw new APIException("PatientId is required to add an action");
		}
		dao.insertCdsAction(action);
	}
	
	@Override
	public List<CdsActionRecord> getPendingCdsActions() throws APIException {
		return dao.getPendingCdsActions();
	}
	
	@Override
	public void updateCdsActionStatus(Integer actionId, String status) throws APIException {
		if (actionId == null) {
			throw new APIException("actionId is required");
		}
		dao.updateCdsActionStatus(actionId, status);
	}
	
	private Date addDays(Date base, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(base);
		cal.add(Calendar.DAY_OF_MONTH, days);
		return cal.getTime();
	}
}
