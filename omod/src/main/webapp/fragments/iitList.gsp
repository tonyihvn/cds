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

<% if (iitPatientIds && iitPatientIds.size() > 0) { %>
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
            <% iitPatientIds.each { patientId -> %>
                <%
                    def patient = org.openmrs.api.context.Context.getPatientService().getPatient(patientId)
                %>
                <tr class="danger">
                    <td>${patient.patientId}</td>
                    <td>
                        <a href="${ui.pageLink('coreapps', 'clinicianfacing/patient', [patientId: patient.patientId])}">
                            ${patient.personName}
                        </a>
                    </td>
                    <td>
                        <span class="label label-danger">Verge of IIT</span>
                    </td>
                    <td>
                        <div class="btn-group">
                            <button class="btn btn-primary" onclick="assignAction(${patient.patientId})">
                                Assign Action
                            </button>
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
        <i class="icon-check"></i>
        <p>No patients on verge of IIT in the last ${lookbackDays} days.</p>
    </div>
<% } %>

<script>
function assignAction(patientId) {
    // This would typically open a modal for assigning an action
    alert('Action assignment for patient ' + patientId + ' - feature to be implemented');
}
</script>

