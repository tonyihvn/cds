package org.openmrs.module.cds.fragment.controller;

import junit.framework.TestCase;
import org.openmrs.ui.framework.fragment.FragmentModel;

public class CdsFragmentControllerTest extends TestCase {
    private CdsFragmentController controller;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        controller = new CdsFragmentController();
    }

    public void testClientEffort_WithNullPatientId_SetsErrorAndNullEntries() {
        // Arrange
        FragmentModel model = new FragmentModel();

        // Act
        controller.clientEffort(model, null);

        // Assert
        assertNull("clientEffortEntries should be null for null patientId", model.getAttribute("clientEffortEntries"));
        assertEquals("Invalid patient ID", model.getAttribute("error"));
    }

    public void testClientEffort_WithNonPositivePatientId_SetsErrorAndNullEntries() {
        // Arrange
        FragmentModel modelZero = new FragmentModel();
        FragmentModel modelNegative = new FragmentModel();

        // Act
        controller.clientEffort(modelZero, 0);
        controller.clientEffort(modelNegative, -5);

        // Assert
        assertNull("clientEffortEntries should be null for zero patientId", modelZero.getAttribute("clientEffortEntries"));
        assertEquals("Invalid patient ID", modelZero.getAttribute("error"));

        assertNull("clientEffortEntries should be null for negative patientId", modelNegative.getAttribute("clientEffortEntries"));
        assertEquals("Invalid patient ID", modelNegative.getAttribute("error"));
    }
}