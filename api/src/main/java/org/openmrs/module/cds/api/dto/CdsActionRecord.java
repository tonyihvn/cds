package org.openmrs.module.cds.api.dto;

import java.util.Date;

public class CdsActionRecord {
	
	private Integer actionId;
	
	private Integer patientId;
	
	private Integer encounterId;
	
	private String callReport;
	
	private String nextStepAction;
	
	private Integer assignedToUserId;
	
	private String status;
	
	private Date dateCreated;
	
	public Integer getActionId() {
		return actionId;
	}
	
	public void setActionId(Integer actionId) {
		this.actionId = actionId;
	}
	
	public Integer getPatientId() {
		return patientId;
	}
	
	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}
	
	public Integer getEncounterId() {
		return encounterId;
	}
	
	public void setEncounterId(Integer encounterId) {
		this.encounterId = encounterId;
	}
	
	public String getCallReport() {
		return callReport;
	}
	
	public void setCallReport(String callReport) {
		this.callReport = callReport;
	}
	
	public String getNextStepAction() {
		return nextStepAction;
	}
	
	public void setNextStepAction(String nextStepAction) {
		this.nextStepAction = nextStepAction;
	}
	
	public Integer getAssignedToUserId() {
		return assignedToUserId;
	}
	
	public void setAssignedToUserId(Integer assignedToUserId) {
		this.assignedToUserId = assignedToUserId;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Date getDateCreated() {
		return dateCreated;
	}
	
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
}
