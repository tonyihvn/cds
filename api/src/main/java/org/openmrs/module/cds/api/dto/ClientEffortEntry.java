package org.openmrs.module.cds.api.dto;

import java.util.Date;

public class ClientEffortEntry {
	
	private Date actionDate;
	
	private String status;
	
	private String comments;
	
	public ClientEffortEntry() {
	}
	
	public ClientEffortEntry(Date actionDate, String status, String comments) {
		this.actionDate = actionDate;
		this.status = status;
		this.comments = comments;
	}
	
	public Date getActionDate() {
		return actionDate;
	}
	
	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getComments() {
		return comments;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}
}
