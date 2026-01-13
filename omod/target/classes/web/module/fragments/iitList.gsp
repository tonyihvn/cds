<%
    /**
     * IIT (Verge of Interruption in Treatment) List Fragment
     * Displays patients on the verge of IIT with ability to assign actions
     */

    // Provide safe defaults if the fragment model didn't include expected attributes
    if (!binding.hasVariable('iitPatientIds')) {
        iitPatientIds = []
    }
    if (!binding.hasVariable('lookbackDays') || lookbackDays == null) {
        lookbackDays = 27
    }
%>

<style>
.patient-table {
    width: 100%;
    border-collapse: collapse;
}

.patient-table th {
    background-color: #f5f5f5;
    padding: 12px;
    text-align: left;
    font-weight: 600;
    border-bottom: 2px solid #ddd;
}

.patient-table td {
    padding: 12px;
    border-bottom: 1px solid #eee;
}

.patient-table tr.danger {
    background-color: #f8d7da;
}

.btn-group {
    display: flex;
    gap: 5px;
}

.btn {
    padding: 6px 10px;
    border-radius: 3px;
    border: none;
    cursor: pointer;
    font-size: 12px;
}

.btn-primary {
    background-color: #007bff;
    color: white;
}

.btn-primary:hover {
    background-color: #0056b3;
}

.btn-secondary {
    background-color: #6c757d;
    color: white;
}

.btn-secondary:hover {
    background-color: #545b62;
}

.label {
    padding: 4px 8px;
    border-radius: 3px;
    font-size: 11px;
    font-weight: 600;
}

.label-danger {
    background-color: #dc3545;
    color: white;
}

.status-badge {
    background: #dc3545;
    color: white;
    padding: 5px 10px;
    border-radius: 3px;
}
</style>

<% if (iitPatientIds && iitPatientIds.size() > 0) { %>
    <table class="patient-table">
        <thead>
            <tr>
                <th>PEPFAR ID</th>
                <th>Patient Name</th>
                <th>Age / Gender</th>
                <th>Last Appointment</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <% iitPatientIds.each { patientId -> %>
                <%
                    def patientService = org.openmrs.api.context.Context.getPatientService()
                    def obsService = org.openmrs.api.context.Context.getObsService()
                    def patient = patientService.getPatient(patientId)

                    // Get PEPFAR ID (identifier_type_id = 4)
                    def pepfarId = 'N/A'
                    if (patient && patient.getIdentifiers()) {
                        patient.getIdentifiers().each { id ->
                            if (id.getIdentifierType() && id.getIdentifierType().getId() == 4) {
                                pepfarId = id.getIdentifier()
                            }
                        }
                    }

                    // Get patient names
                    def givenName = patient?.getPersonName()?.getGivenName() ?: ''
                    def familyName = patient?.getPersonName()?.getFamilyName() ?: ''
                    def fullName = givenName + ' ' + familyName

                    // Get last appointment date
                    def lastApptDate = 'N/A'
                    try {
                        def concept5096 = org.openmrs.api.context.Context.getConceptService().getConcept(5096)
                        def appointmentObs = obsService.getObservationsByPersonAndConcept(patient, concept5096)
                        if (appointmentObs && !appointmentObs.isEmpty()) {
                            lastApptDate = appointmentObs.last().getObsDatetime().format('yyyy-MM-dd')
                        }
                    } catch (Exception e) {
                        lastApptDate = 'Error'
                    }
                %>
                <% if (patient) { %>
                    <tr class="danger">
                        <td><strong>${pepfarId}</strong></td>
                        <td>
                            <a href="${ui.pageLink('coreapps', 'clinicianfacing/patient', [patientId: patient.patientId])}">
                                ${fullName}
                            </a>
                        </td>
                        <td>${patient.age} / ${patient.gender}</td>
                        <td>${lastApptDate}</td>
                        <td>
                            <span class="label label-danger">Verge of IIT</span>
                        </td>
                        <td>
                            <div class="btn-group">
                                <button class="btn btn-primary" onclick="viewPatientDashboard(${patient.patientId})" title="View complete CDS dashboard">
                                    📊 Dashboard
                                </button>
                                <%
                                    // Check if EAC history exists
                                    def hasEAC = false
                                    try {
                                        def eacConcept = org.openmrs.api.context.Context.getConceptService().getConcept(166097)
                                        def eacObs = obsService.getObservationsByPersonAndConcept(patient, eacConcept)
                                        hasEAC = eacObs && !eacObs.isEmpty()
                                    } catch (Exception e) {
                                        hasEAC = false
                                    }
                                %>
                                <% if (!hasEAC) { %>
                                    <button class="btn btn-secondary" onclick="openEACForm('${patient.uuid}')" title="Open Enhanced Adherence Counselling form">
                                        📋 EAC
                                    </button>
                                <% } else { %>
                                    <button class="btn btn-secondary" disabled title="EAC history exists">
                                        ✓ EAC Done
                                    </button>
                                <% } %>
                            </div>
                        </td>
                    </tr>
                <% } %>
            <% } %>
        </tbody>
    </table>
<% } else { %>
    <div class="empty-state">
        <p style="text-align: center; padding: 20px; color: #666;">
            ✓ No patients on verge of IIT in the last ${lookbackDays} days.
        </p>
    </div>
<% } %>

<script>
function viewPatientDashboard(patientId) {
    console.log('[CDS IIT List] Viewing CDS dashboard for patient:', patientId);
    // Navigate to patient dashboard
    window.location.href = '${ui.pageLink("cds", "cdsdashboard", ["patientId": "PATIENT_ID"])}'
                          .replace('PATIENT_ID', patientId);
}

function openEACForm(patientUuid) {
    console.log('[CDS IIT List] Opening EAC form for patient:', patientUuid);
    // Open EAC form with UUID-based URL
    var formUuid = 'd42c5cf6-8722-4f32-8767-ec8df0a40094'; // EAC form UUID
    var returnUrl = encodeURIComponent('http://localhost:8080/openmrs/coreapps/clinicianfacing/patient.page?patientId=' + patientUuid);
    window.location.href = 'http://localhost:8080/openmrs/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId=' + patientUuid + '&visitId=0&formUuid=' + formUuid + '&returnUrl=' + returnUrl;
}

function assignAction(patientId) {
    console.log('[CDS IIT List] Assigning action for patient:', patientId);
    // This would open a modal or navigate to action assignment
    alert('Action assignment for patient: ' + patientId);
}
</script>

<script>
function assignAction(patientId) {
    // This would typically open a modal for assigning an action
    alert('Action assignment for patient ' + patientId + ' - feature to be implemented');
}
</script>

