<%
    /**
     * Client Effort Fragment
     * Displays tracking history for a specific patient including verification status and comments
     */
%>

<% if (error) { %>
    <div class="alert alert-danger">
        ${error}
    </div>
<% } else if (clientEffortEntries && clientEffortEntries.size() > 0) { %>
    <h4>Tracking History for Patient ${patientId}</h4>

    <table class="patient-table">
        <thead>
            <tr>
                <th>Date</th>
                <th>Status</th>
                <th>Comments</th>
            </tr>
        </thead>
        <tbody>
            <% clientEffortEntries.each { entry -> %>
                <tr>
                    <td>
                        <% def sdf = new java.text.SimpleDateFormat("MMM dd, yyyy HH:mm") %>
                        ${sdf.format(entry.actionDate)}
                    </td>
                    <td>
                        <span class="label">${entry.status ?: 'N/A'}</span>
                    </td>
                    <td>
                        ${entry.comments ?: '-'}
                    </td>
                </tr>
            <% } %>
        </tbody>
    </table>
<% } else { %>
    <div class="empty-state">
        <i class="icon-file-text"></i>
        <p>No tracking history available for patient ${patientId}.</p>
    </div>
<% } %>

