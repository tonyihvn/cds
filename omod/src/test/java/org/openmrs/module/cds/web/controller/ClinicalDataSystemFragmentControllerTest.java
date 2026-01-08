/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.cds.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.cds.api.ClinicalDataSystemService;
import org.openmrs.module.cds.api.dto.DashboardStats;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ClinicalDataSystemFragmentController
 * Tests all endpoints and mock data fallback functionality
 */
@RunWith(MockitoJUnitRunner.class)
public class ClinicalDataSystemFragmentControllerTest {

	@Mock
	private ClinicalDataSystemService cdsService;

	@InjectMocks
	private ClinicalDataSystemController controller;

	private DashboardStats mockStats;

	@Before
	public void setUp() {
		mockStats = new DashboardStats();
		mockStats.setIitCount(8);
		mockStats.setMissedCount(15);
		mockStats.setUpcomingCount(22);
		mockStats.setPendingActionsCount(5);

		// Inject the mock service into the controller
		controller.cdsService = cdsService;
	}

	@Test
	public void testGetDashboardStats_Success() {
		// Arrange
		when(cdsService.getIITPatientIds(90)).thenReturn(createPatientList(8));
		when(cdsService.getMissedAppointmentPatientIds(30)).thenReturn(createPatientList(15));
		when(cdsService.getUpcomingAppointmentPatientIds(30)).thenReturn(createPatientList(22));
		when(cdsService.getPendingCdsActions()).thenReturn(createActionsList(5));

		// Act
		DashboardStats result = controller.getDashboardStats();

		// Assert
		assertNotNull(result);
		assertEquals(8, result.getIitCount());
		assertEquals(15, result.getMissedCount());
		assertEquals(22, result.getUpcomingCount());
		assertEquals(5, result.getPendingActionsCount());
		verify(cdsService, times(1)).getIITPatientIds(90);
	}

	@Test
	public void testGetDashboardStats_ServiceNull_ReturnsMockData() {
		// Arrange
		// Service is null (not initialized)
		controller.cdsService = null;

		// Act
		DashboardStats result = controller.getDashboardStats();

		// Assert
		assertNotNull(result);
		assertEquals(8, result.getIitCount());
		assertEquals(15, result.getMissedCount());
	}

	@Test
	public void testGetDashboardStats_DatabaseDown_ReturnsMockData() {
		// Arrange
		when(cdsService.getIITPatientIds(90)).thenThrow(new RuntimeException("Connection refused"));
		when(cdsService.getMissedAppointmentPatientIds(30)).thenThrow(new RuntimeException("Connection refused"));
		when(cdsService.getUpcomingAppointmentPatientIds(30)).thenThrow(new RuntimeException("Connection refused"));
		when(cdsService.getPendingCdsActions()).thenThrow(new RuntimeException("Connection refused"));

		// Act
		DashboardStats result = controller.getDashboardStats();

		// Assert
		assertNotNull(result);
		assertEquals(8, result.getIitCount());
		assertEquals(15, result.getMissedCount());
		assertEquals(22, result.getUpcomingCount());
		assertEquals(5, result.getPendingActionsCount());
	}

	@Test
	public void testGetDashboardStats_PartialFailure_UsesAvailableData() {
		// Arrange
		when(cdsService.getIITPatientIds(90)).thenReturn(createPatientList(8));
		when(cdsService.getMissedAppointmentPatientIds(30)).thenThrow(new RuntimeException("Connection error"));
		when(cdsService.getUpcomingAppointmentPatientIds(30)).thenReturn(createPatientList(22));
		when(cdsService.getPendingCdsActions()).thenReturn(createActionsList(5));

		// Act
		DashboardStats result = controller.getDashboardStats();

		// Assert
		assertNotNull(result);
		assertEquals(8, result.getIitCount());
		assertEquals(22, result.getUpcomingCount());
		assertEquals(5, result.getPendingActionsCount());
	}

	@Test
	public void testGetDashboardStats_AllZeroWithErrors_ReturnsMockData() {
		// Arrange
		when(cdsService.getIITPatientIds(90)).thenReturn(createPatientList(0));
		when(cdsService.getMissedAppointmentPatientIds(30)).thenThrow(new RuntimeException("Error"));
		when(cdsService.getUpcomingAppointmentPatientIds(30)).thenThrow(new RuntimeException("Error"));
		when(cdsService.getPendingCdsActions()).thenThrow(new RuntimeException("Error"));

		// Act
		DashboardStats result = controller.getDashboardStats();

		// Assert
		assertNotNull(result);
		assertEquals(8, result.getIitCount());
	}

	@Test
	public void testGetDashboardStats_CriticalException_ReturnsMockData() {
		// Arrange
		when(cdsService.getIITPatientIds(90)).thenThrow(new NullPointerException("Unexpected null"));

		// Act
		DashboardStats result = controller.getDashboardStats();

		// Assert
		assertNotNull(result);
		assertEquals(8, result.getIitCount());
	}

	@Test
	public void testGetMockDashboardStats() {
		// Act
		DashboardStats mockData = controller.getMockDashboardStats();

		// Assert
		assertNotNull(mockData);
		assertEquals(8, mockData.getIitCount());
		assertEquals(15, mockData.getMissedCount());
		assertEquals(22, mockData.getUpcomingCount());
		assertEquals(5, mockData.getPendingActionsCount());
	}

	@Test
	public void testGetDashboardStats_ZeroRealValues() {
		// Arrange
		when(cdsService.getIITPatientIds(90)).thenReturn(createPatientList(0));
		when(cdsService.getMissedAppointmentPatientIds(30)).thenReturn(createPatientList(0));
		when(cdsService.getUpcomingAppointmentPatientIds(30)).thenReturn(createPatientList(0));
		when(cdsService.getPendingCdsActions()).thenReturn(createActionsList(0));

		// Act
		DashboardStats result = controller.getDashboardStats();

		// Assert
		assertNotNull(result);
		assertEquals(0, result.getIitCount());
		assertEquals(0, result.getMissedCount());
	}

	@Test
	public void testGetDashboardStats_LargeNumbers() {
		// Arrange
		when(cdsService.getIITPatientIds(90)).thenReturn(createPatientList(1000));
		when(cdsService.getMissedAppointmentPatientIds(30)).thenReturn(createPatientList(5000));
		when(cdsService.getUpcomingAppointmentPatientIds(30)).thenReturn(createPatientList(3000));
		when(cdsService.getPendingCdsActions()).thenReturn(createActionsList(500));

		// Act
		DashboardStats result = controller.getDashboardStats();

		// Assert
		assertNotNull(result);
		assertEquals(1000, result.getIitCount());
		assertEquals(5000, result.getMissedCount());
		assertEquals(3000, result.getUpcomingCount());
	}

	@Test
	public void testGetDashboardStats_MultipleConsecutiveCalls() {
		// Arrange
		when(cdsService.getIITPatientIds(90)).thenReturn(createPatientList(8));
		when(cdsService.getMissedAppointmentPatientIds(30)).thenReturn(createPatientList(15));
		when(cdsService.getUpcomingAppointmentPatientIds(30)).thenReturn(createPatientList(22));
		when(cdsService.getPendingCdsActions()).thenReturn(createActionsList(5));

		// Act
		DashboardStats result1 = controller.getDashboardStats();
		DashboardStats result2 = controller.getDashboardStats();

		// Assert
		assertNotNull(result1);
		assertNotNull(result2);
		assertEquals(result1.getIitCount(), result2.getIitCount());
	}

	@Test
	public void testGetDashboardStats_NullListReturned() {
		// Arrange
		when(cdsService.getIITPatientIds(90)).thenReturn(null);
		when(cdsService.getMissedAppointmentPatientIds(30)).thenReturn(createPatientList(15));
		when(cdsService.getUpcomingAppointmentPatientIds(30)).thenReturn(createPatientList(22));
		when(cdsService.getPendingCdsActions()).thenReturn(createActionsList(5));

		// Act
		DashboardStats result = controller.getDashboardStats();

		// Assert
		assertNotNull(result);
		assertEquals(0, result.getIitCount());
	}

	// Helper methods
	private java.util.List<Integer> createPatientList(int count) {
		java.util.List<Integer> list = new java.util.ArrayList<>();
		for (int i = 0; i < count; i++) {
			list.add(i + 1);
		}
		return list;
	}

	private java.util.List<org.openmrs.module.cds.api.dto.CdsActionRecord> createActionsList(int count) {
		java.util.List<org.openmrs.module.cds.api.dto.CdsActionRecord> list = new java.util.ArrayList<>();
		for (int i = 0; i < count; i++) {
			list.add(new org.openmrs.module.cds.api.dto.CdsActionRecord());
		}
		return list;
	}
}

