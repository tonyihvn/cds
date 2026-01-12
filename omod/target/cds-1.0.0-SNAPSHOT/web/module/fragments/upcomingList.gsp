<%
    /**
     * Upcoming Appointments List Fragment
     * Displays patients with upcoming appointments in the next N days
     */
    if (!binding.hasVariable('upcomingPatientIds')) {
        upcomingPatientIds = []
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

.patient-table tr.success {
    background-color: #d4edda;
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

.btn-success {
    background-color: #28a745;
    color: white;
}

.btn-success:hover {
    background-color: #218838;
}

.label {
    padding: 4px 8px;
    border-radius: 3px;
    font-size: 11px;
    font-weight: 600;
}

.label-success {
    background-color: #28a745;
    color: white;
}
</style>

<% if (upcomingPatientIds && upcomingPatientIds.size() > 0) { %>
    <table class="patient-table">
        <thead>
            <tr>
                <th>PEPFAR ID</th>
                <th>Patient Name</th>
                <th>Age / Gender</th>
                <th>Appointment Date</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <% upcomingPatientIds.each { patientId -> %>
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

                    // Get upcoming appointment date
                    def appointmentDate = 'N/A'
                    try {
                        def concept5096 = org.openmrs.api.context.Context.getConceptService().getConcept(5096)
                        def appointmentObs = obsService.getObservationsByPersonAndConcept(patient, concept5096)
                        if (appointmentObs && !appointmentObs.isEmpty()) {
                            appointmentObs.each { obs ->
                                if (appointmentDate == 'N/A' && obs.getValueDatetime() != null && obs.getValueDatetime().after(new Date())) {
                                    appointmentDate = obs.getValueDatetime().format('yyyy-MM-dd')
                                }
                            }
                        }
                    } catch (Exception e) {
                        appointmentDate = 'Error'
                    }
                %>
                <% if (patient) { %>
                    <tr class="success">
                        <td><strong>${pepfarId}</strong></td>
                        <td>
                            <a href="${ui.pageLink('coreapps', 'clinicianfacing/patient', [patientId: patient.patientId])}">
                                ${fullName}
                            </a>
                        </td>
                        <td>${patient.age} / ${patient.gender}</td>
                        <td>${appointmentDate}</td>
                        <td>
                            <span class="label label-success">Upcoming Appointment</span>
                        </td>
                        <td>
                            <div class="btn-group">
                                <button class="btn btn-primary" onclick="viewPatientDashboard(${patient.patientId})" title="View complete CDS dashboard">
                                    📊 Dashboard
                                </button>
                                <a href="${ui.pageLink('coreapps', 'clinicianfacing/patient', [patientId: patient.patientId])}" class="btn btn-success">
                                    👤 View Patient
                                </a>
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
            📅 No upcoming appointments in the next ${lookbackDays} days.
        </p>
    </div>
<% } %>

<script>
function viewPatientDashboard(patientId) {
    console.log('[CDS Upcoming List] Viewing CDS dashboard for patient:', patientId);
    // Navigate to patient dashboard
    window.location.href = '${ui.pageLink("cds", "patientDashboard", ["patientId": "PATIENT_ID"])}'
                          .replace('PATIENT_ID', patientId);
}
</script>

