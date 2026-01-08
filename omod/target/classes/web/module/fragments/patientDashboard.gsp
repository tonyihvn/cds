<%
/*
Patient Dashboard Fragment
Displays comprehensive patient information including:
- PEPFAR ID and patient names
- Viral load status and next due date
- Current regimen and line
- Next appointment date
- Encounter history
- Pending actions
- EAC referral button
*/
%>

<style>
.patient-dashboard {
    background: #f5f5f5;
    padding: 20px;
    border-radius: 5px;
}

.patient-header {
    background: white;
    padding: 20px;
    border-radius: 5px;
    margin-bottom: 20px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.patient-info-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 15px;
    margin-bottom: 20px;
}

.info-card {
    background: white;
    padding: 15px;
    border-left: 4px solid #007bff;
    border-radius: 3px;
}

.info-label {
    font-size: 12px;
    color: #666;
    text-transform: uppercase;
    font-weight: 600;
    margin-bottom: 5px;
}

.info-value {
    font-size: 16px;
    color: #333;
    font-weight: 600;
}

.alert-danger {
    background: #f8d7da;
    border: 1px solid #f5c6cb;
    color: #721c24;
    padding: 12px;
    border-radius: 3px;
    margin-bottom: 15px;
}

.alert-success {
    background: #d4edda;
    border: 1px solid #c3e6cb;
    color: #155724;
    padding: 12px;
    border-radius: 3px;
    margin-bottom: 15px;
}

.btn-dashboard {
    padding: 8px 16px;
    margin: 5px;
    background: #007bff;
    color: white;
    border: none;
    border-radius: 3px;
    cursor: pointer;
    font-size: 14px;
}

.btn-dashboard:hover {
    background: #0056b3;
}

.btn-danger {
    background: #dc3545;
}

.btn-danger:hover {
    background: #c82333;
}

.btn-success {
    background: #28a745;
}

.btn-success:hover {
    background: #218838;
}

.encounters-table {
    width: 100%;
    border-collapse: collapse;
    background: white;
    margin-top: 15px;
}

.encounters-table th,
.encounters-table td {
    padding: 12px;
    text-align: left;
    border-bottom: 1px solid #ddd;
}

.encounters-table th {
    background: #f8f9fa;
    font-weight: 600;
    color: #333;
}

.encounters-table tr:hover {
    background: #f9f9f9;
}

.modal {
    display: none;
    position: fixed;
    z-index: 1000;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    background: rgba(0,0,0,0.5);
}

.modal-content {
    background: white;
    margin: 5% auto;
    padding: 30px;
    border-radius: 5px;
    width: 80%;
    max-width: 600px;
    box-shadow: 0 4px 8px rgba(0,0,0,0.2);
}

.close-modal {
    color: #aaa;
    float: right;
    font-size: 28px;
    font-weight: bold;
    cursor: pointer;
}

.close-modal:hover {
    color: black;
}

.form-group {
    margin-bottom: 15px;
}

.form-group label {
    display: block;
    font-weight: 600;
    margin-bottom: 5px;
    color: #333;
}

.form-group input,
.form-group textarea,
.form-group select {
    width: 100%;
    padding: 8px;
    border: 1px solid #ddd;
    border-radius: 3px;
    font-size: 14px;
}

.form-group textarea {
    resize: vertical;
    min-height: 80px;
}
</style>

<div class="patient-dashboard">
    <!-- Error Message -->
    <% if (error) { %>
        <div class="alert-danger">
            <strong>Error:</strong> ${error}
        </div>
    <% } %>

    <!-- Patient Header -->
    <div class="patient-header">
        <h2>Patient Dashboard</h2>
        <hr>
        <div class="patient-info-grid">
            <div class="info-card">
                <div class="info-label">PEPFAR ID</div>
                <div class="info-value">${pepfarId}</div>
            </div>
            <div class="info-card">
                <div class="info-label">Patient Name</div>
                <div class="info-value">${givenName} ${familyName}</div>
            </div>
            <div class="info-card">
                <div class="info-label">Age</div>
                <div class="info-value">${patient.age} years</div>
            </div>
            <div class="info-card">
                <div class="info-label">Gender</div>
                <div class="info-value">${patient.gender}</div>
            </div>
        </div>
    </div>

    <!-- Vital Load Status -->
    <div style="background: white; padding: 20px; border-radius: 5px; margin-bottom: 20px;">
        <h3>Viral Load Status</h3>
        <% if (viralLoadData.currentViralLoad && viralLoadData.currentViralLoad != 'N/A') { %>
            <% if (viralLoadData.currentViralLoad > 1000) { %>
                <div class="alert-danger">
                    <strong>Unsuppressed:</strong> Current VL = ${viralLoadData.currentViralLoad} c/ml
                </div>
            <% } else { %>
                <div class="alert-success">
                    <strong>Suppressed:</strong> Current VL = ${viralLoadData.currentViralLoad} c/ml
                </div>
            <% } %>
        <% } %>
        <div class="patient-info-grid">
            <div class="info-card">
                <div class="info-label">Current Viral Load</div>
                <div class="info-value">${viralLoadData.currentViralLoad ?: 'N/A'} c/ml</div>
            </div>
            <div class="info-card">
                <div class="info-label">Last VL Date</div>
                <div class="info-value">
                    <% if (viralLoadData.lastViralLoadDate) { %>
                        <fmt:formatDate value="${viralLoadData.lastViralLoadDate}" pattern="yyyy-MM-dd"/>
                    <% } else { %>
                        Not available
                    <% } %>
                </div>
            </div>
            <div class="info-card">
                <div class="info-label">Next VL Due</div>
                <div class="info-value">
                    <% if (viralLoadData.nextViralLoadDate) { %>
                        <fmt:formatDate value="${viralLoadData.nextViralLoadDate}" pattern="yyyy-MM-dd"/>
                    <% } else { %>
                        Not scheduled
                    <% } %>
                </div>
            </div>
        </div>
    </div>

    <!-- Regimen Information -->
    <div style="background: white; padding: 20px; border-radius: 5px; margin-bottom: 20px;">
        <h3>Current Treatment</h3>
        <div class="patient-info-grid">
            <div class="info-card">
                <div class="info-label">Regimen Line</div>
                <div class="info-value">${regimenData.currentLine ?: 'N/A'}</div>
            </div>
            <div class="info-card">
                <div class="info-label">Current Regimen</div>
                <div class="info-value">${regimenData.currentRegimen ?: 'N/A'}</div>
            </div>
            <div class="info-card">
                <div class="info-label">Last Pick Up</div>
                <div class="info-value">
                    <% if (regimenData.lastPickUpDate) { %>
                        <fmt:formatDate value="${regimenData.lastPickUpDate}" pattern="yyyy-MM-dd"/>
                    <% } else { %>
                        N/A
                    <% } %>
                </div>
            </div>
            <div class="info-card">
                <div class="info-label">Next Appointment</div>
                <div class="info-value">${nextAppointmentDate ?: 'Not scheduled'}</div>
            </div>
        </div>
    </div>

    <!-- Recent Encounters -->
    <div style="background: white; padding: 20px; border-radius: 5px; margin-bottom: 20px;">
        <h3>Recent Encounter History</h3>
        <% if (encounters && encounters.size() > 0) { %>
            <table class="encounters-table">
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Encounter Type</th>
                        <th>Provider</th>
                        <th>Location</th>
                    </tr>
                </thead>
                <tbody>
                    <% encounters.each { encounter -> %>
                        <tr>
                            <td><fmt:formatDate value="${encounter.encounterDatetime}" pattern="yyyy-MM-dd HH:mm"/></td>
                            <td>${encounter.encounterType.name}</td>
                            <td>${encounter.provider?.personName ?: 'N/A'}</td>
                            <td>${encounter.location?.name ?: 'N/A'}</td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        <% } else { %>
            <p>No encounters found</p>
        <% } %>
    </div>

    <!-- Actions Section -->
    <div style="background: white; padding: 20px; border-radius: 5px; margin-bottom: 20px;">
        <h3>Clinical Actions</h3>

        <!-- EAC Referral Button -->
        <% if (!hasEACHistory) { %>
            <button class="btn-dashboard btn-danger" onclick="openEACForm()">
                📋 Enhanced Adherence Counselling (EAC)
            </button>
        <% } else { %>
            <button class="btn-dashboard btn-success" disabled>
                ✓ EAC History Present
            </button>
        <% } %>

        <!-- Document Action Button -->
        <button class="btn-dashboard" onclick="openActionModal()">
            ➕ Document New Action
        </button>

        <!-- Send to API Button -->
        <button class="btn-dashboard" onclick="openAPIModal()">
            📤 Send Report to API
        </button>
    </div>

    <!-- Pending Actions -->
    <% if (pendingActions && pendingActions.size() > 0) { %>
        <div style="background: white; padding: 20px; border-radius: 5px; margin-bottom: 20px;">
            <h3>Pending Actions</h3>
            <table class="encounters-table">
                <thead>
                    <tr>
                        <th>Action</th>
                        <th>Assigned To</th>
                        <th>Status</th>
                        <th>Date Created</th>
                    </tr>
                </thead>
                <tbody>
                    <% pendingActions.each { action -> %>
                        <tr>
                            <td>${action.nextStepAction}</td>
                            <td>${action.assignedToUser ?: 'Unassigned'}</td>
                            <td>${action.status}</td>
                            <td><fmt:formatDate value="${action.dateCreated}" pattern="yyyy-MM-dd"/></td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    <% } %>
</div>

<!-- Modal for Document Action -->
<div id="actionModal" class="modal">
    <div class="modal-content">
        <span class="close-modal" onclick="closeActionModal()">&times;</span>
        <h2>Document New Action</h2>
        <form onsubmit="saveAction(event)">
            <div class="form-group">
                <label>Action Description</label>
                <textarea id="actionDescription" required placeholder="Describe the action to be taken..."></textarea>
            </div>
            <div class="form-group">
                <label>Assign To</label>
                <select id="assignedToUser" required>
                    <option value="">-- Select User --</option>
                    <% if (users) { %>
                        <% users.each { user -> %>
                            <option value="${user.userId}">${user.personName}</option>
                        <% } %>
                    <% } %>
                </select>
            </div>
            <div class="form-group">
                <label>Next Step</label>
                <input type="text" id="nextStep" required placeholder="Next step action...">
            </div>
            <div class="form-group">
                <label>Notes</label>
                <textarea id="actionNotes" placeholder="Additional notes..."></textarea>
            </div>
            <button type="submit" class="btn-dashboard btn-success">Save Action</button>
        </form>
    </div>
</div>

<!-- Modal for Send to API -->
<div id="apiModal" class="modal">
    <div class="modal-content">
        <span class="close-modal" onclick="closeAPIModal()">&times;</span>
        <h2>Send Report to API</h2>
        <form onsubmit="sendToAPI(event)">
            <div class="form-group">
                <label>API Endpoint</label>
                <input type="text" id="apiEndpoint" value="www.example.com/api" required>
            </div>
            <div class="form-group">
                <label>Report Notes</label>
                <textarea id="reportNotes" required placeholder="Include report recommendations..."></textarea>
            </div>
            <div class="form-group">
                <label>Include Attachments</label>
                <input type="checkbox" id="includeAttachments"> Attach viral load results and regimen info
            </div>
            <button type="submit" class="btn-dashboard btn-success">Send Report</button>
        </form>
    </div>
</div>

<script>
// Modal functions
function openActionModal() {
    document.getElementById('actionModal').style.display = 'block';
}

function closeActionModal() {
    document.getElementById('actionModal').style.display = 'none';
}

function openAPIModal() {
    document.getElementById('apiModal').style.display = 'block';
}

function closeAPIModal() {
    document.getElementById('apiModal').style.display = 'none';
}

function openEACForm() {
    // Navigate to EAC form
    // Assuming form ID 69 - adjust URL as needed
    window.location.href = '${ ui.pageLink("coreapps", "clinicianfacing/patient", [patientId: patient.patientId, formId: 69]) }';
}

function saveAction(event) {
    event.preventDefault();
    const actionData = {
        patientId: ${patient.patientId},
        nextStepAction: document.getElementById('nextStep').value,
        assignedToUserId: document.getElementById('assignedToUser').value,
        callReport: document.getElementById('actionDescription').value,
        status: 'PENDING'
    };

    console.log('[CDS PatientDashboard] Saving action:', actionData);

    // Make API call to save action
    fetch('${ ui.pageLink("cds", "patientDashboard", [action: "saveAction"]) }', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(actionData)
    }).then(response => {
        if (response.ok) {
            alert('Action saved successfully!');
            closeActionModal();
            location.reload();
        } else {
            alert('Error saving action');
        }
    }).catch(error => {
        console.error('[CDS PatientDashboard] Error:', error);
        alert('Error saving action: ' + error);
    });
}

function sendToAPI(event) {
    event.preventDefault();
    const reportData = {
        patientId: ${patient.patientId},
        pepfarId: '${pepfarId}',
        patientName: '${givenName} ${familyName}',
        viralLoad: '${viralLoadData.currentViralLoad}',
        regimen: '${regimenData.currentRegimen}',
        regimenLine: '${regimenData.currentLine}',
        nextAppointment: '${nextAppointmentDate}',
        notes: document.getElementById('reportNotes').value,
        apiEndpoint: document.getElementById('apiEndpoint').value
    };

    console.log('[CDS PatientDashboard] Sending report to API:', reportData);

    // Make API call to send report
    fetch('${ ui.pageLink("cds", "patientDashboard", [action: "sendReport"]) }', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(reportData)
    }).then(response => {
        if (response.ok) {
            alert('Report sent successfully!');
            closeAPIModal();
        } else {
            alert('Error sending report');
        }
    }).catch(error => {
        console.error('[CDS PatientDashboard] Error:', error);
        alert('Error sending report: ' + error);
    });
}

// Close modal when clicking outside of it
window.onclick = function(event) {
    const actionModal = document.getElementById('actionModal');
    const apiModal = document.getElementById('apiModal');
    if (event.target == actionModal) {
        actionModal.style.display = 'none';
    }
    if (event.target == apiModal) {
        apiModal.style.display = 'none';
    }
}
</script>

