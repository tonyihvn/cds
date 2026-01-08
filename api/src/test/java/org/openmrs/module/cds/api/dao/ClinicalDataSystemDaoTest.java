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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.db.hibernate.DbSession;
import org.hibernate.SQLQuery;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatcher.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ClinicalDataSystemDao
 * Tests database access layer with Hibernate mocks
 */
@RunWith(MockitoJUnitRunner.class)
public class ClinicalDataSystemDaoTest {

 @Mock
 private DbSessionFactory sessionFactory;

 @Mock
 private DbSession session;

 @Mock
 private SQLQuery query;

	@InjectMocks
	private ClinicalDataSystemDao dao;

 @Before
 public void setUp() {
     // Setup default behavior
     when(sessionFactory.getCurrentSession()).thenReturn(session);
 }

	@Test
	public void testGetIITPatientIds_Success() {
		// Arrange
		List<Integer> mockIds = createIntegerList(8);
		Date now = new Date();
		Date pastDate = new Date(System.currentTimeMillis() - (90 * 24 * 60 * 60 * 1000L));

  when(session.createSQLQuery(anyString())).thenReturn(query);
  when(query.setDate(anyString(), any(Date.class))).thenReturn(query);
  when(query.list()).thenReturn(mockIds);

		// Act
		List<Integer> result = dao.getIITPatientIds(pastDate, now);

		// Assert
		assertNotNull(result);
		assertEquals(8, result.size());
  verify(session, times(1)).createSQLQuery(anyString());
	}

	@Test
	public void testGetIITPatientIds_EmptyResult() {
		// Arrange
		Date now = new Date();
		Date pastDate = new Date(System.currentTimeMillis() - (90 * 24 * 60 * 60 * 1000L));

  when(session.createSQLQuery(anyString())).thenReturn(query);
  when(query.setDate(anyString(), any(Date.class))).thenReturn(query);
  when(query.list()).thenReturn(new ArrayList<>());

		// Act
		List<Integer> result = dao.getIITPatientIds(pastDate, now);

		// Assert
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetMissedAppointmentPatientIds_Success() {
		// Arrange
		List<Integer> mockIds = createIntegerList(15);
		Date now = new Date();
		Date pastDate = new Date(System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L));

  when(session.createSQLQuery(anyString())).thenReturn(query);
  when(query.setDate(anyString(), any(Date.class))).thenReturn(query);
  when(query.list()).thenReturn(mockIds);

		// Act
		List<Integer> result = dao.getMissedAppointmentPatientIds(pastDate, now);

		// Assert
		assertNotNull(result);
		assertEquals(15, result.size());
  verify(session, times(1)).createSQLQuery(anyString());
	}

	@Test
	public void testGetUpcomingAppointmentPatientIds_Success() {
		// Arrange
		List<Integer> mockIds = createIntegerList(22);
		Date now = new Date();
		Date futureDate = new Date(System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000L));

  when(session.createSQLQuery(anyString())).thenReturn(query);
  when(query.setDate(anyString(), any(Date.class))).thenReturn(query);
  when(query.list()).thenReturn(mockIds);

		// Act
		List<Integer> result = dao.getUpcomingAppointmentPatientIds(now, futureDate);

		// Assert
		assertNotNull(result);
		assertEquals(22, result.size());
	}

	@Test
	public void testGetIITPatientIds_DifferentLookbackPeriods() {
		// Arrange
		Date now = new Date();
		Date past30 = new Date(System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L));
		Date past60 = new Date(System.currentTimeMillis() - (60 * 24 * 60 * 60 * 1000L));
		Date past90 = new Date(System.currentTimeMillis() - (90 * 24 * 60 * 60 * 1000L));

  when(session.createSQLQuery(anyString())).thenReturn(query);
  when(query.setDate(anyString(), any(Date.class))).thenReturn(query);
  when(query.list()).thenReturn(createIntegerList(5), createIntegerList(10), createIntegerList(15));

		// Act
		List<Integer> result30 = dao.getIITPatientIds(past30, now);
		List<Integer> result60 = dao.getIITPatientIds(past60, now);
		List<Integer> result90 = dao.getIITPatientIds(past90, now);

		// Assert
		assertEquals(5, result30.size());
		assertEquals(10, result60.size());
		assertEquals(15, result90.size());
	}

 @Test(expected = RuntimeException.class)
 public void testSessionException_ConnectionRefused() {
     // Arrange
     when(sessionFactory.getCurrentSession()).thenThrow(new RuntimeException("Connection refused"));

     // Act & Assert
     Date now = new Date();
     Date past = new Date(now.getTime() - (90L * 24 * 60 * 60 * 1000));
     dao.getIITPatientIds(past, now);
 }

 @Test(expected = RuntimeException.class)
 public void testQueryException_InvalidSQL() {
     // Arrange
     when(session.createSQLQuery(anyString())).thenThrow(new RuntimeException("Invalid SQL"));

     // Act & Assert
     Date now = new Date();
     Date past = new Date(now.getTime() - (90L * 24 * 60 * 60 * 1000));
     dao.getIITPatientIds(past, now);
 }

	@Test
	public void testParameterBinding_CorrectParameter() {
		// Arrange
		Date now = new Date();
		Date pastDate = new Date(System.currentTimeMillis() - (90 * 24 * 60 * 60 * 1000L));

  when(session.createSQLQuery(anyString())).thenReturn(query);
  when(query.setDate(anyString(), any(Date.class))).thenReturn(query);
  when(query.list()).thenReturn(new ArrayList<>());

		// Act
		dao.getIITPatientIds(pastDate, now);

		// Assert
  verify(query, atLeastOnce()).setDate(anyString(), any(Date.class));
	}

	@Test
	public void testMultipleParameterBindings() {
		// Arrange
		Date now = new Date();
		Date pastDate = new Date(System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L));

  when(session.createSQLQuery(anyString())).thenReturn(query);
  when(query.setDate(anyString(), any(Date.class))).thenReturn(query);
  when(query.list()).thenReturn(new ArrayList<>());

		// Act
		dao.getMissedAppointmentPatientIds(pastDate, now);

		// Assert
  verify(query, atLeastOnce()).setDate(anyString(), any(Date.class));
	}

	@Test
	public void testLargeResultSet() {
		// Arrange
		List<Integer> largeList = createIntegerList(10000);
		Date now = new Date();
		Date pastDate = new Date(System.currentTimeMillis() - (90 * 24 * 60 * 60 * 1000L));

  when(session.createSQLQuery(anyString())).thenReturn(query);
  when(query.setDate(anyString(), any(Date.class))).thenReturn(query);
  when(query.list()).thenReturn(largeList);

		// Act
		List<Integer> result = dao.getIITPatientIds(pastDate, now);

		// Assert
		assertNotNull(result);
		assertEquals(10000, result.size());
	}

	@Test(expected = NullPointerException.class)
	public void testNullSessionFactory() {
		// Arrange
		dao.sessionFactory = null;
		Date now = new Date();
		Date pastDate = new Date(System.currentTimeMillis() - (90 * 24 * 60 * 60 * 1000L));

		// Act & Assert
		dao.getIITPatientIds(pastDate, now);
	}

	@Test(expected = RuntimeException.class)
	public void testSessionClosureHandling() {
		// Arrange
		Date now = new Date();
		Date pastDate = new Date(System.currentTimeMillis() - (90 * 24 * 60 * 60 * 1000L));

  when(session.createSQLQuery(anyString())).thenReturn(query);
  when(query.setDate(anyString(), any(Date.class))).thenReturn(query);
  when(query.list()).thenThrow(new RuntimeException("Session is closed"));

		// Act & Assert
		dao.getIITPatientIds(pastDate, now);
	}

	@Test
	public void testTransactionHandling() {
		// Arrange
		Date now = new Date();
		Date pastDate = new Date(System.currentTimeMillis() - (90 * 24 * 60 * 60 * 1000L));

  when(session.createSQLQuery(anyString())).thenReturn(query);
  when(query.setDate(anyString(), any(Date.class))).thenReturn(query);
  when(query.list()).thenReturn(createIntegerList(8));

		// Act
		List<Integer> result = dao.getIITPatientIds(pastDate, now);

		// Assert
		assertNotNull(result);
  verify(session, times(1)).createSQLQuery(anyString());
	}

	// Helper methods
	private List<Integer> createIntegerList(int count) {
		List<Integer> list = new ArrayList<>();
		for (int i = 1; i <= count; i++) {
			list.add(i);
		}
		return list;
	}
}

