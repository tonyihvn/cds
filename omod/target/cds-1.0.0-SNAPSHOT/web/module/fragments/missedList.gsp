<%
    /**
     * Missed Appointments List Fragment
     * Displays patients with missed appointments in the last N days
     * with EAC referral option if no EAC history exists
     */
    if (!binding.hasVariable('missedPatientIds')) {
        missedPatientIds = []
    }
    if (!binding.hasVariable('lookbackDays') || lookbackDays == null) {
        lookbackDays = 30
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

.patient-table tr.warning {
    background-color: #fff3cd;
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

.btn-warning {
    background-color: #ffc107;
    color: #333;
}

.btn-warning:hover {
    background-color: #e0a800;
}

.btn-danger {
    background-color: #dc3545;
    color: white;
}

.btn-danger:hover {
    background-color: #c82333;
}

.label {
    padding: 4px 8px;
    border-radius: 3px;
    font-size: 11px;
    font-weight: 600;
}

.label-warning {
    background-color: #ffc107;
    color: #333;
}
</style>

<% if (missedPatientIds && missedPatientIds.size() > 0) { %>
    <table class="patient-table">
        <thead>
            <tr>
                <th>PEPFAR ID</th>
                <th>Patient Name</th>
                <th>Age / Gender</th>
                <th>Missed Appointment Date</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <% missedPatientIds.each { patientId -> %>
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

                    // Get missed appointment date
                    def missedApptDate = 'N/A'
                    try {
                        def concept5096 = org.openmrs.api.context.Context.getConceptService().getConcept(5096)
                        def appointmentObs = obsService.getObservationsByPersonAndConcept(patient, concept5096)
                        if (appointmentObs && !appointmentObs.isEmpty()) {
                            missedApptDate = appointmentObs.last().getObsDatetime().format('yyyy-MM-dd')
                        }
                    } catch (Exception e) {
                        missedApptDate = 'Error'
                    }

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
                <% if (patient) { %>
                    <tr class="warning">
                        <td><strong>${pepfarId}</strong></td>
                        <td>
                            <a href="${ui.pageLink('coreapps', 'clinicianfacing/patient', [patientId: patient.patientId])}">
                                ${fullName}
                            </a>
                        </td>
                        <td>${patient.age} / ${patient.gender}</td>
                        <td>${missedApptDate}</td>
                        <td>
                            <span class="label label-warning">Missed Appointment</span>
                        </td>
                        <td>
                            <div class="btn-group">
                                <button class="btn btn-primary" onclick="viewPatientDashboard(${patient.patientId})" title="View complete CDS dashboard">
                                    📊 Dashboard
                                </button>
                                <% if (!hasEAC) { %>
                                    <button class="btn btn-danger" onclick="openEACForm('${patient.uuid}')" title="Open Enhanced Adherence Counselling form">
                                        📋 EAC Form
                                    </button>
                                <% } else { %>
                                    <button class="btn btn-warning" disabled title="EAC history exists">
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
            ✓ No patients with missed appointments in the last ${lookbackDays} days.
        </p>
    </div>
<% } %>

<script>
function viewPatientDashboard(patientId) {
    console.log('[CDS Missed List] Viewing CDS dashboard for patient:', patientId);
    // Navigate to patient dashboard
    window.location.href = '${ui.pageLink("cds", "cdsdashboard", ["patientId": "PATIENT_ID"])}'
                          .replace('PATIENT_ID', patientId);
}

function openEACForm(patientUuid) {
    console.log('[CDS Missed List] Opening EAC form for patient:', patientUuid);
    // Open EAC form with UUID-based URL
    var formUuid = 'd42c5cf6-8722-4f32-8767-ec8df0a40094'; // EAC form UUID
    var returnUrl = encodeURIComponent('http://localhost:8080/openmrs/coreapps/clinicianfacing/patient.page?patientId=' + patientUuid);
    window.location.href = 'http://localhost:8080/openmrs/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId=' + patientUuid + '&visitId=0&formUuid=' + formUuid + '&returnUrl=' + returnUrl;
}

function assignAction(patientId) {
    console.log('[CDS Missed List] Assigning action for patient:', patientId);
    alert('Action assignment for patient ' + patientId + ' - feature to be implemented');
}
</script>

