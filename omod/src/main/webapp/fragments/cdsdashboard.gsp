<!-- DataTables CSS -->
<link rel="stylesheet" href="https://cdn.datatables.net/1.11.5/css/jquery.dataTables.min.css">
<link rel="stylesheet" href="https://cdn.datatables.net/buttons/2.2.2/css/buttons.dataTables.min.css">

<!-- jQuery (if not already included) -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<!-- DataTables JS -->
<script src="https://cdn.datatables.net/1.11.5/js/jquery.dataTables.min.js"></script>
<script src="https://cdn.datatables.net/buttons/2.2.2/js/dataTables.buttons.min.js"></script>

<%
/**
 * Patient Dashboard Fragment
 * Displays comprehensive patient information
 *
 * All data is provided by PatientDashboardFragmentController
 * No initialization code needed - just rendering
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
    <!-- Patient Header -->
    <div class="patient-header">
        <h2>Clinical Decision Support | Patient Dashboard</h2>
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
                <div class="info-value">${patient?.age ?: 'N/A'} years</div>
            </div>
            <div class="info-card">
                <div class="info-label">Gender</div>
                <div class="info-value">${patient?.gender ?: 'N/A'}</div>
            </div>
        </div>
    </div>

    <!-- Vital Load Status -->
    <div style="background: white; padding: 20px; border-radius: 5px; margin-bottom: 20px;">
        <h3>Viral Load Status</h3>
        <% if (viralLoadData.currentViralLoad && viralLoadData.currentViralLoad != 'N/A') { %>
            <% if (viralLoadData.currentViralLoad > 20) { %>
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
                    ${viralLoadData?.lastViralLoadDate ?: 'Not available'}
                </div>
            </div>
            <div class="info-card">
                <div class="info-label">Next VL Due</div>
                <div class="info-value">
                    ${viralLoadData?.nextViralLoadDate ?: 'Not scheduled'}
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
                    ${regimenData?.lastPickUpDate ?: 'N/A'}
                </div>
            </div>
                <div class="info-label">Next Appointment</div>
                <div class="info-value">${nextAppointmentDate ?: 'Not scheduled'}</div>
            </div>
        </div>
    </div>    <!-- Recent Encounters -->
    <div style="background: white; padding: 20px; border-radius: 5px; margin-bottom: 20px;">
        <h3>Recent Encounter History</h3>
        <% if (encounters && encounters.size() > 0) { %>
            <table id="encountersTable" class="encounters-table" style="width:100%">
                <thead>
                    <tr>
                        <th>Date <input type="text" class="column-search" placeholder="Search date"></th>
                        <th>Encounter Type <input type="text" class="column-search" placeholder="Search type"></th>
                        <th>Location <input type="text" class="column-search" placeholder="Search location"></th>
                    </tr>
                </thead>
                <tbody>
                    <% encounters.each { encounter -> %>
                        <tr>
                            <td>
                                <%
                                    def dateStr = 'N/A'
                                    if (encounter?.encounterDatetime) {
                                        def sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")
                                        dateStr = sdf.format(encounter.encounterDatetime)
                                    }
                                %>
                                ${dateStr}
                            </td>
                            <td>${encounter?.encounterType?.name ?: 'N/A'}</td>
                            <td>${encounter?.location?.name ?: 'N/A'}</td>
                        </tr>
                    <% } %>
                </tbody>
            </table>

            <script>
                var jq = jQuery;
            jq(document).ready(function() {
                var table = jq('#encountersTable').DataTable({
                    "lengthMenu": [[5, 10, 25, 50, -1], [5, 10, 25, 50, "All"]],
                    "pageLength": 10,
                    "order": [[0, 'desc']]
                });

                // Add column search functionality
                jq('#encountersTable thead tr:nth-child(2) th').each(function(i) {
                    var title = jq('#encountersTable thead th').eq(jq(this).index()).text();
                    jq(this).html('<input type="text" class="column-search" placeholder="Search ' + title + '" />');
                });

                jq('#encountersTable thead').on('keyup', '.column-search', function() {
                    table.column(jq(this).parent().index() + ':visible')
                        .search(this.value)
                        .draw();
                });
            });
            </script>
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

        <!-- Tracking and Termination Button -->
        <button class="btn-dashboard" onclick="openTrackingAndTerminationForm()">
            📍 Client Tracking and Termination
        </button>

        <!-- Send to API Button -->
        <button class="btn-dashboard" onclick="openAPIModal()">
            📤 Send Report to API
        </button>
    </div>

    <!-- Documented Actions Section -->
    <% if (documentedActions && documentedActions.size() > 0) { %>
        <div style="background: white; padding: 20px; border-radius: 5px; margin-bottom: 20px;">
            <h3>Documented Actions</h3>
            <table id="documentedActionsTable" class="encounters-table" style="width:100%">
                <thead>
                    <tr>
                        <th>Date <input type="text" class="column-search" placeholder="Search date"></th>
                        <th>Action <input type="text" class="column-search" placeholder="Search action"></th>
                        <th>Assigned To <input type="text" class="column-search" placeholder="Search user"></th>
                        <th>Next Step <input type="text" class="column-search" placeholder="Search next step"></th>
                        <th>Status <input type="text" class="column-search" placeholder="Search status"></th>
                    </tr>
                </thead>
                <tbody>
                    <% documentedActions.each { action -> %>
                        <tr>
                            <td>
                                <% if (action?.dateCreated) { %>
                                    <g:formatDate date="${action.dateCreated}" format="yyyy-MM-dd HH:mm"/>
                                <% } else { %>
                                    N/A
                                <% } %>
                            </td>
                            <td>${action?.callReport ?: 'N/A'}</td>
                            <td>${action?.assignedToUserName ?: 'N/A'}</td>
                            <td>${action?.nextStepAction ?: 'N/A'}</td>
                            <td><span style="padding: 5px 10px; border-radius: 3px; background: #007bff; color: white;">${action?.status ?: 'PENDING'}</span></td>
                        </tr>
                    <% } %>
                </tbody>
            </table>

            <script>
                var jq = jQuery;
                jq(document).ready(function() {
                    if (jq('#documentedActionsTable').length) {
                        var table = jq('#documentedActionsTable').DataTable({
                            "lengthMenu": [[5, 10, 25, 50, -1], [5, 10, 25, 50, "All"]],
                            "pageLength": 10,
                            "order": [[0, 'desc']]
                        });

                        // Add column search functionality
                        jq('#documentedActionsTable thead').on('keyup', '.column-search', function() {
                            table.column(jq(this).parent().index())
                                .search(this.value)
                                .draw();
                        });
                    }
                });
            </script>
        </div>
    <% } %>

    <!-- Past Tracking Efforts Section -->
    <% if (trackingEfforts && trackingEfforts.size() > 0) { %>
        <div style="background: white; padding: 20px; border-radius: 5px; margin-bottom: 20px;">
            <h3>Client Tracking History</h3>
            <table class="table table-bordered table-striped" id="trackingEffortsTable">
                <thead>
                <tr>
                    <g:each in="${trackingTable.headers}" var="h">
                        <th>${h}</th>
                    </g:each>
                </tr>
                </thead>
                <tbody>
                <g:each in="${trackingTable.rows}" var="row">
                    <tr>
                        <g:each in="${trackingTable.headers}" var="h">
                            <td>
                                <g:if test="${h == 'Encounter Date'}">
                                    <g:formatDate date="${row[h]}" format="yyyy-MM-dd"/>
                                </g:if>
                                <g:else>
                                    ${row[h]}
                                </g:else>
                            </td>
                        </g:each>
                    </tr>
                </g:each>
                </tbody>
            </table>


            <script>
                var jq = jQuery;
                jq(document).ready(function() {
                    if (jq('#trackingEffortsTable').length) {
                        var table = jq('#trackingEffortsTable').DataTable({
                            "lengthMenu": [[5, 10, 25, 50, -1], [5, 10, 25, 50, "All"]],
                            "pageLength": 10,
                            "order": [[0, 'desc']]
                        });

                        // Add column search functionality
                        jq('#trackingEffortsTable thead').on('keyup', '.column-search', function() {
                            table.column(jq(this).parent().index())
                                .search(this.value)
                                .draw();
                        });
                    }
                });
            </script>
        </div>
    <% } %>

    <!-- Pending Actions -->
    <% if (pendingActions && pendingActions.size() > 0) { %>
        <div style="background: white; padding: 20px; border-radius: 5px; margin-bottom: 20px;">
            <h3>Pending Actions</h3>
            <table id="pendingActionsTable" class="encounters-table" style="width:100%">
                <thead>
                    <tr>
                        <th>Action <input type="text" class="column-search" placeholder="Search action" style="width:100%"></th>
                        <th>Assigned To <input type="text" class="column-search" placeholder="Search user" style="width:100%"></th>
                        <th>Status <input type="text" class="column-search" placeholder="Search status" style="width:100%"></th>
                        <th>Date <input type="text" class="column-search" placeholder="Search date" style="width:100%"></th>
                    </tr>
                </thead>
                <tbody>
                    <% pendingActions.each { action -> %>
                        <tr>
                            <td>${action.nextStepAction}</td>
                            <td>${action.assignedToUser ?: 'Unassigned'}</td>
                            <td>${action.status}</td>
                            <td><g:formatDate date="${action?.dateCreated}" format="yyyy-MM-dd"/></td>
                        </tr>
                    <% } %>
                </tbody>
            </table>

            <script>
                var jq = jQuery;
            jq(document).ready(function() {
                if (jq('#pendingActionsTable').length) {
                    var table = jq('#pendingActionsTable').DataTable({
                        "lengthMenu": [[5, 10, 25, 50, -1], [5, 10, 25, 50, "All"]],
                        "pageLength": 10
                    });

                    // Add column search functionality
                    jq('#pendingActionsTable thead').on('keyup', '.column-search', function() {
                        table.column(jq(this).parent().index())
                            .search(this.value)
                            .draw();
                    });
                }
            });
            </script>
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

<!-- Modal for Tracking and Termination -->
<div id="trackingModal" class="modal">
    <div class="modal-content">
        <span class="close-modal" onclick="closeTrackingModal()">&times;</span>
        <h2>Client Tracking and Termination</h2>
        <form onsubmit="saveTracking(event)">
            <div class="form-group">
                <label>Verification Type</label>
                <select id="verificationType" required>
                    <option value="">-- Select Type --</option>
                    <option value="Client Verification">Client Verification</option>
                    <option value="Records Verification">Records Verification</option>
                    <option value="Lost to Follow Up">Lost to Follow Up</option>
                    <option value="Transferred Out">Transferred Out</option>
                    <option value="Terminated">Terminated</option>
                </select>
            </div>
            <div class="form-group">
                <label>Contact Method</label>
                <select id="contactMethod" required>
                    <option value="">-- Select Method --</option>
                    <option value="Phone Call">Phone Call</option>
                    <option value="Home Visit">Home Visit</option>
                    <option value="Contact via CHW">Contact via CHW</option>
                    <option value="Community Outreach">Community Outreach</option>
                    <option value="Records Review">Records Review</option>
                </select>
            </div>
            <div class="form-group">
                <label>Attempted Contact Date</label>
                <input type="date" id="attemptedContactDate" required>
            </div>
            <div class="form-group">
                <label>Status</label>
                <select id="trackingStatus" required>
                    <option value="">-- Select Status --</option>
                    <option value="Ongoing">Ongoing</option>
                    <option value="Discontinued">Discontinued</option>
                    <option value="Records Verified">Records Verified</option>
                    <option value="Successfully Located">Successfully Located</option>
                    <option value="Unable to Locate">Unable to Locate</option>
                </select>
            </div>
            <div class="form-group">
                <label>Outcome</label>
                <textarea id="trackingOutcome" required placeholder="Document the outcome of this tracking effort..."></textarea>
            </div>
            <div class="form-group">
                <label>Notes</label>
                <textarea id="trackingNotes" placeholder="Additional notes..."></textarea>
            </div>
            <button type="submit" class="btn-dashboard btn-success">Save Tracking Record</button>
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
var jq = jQuery;

// Debug logging
console.log('[CDS Dashboard] Viral Load Data:', {
    currentViralLoad: '${viralLoadData?.currentViralLoad ?: 'N/A'}',
    lastViralLoadDate: '${viralLoadData?.lastViralLoadDate ?: 'N/A'}',
    nextViralLoadDate: '${viralLoadData?.nextViralLoadDate ?: 'N/A'}'
});

console.log('[CDS Dashboard] Regimen Data:', {
    currentLine: '${regimenData?.currentLine ?: 'N/A'}',
    currentRegimen: '${regimenData?.currentRegimen ?: 'N/A'}',
    lastPickUpDate: '${regimenData?.lastPickUpDate ?: 'N/A'}'
});

console.log('[CDS Dashboard] Encounters Count:', ${encounters?.size() ?: 0});

// Store patient ID and UUID for JavaScript functions
var currentPatientId = ${patientId ?: 0};
var currentPatientUuid = '${patientUuid ?: ''}';
var eacFormUrl = '${eacFormUrl ?: ''}';

console.log('[CDS PatientDashboard] Patient ID in script: ' + currentPatientId);
console.log('[CDS PatientDashboard] Patient UUID in script: ' + currentPatientUuid);
console.log('[CDS PatientDashboard] EAC Form URL: ' + eacFormUrl);

// Modal functions
function openActionModal() {
    document.getElementById('actionModal').style.display = 'block';
}

function closeActionModal() {
    document.getElementById('actionModal').style.display = 'none';
}

function openTrackingModal() {
    document.getElementById('trackingModal').style.display = 'block';
}

function closeTrackingModal() {
    document.getElementById('trackingModal').style.display = 'none';
}

function openAPIModal() {
    document.getElementById('apiModal').style.display = 'block';
}

function closeAPIModal() {
    document.getElementById('apiModal').style.display = 'none';
}

function openEACForm() {
    // Navigate to EAC form with UUID-based URL
    if (currentPatientUuid && currentPatientUuid.length > 0) {
        console.log('[CDS PatientDashboard] Opening EAC form for patient:', currentPatientUuid);
        var formUuid = 'd42c5cf6-8722-4f32-8767-ec8df0a40094'; // EAC form UUID
        var returnUrl = encodeURIComponent('http://localhost:8080/openmrs/coreapps/clinicianfacing/patient.page?patientId=' + currentPatientUuid);
        window.location.href = 'http://localhost:8080/openmrs/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId=' + currentPatientUuid + '&visitId=0&formUuid=' + formUuid + '&returnUrl=' + returnUrl;
    } else {
        alert('Patient UUID is not available');
    }
}

function openTrackingAndTerminationForm() {
    // Navigate to Client Tracking and Termination form with UUID-based URL
    if (currentPatientUuid && currentPatientUuid.length > 0) {
        console.log('[CDS PatientDashboard] Opening Tracking and Termination form for patient:', currentPatientUuid);
        var formUuid = '5fbc99be-9aeb-4f94-85b0-b2fae88a0ced'; // Client Tracking and Termination form UUID
        var returnUrl = encodeURIComponent('/openmrs/coreapps/clinicianfacing/patient.page?patientId=' + currentPatientUuid);
        window.location.href = '/openmrs/htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId=' + currentPatientUuid + '&visitId=0&formUuid=' + formUuid + '&returnUrl=' + returnUrl;
    } else {
        alert('Patient UUID is not available');
    }
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

function saveAction(event) {
    event.preventDefault();
    const actionData = {
        patientId: currentPatientId,
        nextStepAction: document.getElementById('nextStep').value,
        assignedToUserId: document.getElementById('assignedToUser').value,
        callReport: document.getElementById('actionDescription').value,
        status: 'PENDING'
    };

    console.log('[CDS PatientDashboard] Saving action:', actionData);

    // Make API call to save action
    fetch('${ ui.pageLink("cds", "cdsdashboard", [action: "saveAction"]) }', {
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

function saveTracking(event) {
    event.preventDefault();
    const trackingData = {
        patientId: currentPatientId,
        verificationType: document.getElementById('verificationType').value,
        contactMethod: document.getElementById('contactMethod').value,
        attemptedContactDate: document.getElementById('attemptedContactDate').value,
        status: document.getElementById('trackingStatus').value,
        outcome: document.getElementById('trackingOutcome').value,
        notes: document.getElementById('trackingNotes').value
    };

    console.log('[CDS PatientDashboard] Saving tracking record:', trackingData);

    // Make API call to save tracking record
    fetch('${ ui.pageLink("cds", "cdsdashboard", [action: "saveTracking"]) }', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(trackingData)
    }).then(response => {
        if (response.ok) {
            alert('Tracking record saved successfully!');
            closeTrackingModal();
            location.reload();
        } else {
            alert('Error saving tracking record');
        }
    }).catch(error => {
        console.error('[CDS PatientDashboard] Error:', error);
        alert('Error saving tracking record: ' + error);
    });
}

function sendToAPI(event) {
    event.preventDefault();
    const reportData = {
        patientId: currentPatientId,
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
    fetch('${ ui.pageLink("cds", "cdsdashboard", [action: "sendReport"]) }', {
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

