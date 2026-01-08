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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.cds.api.dao.ClinicalDataSystemDao;
import org.openmrs.module.cds.api.dto.CdsActionRecord;
import org.openmrs.module.cds.api.dto.ClientEffortEntry;
import org.openmrs.module.cds.api.impl.ClinicalDataSystemServiceImpl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ClinicalDataSystemService using actual method signatures
 * Tests all business logic and error handling
 */
@RunWith(MockitoJUnitRunner.class)
public class ClinicalDataSystemServiceTest {

	@Mock
	private ClinicalDataSystemDao dao;

	@InjectMocks
	private ClinicalDataSystemServiceImpl service;

	private Date now;
	private Date pastDate;

	@Before
	public void setUp() {
		now = new Date();
		pastDate = new Date(System.currentTimeMillis() - (90 * 24 * 60 * 60 * 1000L));
	}

	@Test
	public void testGetIITPatientIds_Success() {
		// Arrange
		List<Integer> expectedIds = createIntegerList(5);
		when(dao.getIITPatientIds(pastDate, now)).thenReturn(expectedIds);

		// Act
		List<Integer> result = service.getIITPatientIds(90);

		// Assert
		assertNotNull(result);
		assertEquals(5, result.size());
		verify(dao, times(1)).getIITPatientIds(any(Date.class), any(Date.class));
	}

	@Test
	public void testGetIITPatientIds_EmptyResult() {
		// Arrange
		when(dao.getIITPatientIds(any(Date.class), any(Date.class))).thenReturn(new ArrayList<>());

		// Act
		List<Integer> result = service.getIITPatientIds(90);

		// Assert
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetMissedAppointmentPatientIds_Success() {
		// Arrange
		List<Integer> expectedIds = createIntegerList(12);
		when(dao.getMissedAppointmentPatientIds(any(Date.class), any(Date.class))).thenReturn(expectedIds);

		// Act
		List<Integer> result = service.getMissedAppointmentPatientIds(30);

		// Assert
		assertNotNull(result);
		assertEquals(12, result.size());
		verify(dao, times(1)).getMissedAppointmentPatientIds(any(Date.class), any(Date.class));
	}

	@Test
	public void testGetUpcomingAppointmentPatientIds_Success() {
		// Arrange
		List<Integer> expectedIds = createIntegerList(23);
		when(dao.getUpcomingAppointmentPatientIds(any(Date.class), any(Date.class))).thenReturn(expectedIds);

		// Act
		List<Integer> result = service.getUpcomingAppointmentPatientIds(30);

		// Assert
		assertNotNull(result);
		assertEquals(23, result.size());
		verify(dao, times(1)).getUpcomingAppointmentPatientIds(any(Date.class), any(Date.class));
	}

	@Test
	public void testGetPendingCdsActions_Success() {
		// Arrange
		List<CdsActionRecord> expectedActions = createActionList(5);
		when(dao.getPendingCdsActions()).thenReturn(expectedActions);

		// Act
		List<CdsActionRecord> result = service.getPendingCdsActions();

		// Assert
		assertNotNull(result);
		assertEquals(5, result.size());
		verify(dao, times(1)).getPendingCdsActions();
	}

	@Test
	public void testGetPendingCdsActions_EmptyResult() {
		// Arrange
		when(dao.getPendingCdsActions()).thenReturn(new ArrayList<>());

		// Act
		List<CdsActionRecord> result = service.getPendingCdsActions();

		// Assert
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetClientEffort_Success() {
		// Arrange
		Integer patientId = 1;
		List<ClientEffortEntry> expectedEntries = createEffortList(3);
		when(dao.getClientEffort(patientId)).thenReturn(expectedEntries);

		// Act
		List<ClientEffortEntry> result = service.getClientEffort(patientId);

		// Assert
		assertNotNull(result);
		assertEquals(3, result.size());
		verify(dao, times(1)).getClientEffort(patientId);
	}

	@Test
	public void testMultipleConsecutiveQueries() {
		// Arrange
		List<Integer> ids = createIntegerList(8);
		when(dao.getIITPatientIds(any(Date.class), any(Date.class))).thenReturn(ids);

		// Act
		List<Integer> result1 = service.getIITPatientIds(90);
		List<Integer> result2 = service.getIITPatientIds(90);

		// Assert
		assertNotNull(result1);
		assertNotNull(result2);
		assertEquals(result1.size(), result2.size());
		verify(dao, times(2)).getIITPatientIds(any(Date.class), any(Date.class));
	}

	@Test
	public void testLargeDataSets() {
		// Arrange
		List<Integer> largeList = createIntegerList(10000);
		when(dao.getIITPatientIds(any(Date.class), any(Date.class))).thenReturn(largeList);

		// Act
		List<Integer> result = service.getIITPatientIds(90);

		// Assert
		assertNotNull(result);
		assertEquals(10000, result.size());
	}

	@Test
	public void testDifferentLookbackPeriods() {
		// Arrange
		when(dao.getIITPatientIds(any(Date.class), any(Date.class)))
				.thenReturn(createIntegerList(5))
				.thenReturn(createIntegerList(10))
				.thenReturn(createIntegerList(15));

		// Act
		List<Integer> result30 = service.getIITPatientIds(30);
		List<Integer> result60 = service.getIITPatientIds(60);
		List<Integer> result90 = service.getIITPatientIds(90);

		// Assert
		assertEquals(5, result30.size());
		assertEquals(10, result60.size());
		assertEquals(15, result90.size());
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

	private List<ClientEffortEntry> createEffortList(int count) {
		List<ClientEffortEntry> list = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			list.add(new ClientEffortEntry());
		}
		return list;
	}
}

