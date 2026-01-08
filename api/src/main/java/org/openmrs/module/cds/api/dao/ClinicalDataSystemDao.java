/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.cds.api.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.SQLQuery;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.cds.Item;
import org.openmrs.module.cds.api.dto.CdsActionRecord;
import org.openmrs.module.cds.api.dto.ClientEffortEntry;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository("cds.ClinicalDataSystemDao")
public class ClinicalDataSystemDao {
	
	private static final Log log = LogFactory.getLog(ClinicalDataSystemDao.class);

	DbSessionFactory sessionFactory;
	
	/**
	 * Injected in moduleApplicationContext.xml
	 */
	public void setSessionFactory(DbSessionFactory sessionFactory) {
		System.out.println("[CDS DAO] setSessionFactory() called");
		System.out.println("[CDS DAO] Parameter - sessionFactory: " + (sessionFactory != null ? sessionFactory.getClass().getName() : "NULL"));
		this.sessionFactory = sessionFactory;
		System.out.println("[CDS DAO] setSessionFactory() - SessionFactory injected successfully");
	}
	
	private DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	public Item getItemByUuid(String uuid) {
		System.out.println("[CDS DAO] getItemByUuid() called");
		System.out.println("[CDS DAO] Parameter - uuid: " + (uuid != null ? uuid : "NULL"));
		try {
			Item result = (Item) getSession().createCriteria(Item.class).add(Restrictions.eq("uuid", uuid)).uniqueResult();
			System.out.println("[CDS DAO] getItemByUuid() - Query executed successfully, Result: " + (result != null ? result.getId() : "NULL"));
			return result;
		} catch (Exception e) {
			log.error("[CDS DAO] getItemByUuid() - ERROR: " + e.getMessage(), e);
			throw e;
		}
	}
	
	public Item saveItem(Item item) {
		System.out.println("[CDS DAO] saveItem() called");
		System.out.println("[CDS DAO] Parameter - item: " + (item != null ? "Item ID=" + item.getId() : "NULL"));
		try {
			getSession().saveOrUpdate(item);
			System.out.println("[CDS DAO] saveItem() - Item saved/updated successfully, Item ID: " + item.getId());
			return item;
		} catch (Exception e) {
			log.error("[CDS DAO] saveItem() - ERROR: " + e.getMessage(), e);
			throw e;
		}
	}
	
	// ---- CDS custom queries ----
	
	@SuppressWarnings("unchecked")
	public List<Integer> getUpcomingAppointmentPatientIds(Date now, Date until) {
		System.out.println("[CDS DAO] getUpcomingAppointmentPatientIds() called");
		System.out.println("[CDS DAO] Parameter - now: " + now);
		System.out.println("[CDS DAO] Parameter - until: " + until);
		try {
			String sql = "select distinct e.patient_id " + "from obs o join encounter e on o.encounter_id = e.encounter_id "
			        + "where o.concept_id = 5096 and e.form_id = 27 " + "and o.value_datetime between :now and :until "
			        + "and o.voided = 0 and e.voided = 0";
			log.debug("[CDS DAO] Executing SQL: " + sql);
			SQLQuery q = getSession().createSQLQuery(sql);
			q.setDate("now", now);
			q.setDate("until", until);
			List<?> rows = q.list();
			System.out.println("[CDS DAO] getUpcomingAppointmentPatientIds() - Query returned " + rows.size() + " rows");

			List<Integer> result = new ArrayList<Integer>();
			for (Object r : rows) {
				if (r instanceof Number)
					result.add(((Number) r).intValue());
			}
			System.out.println("[CDS DAO] getUpcomingAppointmentPatientIds() - Parsed " + result.size() + " patient IDs");
			return result;
		} catch (Exception e) {
			log.error("[CDS DAO] getUpcomingAppointmentPatientIds() - ERROR: " + e.getMessage(), e);
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getMissedAppointmentPatientIds(Date fromDate, Date now) {
		System.out.println("[CDS DAO] getMissedAppointmentPatientIds() called");
		System.out.println("[CDS DAO] Parameter - fromDate: " + fromDate);
		System.out.println("[CDS DAO] Parameter - now: " + now);
		try {
			String sql = "select distinct e.patient_id from obs o "
			        + "join encounter e on o.encounter_id = e.encounter_id "
			        + "where o.concept_id = 5096 and e.form_id = 27 "
			        + "and o.value_datetime < :now and o.value_datetime > :fromDate "
			        + "and o.voided = 0 and e.voided = 0 "
			        + "and not exists (select 1 from encounter e2 where e2.patient_id = e.patient_id and e2.form_id in (27,14,21) and e2.encounter_datetime >= o.value_datetime and e2.voided = 0)";
			log.debug("[CDS DAO] Executing SQL: " + sql);
			SQLQuery q = getSession().createSQLQuery(sql);
			q.setDate("now", now);
			q.setDate("fromDate", fromDate);
			List<?> rows = q.list();
			System.out.println("[CDS DAO] getMissedAppointmentPatientIds() - Query returned " + rows.size() + " rows");

			List<Integer> result = new ArrayList<Integer>();
			for (Object r : rows) {
				if (r instanceof Number)
					result.add(((Number) r).intValue());
			}
			System.out.println("[CDS DAO] getMissedAppointmentPatientIds() - Parsed " + result.size() + " patient IDs");
			return result;
		} catch (Exception e) {
			log.error("[CDS DAO] getMissedAppointmentPatientIds() - ERROR: " + e.getMessage(), e);
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getIITPatientIds(Date fromDate, Date now) {
		System.out.println("[CDS DAO] getIITPatientIds() called");
		System.out.println("[CDS DAO] Parameter - fromDate: " + fromDate);
		System.out.println("[CDS DAO] Parameter - now: " + now);
		try {
			String sql = "select distinct e.patient_id "
			        + "from encounter e "
			        + "join obs o_appt on o_appt.person_id = e.patient_id "
			        + "where e.form_id = 13 and e.voided = 0 and o_appt.voided = 0 "
			        + "and o_appt.concept_id = 5096 and o_appt.value_datetime between :fromDate and :now and o_appt.value_datetime < :now "
			        + "and not exists (select 1 from obs o_disc where o_disc.encounter_id = e.encounter_id and o_disc.concept_id = 165470 and o_disc.voided = 0)";
			log.debug("[CDS DAO] Executing SQL: " + sql);
			SQLQuery q = getSession().createSQLQuery(sql);
			q.setDate("fromDate", fromDate);
			q.setDate("now", now);
			List<?> rows = q.list();
			System.out.println("[CDS DAO] getIITPatientIds() - Query returned " + rows.size() + " rows");

			List<Integer> result = new ArrayList<Integer>();
			for (Object r : rows) {
				if (r instanceof Number)
					result.add(((Number) r).intValue());
			}
			System.out.println("[CDS DAO] getIITPatientIds() - Parsed " + result.size() + " patient IDs");
			return result;
		} catch (Exception e) {
			log.error("[CDS DAO] getIITPatientIds() - ERROR: " + e.getMessage(), e);
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ClientEffortEntry> getClientEffort(Integer patientId) {
		System.out.println("[CDS DAO] getClientEffort() called");
		System.out.println("[CDS DAO] Parameter - patientId: " + patientId);
		try {
			String sql = "select e.encounter_datetime as action_date, "
			        + "(select cn.name from concept_name cn where cn.concept_id = o_status.value_coded and cn.locale = 'en' and cn.concept_name_type = 'FULLY_SPECIFIED') as status_name, "
			        + "o_comment.value_text as comments "
			        + "from encounter e "
			        + "left join obs o_status on e.encounter_id = o_status.encounter_id and o_status.concept_id = 167239 and o_status.voided = 0 "
			        + "left join obs o_comment on e.encounter_id = o_comment.encounter_id and o_comment.concept_id = 167237 and o_comment.voided = 0 "
			        + "where e.patient_id = :pid and e.form_id = 13 and e.voided = 0 " + "order by e.encounter_datetime desc";
			log.debug("[CDS DAO] Executing SQL: " + sql);
			SQLQuery q = getSession().createSQLQuery(sql);
			q.setInteger("pid", patientId);
			List<?> rows = q.list();
			System.out.println("[CDS DAO] getClientEffort() - Query returned " + rows.size() + " rows");

			List<ClientEffortEntry> result = new ArrayList<ClientEffortEntry>();
			for (Object row : rows) {
				Object[] cols = (Object[]) row;
				ClientEffortEntry e = new ClientEffortEntry((Date) cols[0], cols[1] != null ? cols[1].toString() : null,
				        cols[2] != null ? cols[2].toString() : null);
				result.add(e);
			}
			System.out.println("[CDS DAO] getClientEffort() - Parsed " + result.size() + " client effort entries");
			return result;
		} catch (Exception e) {
			log.error("[CDS DAO] getClientEffort() - ERROR: " + e.getMessage(), e);
			throw e;
		}
	}
	
	public void insertCdsAction(CdsActionRecord a) {
		System.out.println("[CDS DAO] insertCdsAction() called");
		System.out.println("[CDS DAO] Parameter - CdsActionRecord: patientId=" + a.getPatientId() + ", encounterId=" + a.getEncounterId()
		        + ", callReport=" + a.getCallReport() + ", nextStepAction=" + a.getNextStepAction()
		        + ", assignedToUserId=" + a.getAssignedToUserId() + ", status=" + a.getStatus());
		try {
			String sql = "insert into cds_actions_table (patient_id, encounter_id, call_report, next_step_action, assigned_to_user_id, status) "
			        + "values (:patient_id, :encounter_id, :call_report, :next_step_action, :assigned_to_user_id, coalesce(:status, 'PENDING'))";
			log.debug("[CDS DAO] Executing SQL: " + sql);
			SQLQuery q = getSession().createSQLQuery(sql);
			q.setInteger("patient_id", a.getPatientId());
			if (a.getEncounterId() == null)
				q.setParameter("encounter_id", null);
			else
				q.setInteger("encounter_id", a.getEncounterId());
			q.setString("call_report", a.getCallReport());
			q.setString("next_step_action", a.getNextStepAction());
			if (a.getAssignedToUserId() == null)
				q.setParameter("assigned_to_user_id", null);
			else
				q.setInteger("assigned_to_user_id", a.getAssignedToUserId());
			q.setString("status", a.getStatus());
			int result = q.executeUpdate();
			System.out.println("[CDS DAO] insertCdsAction() - Query executed successfully, Rows affected: " + result);
		} catch (Exception e) {
			log.error("[CDS DAO] insertCdsAction() - ERROR: " + e.getMessage(), e);
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<CdsActionRecord> getPendingCdsActions() {
		System.out.println("[CDS DAO] getPendingCdsActions() called");
		try {
			String sql = "select action_id, patient_id, encounter_id, call_report, next_step_action, assigned_to_user_id, status, date_created "
			        + "from cds_actions_table where status = 'PENDING' order by date_created desc";
			log.debug("[CDS DAO] Executing SQL: " + sql);
			SQLQuery q = getSession().createSQLQuery(sql);
			List<?> rows = q.list();
			System.out.println("[CDS DAO] getPendingCdsActions() - Query returned " + rows.size() + " rows");

			List<CdsActionRecord> result = new ArrayList<CdsActionRecord>();
			for (Object row : rows) {
				Object[] cols = (Object[]) row;
				CdsActionRecord a = new CdsActionRecord();
				a.setActionId(cols[0] != null ? ((Number) cols[0]).intValue() : null);
				a.setPatientId(cols[1] != null ? ((Number) cols[1]).intValue() : null);
				a.setEncounterId(cols[2] != null ? ((Number) cols[2]).intValue() : null);
				a.setCallReport(cols[3] != null ? cols[3].toString() : null);
				a.setNextStepAction(cols[4] != null ? cols[4].toString() : null);
				a.setAssignedToUserId(cols[5] != null ? ((Number) cols[5]).intValue() : null);
				a.setStatus(cols[6] != null ? cols[6].toString() : null);
				a.setDateCreated((Date) cols[7]);
				result.add(a);
			}
			System.out.println("[CDS DAO] getPendingCdsActions() - Parsed " + result.size() + " CDS action records");
			return result;
		} catch (Exception e) {
			log.error("[CDS DAO] getPendingCdsActions() - ERROR: " + e.getMessage(), e);
			throw e;
		}
	}
	
	public void updateCdsActionStatus(Integer actionId, String status) {
		log.info("[CDS DAO] updateCdsActionStatus() called");
		log.info("[CDS DAO] Parameter - actionId: " + actionId);
		log.info("[CDS DAO] Parameter - status: " + status);
		try {
			String sql = "update cds_actions_table set status = :status where action_id = :id";
			log.debug("[CDS DAO] Executing SQL: " + sql);
			SQLQuery q = getSession().createSQLQuery(sql);
			q.setString("status", status);
			q.setInteger("id", actionId);
			int result = q.executeUpdate();
			log.info("[CDS DAO] updateCdsActionStatus() - Query executed successfully, Rows affected: " + result);
		} catch (Exception e) {
			log.error("[CDS DAO] updateCdsActionStatus() - ERROR: " + e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * Get all pending actions for a specific patient
	 */
	@SuppressWarnings("unchecked")
	public List<CdsActionRecord> getPendingActionsByPatient(Integer patientId) {
		log.info("[CDS DAO] getPendingActionsByPatient() called");
		log.info("[CDS DAO] Parameter - patientId: " + patientId);
		try {
			String sql = "select action_id, patient_id, encounter_id, call_report, next_step_action, assigned_to_user_id, status, date_created "
			        + "from cds_actions_table where patient_id = :pid and status = 'PENDING' order by date_created desc";
			log.debug("[CDS DAO] Executing SQL: " + sql);
			SQLQuery q = getSession().createSQLQuery(sql);
			q.setInteger("pid", patientId);
			List<?> rows = q.list();
			log.info("[CDS DAO] getPendingActionsByPatient() - Query returned " + rows.size() + " rows");

			List<CdsActionRecord> result = new ArrayList<CdsActionRecord>();
			for (Object row : rows) {
				Object[] cols = (Object[]) row;
				CdsActionRecord a = new CdsActionRecord();
				a.setActionId(cols[0] != null ? ((Number) cols[0]).intValue() : null);
				a.setPatientId(cols[1] != null ? ((Number) cols[1]).intValue() : null);
				a.setEncounterId(cols[2] != null ? ((Number) cols[2]).intValue() : null);
				a.setCallReport(cols[3] != null ? cols[3].toString() : null);
				a.setNextStepAction(cols[4] != null ? cols[4].toString() : null);
				a.setAssignedToUserId(cols[5] != null ? ((Number) cols[5]).intValue() : null);
				a.setStatus(cols[6] != null ? cols[6].toString() : null);
				a.setDateCreated((Date) cols[7]);
				result.add(a);
			}
			log.info("[CDS DAO] getPendingActionsByPatient() - Parsed " + result.size() + " pending action records");
			return result;
		} catch (Exception e) {
			log.error("[CDS DAO] getPendingActionsByPatient() - ERROR: " + e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * Get all actions (pending and completed) for a specific patient
	 */
	@SuppressWarnings("unchecked")
	public List<CdsActionRecord> getAllActionsByPatient(Integer patientId) {
		log.info("[CDS DAO] getAllActionsByPatient() called");
		log.info("[CDS DAO] Parameter - patientId: " + patientId);
		try {
			String sql = "select action_id, patient_id, encounter_id, call_report, next_step_action, assigned_to_user_id, status, date_created "
			        + "from cds_actions_table where patient_id = :pid order by date_created desc";
			log.debug("[CDS DAO] Executing SQL: " + sql);
			SQLQuery q = getSession().createSQLQuery(sql);
			q.setInteger("pid", patientId);
			List<?> rows = q.list();
			log.info("[CDS DAO] getAllActionsByPatient() - Query returned " + rows.size() + " rows");

			List<CdsActionRecord> result = new ArrayList<CdsActionRecord>();
			for (Object row : rows) {
				Object[] cols = (Object[]) row;
				CdsActionRecord a = new CdsActionRecord();
				a.setActionId(cols[0] != null ? ((Number) cols[0]).intValue() : null);
				a.setPatientId(cols[1] != null ? ((Number) cols[1]).intValue() : null);
				a.setEncounterId(cols[2] != null ? ((Number) cols[2]).intValue() : null);
				a.setCallReport(cols[3] != null ? cols[3].toString() : null);
				a.setNextStepAction(cols[4] != null ? cols[4].toString() : null);
				a.setAssignedToUserId(cols[5] != null ? ((Number) cols[5]).intValue() : null);
				a.setStatus(cols[6] != null ? cols[6].toString() : null);
				a.setDateCreated((Date) cols[7]);
				result.add(a);
			}
			log.info("[CDS DAO] getAllActionsByPatient() - Parsed " + result.size() + " action records");
			return result;
		} catch (Exception e) {
			log.error("[CDS DAO] getAllActionsByPatient() - ERROR: " + e.getMessage(), e);
			throw e;
		}
	}
}
