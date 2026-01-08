<%
    /**
     * Pending Actions List Fragment
     * Displays all pending actions that need to be addressed by staff
     */
%>

<% if (pendingActions && pendingActions.size() > 0) { %>
    <table class="patient-table">
        <thead>
            <tr>
                <th>Action ID</th>
                <th>Patient ID</th>
                <th>Action Type</th>
                <th>Call Report / Notes</th>
                <th>Assigned To</th>
                <th>Created</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <% pendingActions.each { action -> %>
                <%
                    def patient = org.openmrs.api.context.Context.getPatientService().getPatient(action.patientId)
                    def assignedUser = null
                    if (action.assignedToUserId) {
                        assignedUser = org.openmrs.api.context.Context.getUserService().getUser(action.assignedToUserId)
                    }
                %>
                <tr>
                    <td>${action.actionId}</td>
                    <td>
                        <a href="${ui.pageLink('coreapps', 'clinicianfacing/patient', [patientId: patient.patientId])}">
                            ${patient.patientId}
                        </a>
                    </td>
                    <td>${action.nextStepAction ?: 'N/A'}</td>
                    <td>
                        <small>${action.callReport?.take(50) ?: 'N/A'}</small>
                    </td>
                    <td>
                        <% if (assignedUser) { %>
                            ${assignedUser.personName}
                        <% } else { %>
                            <em>Unassigned</em>
                        <% } %>
                    </td>
                    <td>
                        <% def sdf = new java.text.SimpleDateFormat("MMM dd, yyyy HH:mm") %>
                        ${sdf.format(action.dateCreated)}
                    </td>
                    <td>
                        <div class="btn-group">
                            <button class="btn btn-primary btn-sm" onclick="markComplete(${action.actionId})">
                                Mark Complete
                            </button>
                            <a href="${ui.pageLink('cds', 'cds', [tab: 'clientEffort', patientId: action.patientId])}" class="btn btn-secondary btn-sm">
                                Details
                            </a>
                        </div>
                    </td>
                </tr>
            <% } %>
        </tbody>
    </table>
<% } else { %>
    <div class="empty-state">
        <i class="icon-thumbs-up"></i>
        <p>No pending actions. All tasks are up to date!</p>
    </div>
<% } %>

<script>
function markComplete(actionId) {
    if (confirm('Mark this action as complete?')) {
        // This would typically make an AJAX call to update the action status
        alert('Marking action ' + actionId + ' as complete - feature to be implemented');
        location.reload();
    }
}
</script>

