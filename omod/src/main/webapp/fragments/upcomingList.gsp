<%
    /**
     * Upcoming Appointments List Fragment
     * Displays patients with upcoming appointments in the next N days
     */
%>

<% if (upcomingPatientIds && upcomingPatientIds.size() > 0) { %>
    <table class="patient-table">
        <thead>
            <tr>
                <th>Patient ID</th>
                <th>Name</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <% upcomingPatientIds.each { patientId -> %>
                <%
                    def patient = org.openmrs.api.context.Context.getPatientService().getPatient(patientId)
                %>
                <tr class="success">
                    <td>${patient.patientId}</td>
                    <td>
                        <a href="${ui.pageLink('coreapps', 'clinicianfacing/patient', [patientId: patient.patientId])}">
                            ${patient.personName}
                        </a>
                    </td>
                    <td>
                        <span class="label label-success">Upcoming Appointment</span>
                    </td>
                    <td>
                        <div class="btn-group">
                            <a href="${ui.pageLink('coreapps', 'clinicianfacing/patient', [patientId: patient.patientId])}" class="btn btn-primary">
                                View Patient
                            </a>
                            <a href="${ui.pageLink('cds', 'cds', [tab: 'clientEffort', patientId: patient.patientId])}" class="btn btn-secondary">
                                View History
                            </a>
                        </div>
                    </td>
                </tr>
            <% } %>
        </tbody>
    </table>
<% } else { %>
    <div class="empty-state">
        <i class="icon-calendar"></i>
        <p>No upcoming appointments in the next ${lookbackDays} days.</p>
    </div>
<% } %>

