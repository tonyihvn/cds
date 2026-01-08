OpenMRS and this module (cds)

Overview
- OpenMRS is an open-source medical record platform designed for resource-constrained environments. It provides a core platform (OpenMRS Core) and a modular architecture that lets developers add features via modules.
- This repository contains an OpenMRS module named "cds" (Clinical Data System). It follows the standard OpenMRS module structure with two Maven submodules:
  - api: Business logic, services, DAOs, domain objects, and Liquibase changesets.
  - omod: Web layer, controllers, UI fragments, and module configuration for deployment in the OpenMRS webapp.

How OpenMRS modules work
- Modules are packaged as .omod files and installed into a running OpenMRS platform (via the Manage Modules page or by placing the .omod in the modules directory).
- The api submodule registers Spring beans (services and DAOs) and database changes via Liquibase.
- The omod submodule wires web controllers, UI fragments, and the module config.xml which declares the module’s metadata and extensions.

What’s inside this module
- API layer (api/):
  - org.openmrs.module.cds.api.ClinicalDataSystemService and its implementation provide the module’s service API.
  - org.openmrs.module.cds.api.dao.ClinicalDataSystemDao contains data-access logic.
  - Liquibase changes are defined in api/src/main/resources/liquibase.xml.
- Web layer (omod/):
  - Controllers under org.openmrs.module.cds.web.controller and fragments under org.openmrs.module.cds.fragment.controller.
  - UI resources in omod/src/main/webapp.
  - Module metadata in omod/src/main/resources/config.xml.

Build and run
1) Prerequisites
   - Java 8 or 11 (matching your OpenMRS Core version)
   - Maven 3.6+
   - An OpenMRS platform distribution compatible with the module’s declared version

2) Build the module
   - From the project root: mvn clean install
   - The .omod artifact will be produced under omod/target/ (e.g., cds-<version>.omod)

3) Install into OpenMRS
   - Copy the generated .omod into the OpenMRS modules directory (usually ~/.OpenMRS/modules) or upload via the OpenMRS Administration > Manage Modules page.
   - Restart OpenMRS if required.

Useful links
- OpenMRS: https://openmrs.org/
- Developer Guide: https://wiki.openmrs.org/display/docs/Developer+Guide
- Module Development: https://wiki.openmrs.org/display/docs/Creating+Modules
- REST API (if relevant): https://rest.openmrs.org/

Notes
- This README provides a brief orientation for developers who asked, “Do you know about OpenMRS?” If you’re new to OpenMRS, start with the Developer Guide above, then explore this module’s api and omod folders to see how services, DAOs, and web controllers are implemented.

CDS features added (service-only, no REST/controllers)
- New database table via Liquibase: cds_actions_table for internal actions tracking and HQ reporting workflow.
- Service API additions available via Context.getService(ClinicalDataSystemService.class):
  - getUpcomingAppointmentPatientIds(withinDays)
  - getMissedAppointmentPatientIds(lastDays)
  - getIITPatientIds(lookbackDays)
  - getClientEffort(patientId)
  - addCdsAction(CdsActionRecord), getPendingCdsActions(), updateCdsActionStatus(actionId, status)
- These use DAO-backed SQL against core OpenMRS tables (obs, encounter, patient, concept_name) and the new cds_actions_table. Build the module and install into an OpenMRS instance to use these programmatically from other module code or server-side logic.
