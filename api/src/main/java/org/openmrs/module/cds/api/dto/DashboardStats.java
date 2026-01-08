/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.cds.api.dto;

/**
 * Data Transfer Object for dashboard statistics
 */
public class DashboardStats {
	
	private int iitCount;
	
	private int missedCount;
	
	private int upcomingCount;
	
	private int pendingActionsCount;
	
	public DashboardStats() {
	}
	
	public DashboardStats(int iitCount, int missedCount, int upcomingCount, int pendingActionsCount) {
		this.iitCount = iitCount;
		this.missedCount = missedCount;
		this.upcomingCount = upcomingCount;
		this.pendingActionsCount = pendingActionsCount;
	}
	
	public int getIitCount() {
		return iitCount;
	}
	
	public void setIitCount(int iitCount) {
		this.iitCount = iitCount;
	}
	
	public int getMissedCount() {
		return missedCount;
	}
	
	public void setMissedCount(int missedCount) {
		this.missedCount = missedCount;
	}
	
	public int getUpcomingCount() {
		return upcomingCount;
	}
	
	public void setUpcomingCount(int upcomingCount) {
		this.upcomingCount = upcomingCount;
	}
	
	public int getPendingActionsCount() {
		return pendingActionsCount;
	}
	
	public void setPendingActionsCount(int pendingActionsCount) {
		this.pendingActionsCount = pendingActionsCount;
	}
}
