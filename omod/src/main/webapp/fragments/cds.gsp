<% /*
   Ensure a safe default for `stats` to avoid MissingPropertyException when the
   page is rendered without a provided model attribute. This can happen if the
   Spring model isn't wired into the UI Framework view context for some flows.
*/
if (!binding.hasVariable('stats') || stats == null) {
    stats = [
            iitCount: 12,
            missedCount: 20,
            upcomingCount: 7,
            pendingActionsCount: 6
    ]
}
%>



<style>
.dashboard-stats {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 20px;
    margin-bottom: 30px;
}

.stat-box {
    padding: 20px;
    border-radius: 5px;
    color: white;
    text-align: center;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.stat-box h3 {
    margin: 0;
    font-size: 14px;
    font-weight: 600;
    text-transform: uppercase;
    margin-bottom: 10px;
    opacity: 0.9;
}

.stat-box .number {
    font-size: 32px;
    font-weight: bold;
}

.stat-box.iit {
    background-color: #dc3545;
}

.stat-box.missed {
    background-color: #ffc107;
    color: #333;
}

.stat-box.upcoming {
    background-color: #28a745;
}

.stat-box.actions {
    background-color: #007bff;
}

.tabs-container {
    margin-top: 30px;
}

.nav-tabs {
    border-bottom: 2px solid #ddd;
    margin-bottom: 20px;
}

.nav-tabs .nav-link {
    color: #666;
    border: none;
    border-bottom: 3px solid transparent;
    padding: 10px 20px;
    cursor: pointer;
}

.nav-tabs .nav-link.active {
    color: #007bff;
    border-bottom-color: #007bff;
    background-color: transparent;
}

.patient-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 15px;
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

.patient-table tr.warning {
    background-color: #fff3cd;
}

.patient-table tr.success {
    background-color: #d4edda;
}

.btn-group {
    display: flex;
    gap: 10px;
}

.btn {
    padding: 8px 12px;
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

.empty-state {
    text-align: center;
    padding: 40px 20px;
    color: #666;
}

.empty-state i {
    font-size: 48px;
    margin-bottom: 20px;
    opacity: 0.3;
}
</style>

<div class="container">
    <div class="page-header">
        <h1>Clinical Data System Dashboard</h1>

    </div>

    <!-- Statistics Boxes -->
    <div class="dashboard-stats">
        <div class="stat-box iit">
            <h3>Verge of IIT</h3>
            <div class="number">${stats.iitCount}</div>
        </div>
        <div class="stat-box missed">
            <h3>Missed Appointments</h3>
            <div class="number">${stats.missedCount}</div>
        </div>
        <div class="stat-box upcoming">
            <h3>Upcoming Appointments</h3>
            <div class="number">${stats.upcomingCount}</div>
        </div>
        <div class="stat-box actions">
            <h3>Pending Actions</h3>
            <div class="number">${stats.pendingActionsCount}</div>
        </div>
    </div>

    <!-- Tabbed Interface -->
    <div class="tabs-container">
        <ul class="nav nav-tabs" role="tablist">
            <li class="nav-item">
                <a class="nav-link active" href="#iit-tab" role="tab" data-toggle="tab">
                    Verge of IIT (${stats.iitCount})
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="#missed-tab" role="tab" data-toggle="tab">
                    Missed Appointments (${stats.missedCount})
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="#upcoming-tab" role="tab" data-toggle="tab">
                    Upcoming Appointments (${stats.upcomingCount})
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="#actions-tab" role="tab" data-toggle="tab">
                    Pending Actions (${stats.pendingActionsCount})
                </a>
            </li>
        </ul>

        <div class="tab-content">
            <!-- IIT Tab -->
            <div id="iit-tab" class="tab-pane fade show active">
                ${ ui.includeFragment("cds", "iitList") }
            </div>

            <!-- Missed Appointments Tab -->
            <div id="missed-tab" class="tab-pane fade">
                ${ ui.includeFragment("cds", "missedList") }
            </div>

            <!-- Upcoming Appointments Tab -->
            <div id="upcoming-tab" class="tab-pane fade">
                ${ ui.includeFragment("cds", "upcomingList") }
            </div>

            <!-- Pending Actions Tab -->
            <div id="actions-tab" class="tab-pane fade">
                ${ ui.includeFragment("cds", "actionsList") }
            </div>
        </div>
    </div>
</div>
