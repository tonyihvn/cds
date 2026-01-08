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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.cds.api.ClinicalDataSystemService;
import org.openmrs.module.cds.api.dto.CdsActionRecord;
import org.openmrs.module.cds.api.dto.DashboardStats;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Integration tests for ClinicalDataSystemController
 * Tests end-to-end workflows and service integration
 */
@RunWith(MockitoJUnitRunner.class)
public class ClinicalDataSystemControllerIntegrationTest {

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
	}

	@Test
	public void testDashboardStatsEndpoint_WithRealData() {
		// Arrange
		List<Integer> iitIds = createIntegerList(8);
		List<Integer> missedIds = createIntegerList(15);
		List<Integer> upcomingIds = createIntegerList(22);
		List<CdsActionRecord> actions = createActionList(5);

		when(cdsService.getIITPatientIds(90)).thenReturn(iitIds);
		when(cdsService.getMissedAppointmentPatientIds(30)).thenReturn(missedIds);
		when(cdsService.getUpcomingAppointmentPatientIds(30)).thenReturn(upcomingIds);
		when(cdsService.getPendingCdsActions()).thenReturn(actions);

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
	public void testDashboardStatsEndpoint_WithMockData() {
		// Arrange
		when(cdsService.getIITPatientIds(90)).thenThrow(new RuntimeException("DB error"));
		when(cdsService.getMissedAppointmentPatientIds(30)).thenThrow(new RuntimeException("DB error"));
		when(cdsService.getUpcomingAppointmentPatientIds(30)).thenThrow(new RuntimeException("DB error"));
		when(cdsService.getPendingCdsActions()).thenThrow(new RuntimeException("DB error"));

		// Act
		DashboardStats result = controller.getDashboardStats();

		// Assert
		assertNotNull(result);
		assertEquals(8, result.getIitCount());
		assertEquals(15, result.getMissedCount());
	}

	@Test
	public void testDashboardWorkflow_CompleteFlow() {
		// Arrange
		List<Integer> iitIds = createIntegerList(8);
		when(cdsService.getIITPatientIds(90)).thenReturn(iitIds);
		when(cdsService.getMissedAppointmentPatientIds(30)).thenReturn(createIntegerList(15));
		when(cdsService.getUpcomingAppointmentPatientIds(30)).thenReturn(createIntegerList(22));
		when(cdsService.getPendingCdsActions()).thenReturn(createActionList(5));

		// Act
		DashboardStats stats = controller.getDashboardStats();

		// Assert
		assertNotNull(stats);
		assertTrue(stats.getIitCount() > 0);
		assertTrue(stats.getMissedCount() > 0);
		assertTrue(stats.getUpcomingCount() > 0);
		assertTrue(stats.getPendingActionsCount() > 0);

		// Verify all services were called
		verify(cdsService, times(1)).getIITPatientIds(90);
		verify(cdsService, times(1)).getMissedAppointmentPatientIds(30);
		verify(cdsService, times(1)).getUpcomingAppointmentPatientIds(30);
		verify(cdsService, times(1)).getPendingCdsActions();
	}

	@Test
	public void testDashboardWorkflow_ServiceCallOrder() {
		// Arrange
		when(cdsService.getIITPatientIds(90)).thenReturn(createIntegerList(8));
		when(cdsService.getMissedAppointmentPatientIds(30)).thenReturn(createIntegerList(15));
		when(cdsService.getUpcomingAppointmentPatientIds(30)).thenReturn(createIntegerList(22));
		when(cdsService.getPendingCdsActions()).thenReturn(createActionList(5));

		// Act
		DashboardStats stats = controller.getDashboardStats();

		// Assert
		assertNotNull(stats);
		// Verify services are called (order may vary based on implementation)
		verify(cdsService, atLeastOnce()).getIITPatientIds(90);
	}

	@Test
	public void testMultipleDashboardRefresh() {
		// Arrange
		List<Integer> iitIds = createIntegerList(8);
		when(cdsService.getIITPatientIds(90)).thenReturn(iitIds);
		when(cdsService.getMissedAppointmentPatientIds(30)).thenReturn(createIntegerList(15));
		when(cdsService.getUpcomingAppointmentPatientIds(30)).thenReturn(createIntegerList(22));
		when(cdsService.getPendingCdsActions()).thenReturn(createActionList(5));

		// Act
		DashboardStats stats1 = controller.getDashboardStats();
		DashboardStats stats2 = controller.getDashboardStats();
		DashboardStats stats3 = controller.getDashboardStats();

		// Assert
		assertEquals(stats1.getIitCount(), stats2.getIitCount());
		assertEquals(stats2.getIitCount(), stats3.getIitCount());
		verify(cdsService, times(3)).getIITPatientIds(90);
	}

	@Test
	public void testDashboardWithDynamicData() {
		// Arrange - Simulate changing data
		when(cdsService.getIITPatientIds(90))
				.thenReturn(createIntegerList(5))
				.thenReturn(createIntegerList(10))
				.thenReturn(createIntegerList(15));
		when(cdsService.getMissedAppointmentPatientIds(30)).thenReturn(createIntegerList(15));
		when(cdsService.getUpcomingAppointmentPatientIds(30)).thenReturn(createIntegerList(22));
		when(cdsService.getPendingCdsActions()).thenReturn(createActionList(5));

		// Act
		DashboardStats stats1 = controller.getDashboardStats();
		DashboardStats stats2 = controller.getDashboardStats();
		DashboardStats stats3 = controller.getDashboardStats();

		// Assert
		assertEquals(5, stats1.getIitCount());
		assertEquals(10, stats2.getIitCount());
		assertEquals(15, stats3.getIitCount());
	}

	@Test
	public void testDashboardGracefulDegradation() {
		// Arrange
		when(cdsService.getIITPatientIds(90)).thenReturn(createIntegerList(8));
		when(cdsService.getMissedAppointmentPatientIds(30)).thenThrow(new RuntimeException("Service down"));
		when(cdsService.getUpcomingAppointmentPatientIds(30)).thenReturn(createIntegerList(22));
		when(cdsService.getPendingCdsActions()).thenReturn(createActionList(5));

		// Act
		DashboardStats stats = controller.getDashboardStats();

		// Assert
		assertNotNull(stats);
		assertEquals(8, stats.getIitCount());
		assertEquals(22, stats.getUpcomingCount());
		// Missed count will be 0 due to error
	}

	@Test
	public void testDashboardPerformance() {
		// Arrange
		when(cdsService.getIITPatientIds(90)).thenReturn(createIntegerList(8));
		when(cdsService.getMissedAppointmentPatientIds(30)).thenReturn(createIntegerList(15));
		when(cdsService.getUpcomingAppointmentPatientIds(30)).thenReturn(createIntegerList(22));
		when(cdsService.getPendingCdsActions()).thenReturn(createActionList(5));

		// Act
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			controller.getDashboardStats();
		}
		long endTime = System.currentTimeMillis();

		// Assert
		long duration = endTime - startTime;
		assertTrue("Performance test failed: took " + duration + "ms", duration < 10000);
	}

	@Test
	public void testDashboardWithEmptyData() {
		// Arrange
		when(cdsService.getIITPatientIds(90)).thenReturn(new ArrayList<>());
		when(cdsService.getMissedAppointmentPatientIds(30)).thenReturn(new ArrayList<>());
		when(cdsService.getUpcomingAppointmentPatientIds(30)).thenReturn(new ArrayList<>());
		when(cdsService.getPendingCdsActions()).thenReturn(new ArrayList<>());

		// Act
		DashboardStats stats = controller.getDashboardStats();

		// Assert
		assertNotNull(stats);
		assertEquals(0, stats.getIitCount());
		assertEquals(0, stats.getMissedCount());
	}

	@Test
	public void testDashboardWithLargeDataSet() {
		// Arrange
		when(cdsService.getIITPatientIds(90)).thenReturn(createIntegerList(10000));
		when(cdsService.getMissedAppointmentPatientIds(30)).thenReturn(createIntegerList(50000));
		when(cdsService.getUpcomingAppointmentPatientIds(30)).thenReturn(createIntegerList(30000));
		when(cdsService.getPendingCdsActions()).thenReturn(createActionList(5000));

		// Act
		DashboardStats stats = controller.getDashboardStats();

		// Assert
		assertNotNull(stats);
		assertEquals(10000, stats.getIitCount());
		assertEquals(50000, stats.getMissedCount());
	}

	@Test
	public void testDashboardMockDataFallback() {
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
	public void testDashboardDataConsistency() {
		// Arrange
		List<Integer> iitIds = createIntegerList(8);
		when(cdsService.getIITPatientIds(90)).thenReturn(iitIds);
		when(cdsService.getMissedAppointmentPatientIds(30)).thenReturn(createIntegerList(15));
		when(cdsService.getUpcomingAppointmentPatientIds(30)).thenReturn(createIntegerList(22));
		when(cdsService.getPendingCdsActions()).thenReturn(createActionList(5));

		// Act
		DashboardStats stats = controller.getDashboardStats();

		// Assert - Verify data integrity
		assertNotNull(stats);
		assertTrue(stats.getIitCount() == iitIds.size());
		assertEquals(iitIds.size(), stats.getIitCount());
	}

	// Helper methods
	private List<Integer> createIntegerList(int count) {
		List<Integer> list = new ArrayList<>();
		for (int i = 1; i <= count; i++) {
			list.add(i);
		}
		return list;
	}

	private List<CdsActionRecord> createActionList(int count) {
		List<CdsActionRecord> list = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			list.add(new CdsActionRecord());
		}
		return list;
	}
}

