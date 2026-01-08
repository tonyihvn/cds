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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for DashboardStats DTO
 * Tests all getters, setters, and data validations
 */
public class DashboardStatsDTOTest {

	private DashboardStats stats;

	@Before
	public void setUp() {
		stats = new DashboardStats();
	}

	@Test
	public void testConstructor_NoArgs() {
		// Act
		DashboardStats newStats = new DashboardStats();

		// Assert
		assertNotNull(newStats);
	}

	@Test
	public void testConstructor_AllArgs() {
		// Act
		DashboardStats newStats = new DashboardStats(8, 15, 22, 5);

		// Assert
		assertNotNull(newStats);
		assertEquals(8, newStats.getIitCount());
		assertEquals(15, newStats.getMissedCount());
		assertEquals(22, newStats.getUpcomingCount());
		assertEquals(5, newStats.getPendingActionsCount());
	}

	@Test
	public void testSetAndGetIitCount() {
		// Act
		stats.setIitCount(8);

		// Assert
		assertEquals(8, stats.getIitCount());
	}

	@Test
	public void testSetAndGetMissedCount() {
		// Act
		stats.setMissedCount(15);

		// Assert
		assertEquals(15, stats.getMissedCount());
	}

	@Test
	public void testSetAndGetUpcomingCount() {
		// Act
		stats.setUpcomingCount(22);

		// Assert
		assertEquals(22, stats.getUpcomingCount());
	}

	@Test
	public void testSetAndGetPendingActionsCount() {
		// Act
		stats.setPendingActionsCount(5);

		// Assert
		assertEquals(5, stats.getPendingActionsCount());
	}

	@Test
	public void testZeroValues() {
		// Act
		stats.setIitCount(0);
		stats.setMissedCount(0);
		stats.setUpcomingCount(0);
		stats.setPendingActionsCount(0);

		// Assert
		assertEquals(0, stats.getIitCount());
		assertEquals(0, stats.getMissedCount());
		assertEquals(0, stats.getUpcomingCount());
		assertEquals(0, stats.getPendingActionsCount());
	}

	@Test
	public void testLargeValues() {
		// Act
		stats.setIitCount(10000);
		stats.setMissedCount(50000);
		stats.setUpcomingCount(30000);
		stats.setPendingActionsCount(5000);

		// Assert
		assertEquals(10000, stats.getIitCount());
		assertEquals(50000, stats.getMissedCount());
		assertEquals(30000, stats.getUpcomingCount());
		assertEquals(5000, stats.getPendingActionsCount());
	}

	@Test
	public void testNegativeValues() {
		// Act
		stats.setIitCount(-1);

		// Assert
		assertEquals(-1, stats.getIitCount());
	}

	@Test
	public void testMaxIntValue() {
		// Act
		stats.setIitCount(Integer.MAX_VALUE);

		// Assert
		assertEquals(Integer.MAX_VALUE, stats.getIitCount());
	}

	@Test
	public void testMinIntValue() {
		// Act
		stats.setIitCount(Integer.MIN_VALUE);

		// Assert
		assertEquals(Integer.MIN_VALUE, stats.getIitCount());
	}

	@Test
	public void testMultipleSetCalls() {
		// Act
		stats.setIitCount(5);
		stats.setIitCount(10);
		stats.setIitCount(15);

		// Assert
		assertEquals(15, stats.getIitCount());
	}

	@Test
	public void testAllFieldsSetAndGet() {
		// Act
		stats.setIitCount(8);
		stats.setMissedCount(15);
		stats.setUpcomingCount(22);
		stats.setPendingActionsCount(5);

		// Assert
		assertEquals(8, stats.getIitCount());
		assertEquals(15, stats.getMissedCount());
		assertEquals(22, stats.getUpcomingCount());
		assertEquals(5, stats.getPendingActionsCount());
	}

	@Test
	public void testObjectEquality() {
		// Arrange
		DashboardStats stats1 = new DashboardStats(8, 15, 22, 5);
		DashboardStats stats2 = new DashboardStats(8, 15, 22, 5);

		// Assert
		// Note: equals() should be implemented in DTO for proper equality testing
		assertEquals(stats1.getIitCount(), stats2.getIitCount());
	}

	@Test
	public void testObjectCopy() {
		// Arrange
		stats.setIitCount(8);
		stats.setMissedCount(15);
		stats.setUpcomingCount(22);
		stats.setPendingActionsCount(5);

		// Act
		DashboardStats copiedStats = new DashboardStats(
			stats.getIitCount(),
			stats.getMissedCount(),
			stats.getUpcomingCount(),
			stats.getPendingActionsCount()
		);

		// Assert
		assertEquals(stats.getIitCount(), copiedStats.getIitCount());
		assertEquals(stats.getMissedCount(), copiedStats.getMissedCount());
		assertEquals(stats.getUpcomingCount(), copiedStats.getUpcomingCount());
		assertEquals(stats.getPendingActionsCount(), copiedStats.getPendingActionsCount());
	}

	@Test
	public void testIntegerOverflow_Addition() {
		// Arrange
		DashboardStats stats1 = new DashboardStats(Integer.MAX_VALUE, 0, 0, 0);
		DashboardStats stats2 = new DashboardStats(1, 0, 0, 0);

		// Act & Assert
		// Note: Integer overflow is expected in Java
		assertNotEquals(stats1.getIitCount() + stats2.getIitCount(), Integer.MAX_VALUE + 1);
	}

	@Test
	public void testToString() {
		// Arrange
		stats.setIitCount(8);
		stats.setMissedCount(15);

		// Act
		String result = stats.toString();

		// Assert
		assertNotNull(result);
	}
}

