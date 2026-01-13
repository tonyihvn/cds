<%

    ui.decorateWith("appui", "standardEmrPage", [title: "Patient CDS Dashboard", breadcrumbs: [[link: ui.pageLink("cds", "cds"), label: "CDS Dashboard"], [label: "Patient Dashboard"]]])
%>

${ ui.includeFragment("cds", "cdsdashboard") }

