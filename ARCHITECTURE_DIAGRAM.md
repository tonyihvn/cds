# Architecture Diagram - CDS Dashboard Statistics Fix

## Before Fix (ERROR STATE)

```
┌─────────────────────────────────────────────────────────────┐
│                  Browser Request                            │
│         GET /module/cds/cds.form                            │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│         Spring DispatcherServlet                            │
│                                                              │
│  Maps to ClinicalDataSystemController.onGet()              │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│    ClinicalDataSystemController                            │
│                                                              │
│  @ModelAttribute methods:                                   │
│  ✅ getUsers() → "users" attribute added to Model          │
│                                                              │
│  ❌ NO getDashboardStats() method!                          │
│     (stats attribute NOT added to Model)                    │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│         Spring Renders View                                 │
│         /module/cds/cds.gsp                                 │
│                                                              │
│  Available in model:                                        │
│  ✅ users                                                   │
│  ❌ stats (MISSING!)                                        │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│         GSP Template Execution                              │
│                                                              │
│  Line 157: <div>${stats.iitCount}</div>                    │
│                                                              │
│  ERROR:                                                      │
│  groovy.lang.MissingPropertyException:                      │
│  No such property: stats                                    │
│                                                              │
│  ❌ Dashboard FAILS to load                                 │
└─────────────────────────────────────────────────────────────┘
```

---

## After Fix (WORKING STATE)

```
┌─────────────────────────────────────────────────────────────┐
│                  Browser Request                            │
│         GET /module/cds/cds.form                            │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│         Spring DispatcherServlet                            │
│                                                              │
│  Maps to ClinicalDataSystemController.onGet()              │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│    ClinicalDataSystemController                            │
│                                                              │
│  @Autowired ClinicalDataSystemService cdsService;          │
│                                                              │
│  @ModelAttribute methods (called BEFORE handler):          │
│  ✅ getUsers() → "users" attribute added to Model          │
│  ✅ getDashboardStats() → "stats" attribute added          │
│     to Model with DashboardStats object                    │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ├─► DashboardStats DTO Creation
                     │
                     │   cdsService.getIITPatientIds(90)
                     │   ↓ SQL Query ↓ Database
                     │   [Returns list of patient IDs]
                     │   size() = 5
                     │
                     │   cdsService.getMissedAppointmentPatientIds(30)
                     │   ↓ SQL Query ↓ Database
                     │   [Returns list of patient IDs]
                     │   size() = 12
                     │
                     │   cdsService.getUpcomingAppointmentPatientIds(30)
                     │   ↓ SQL Query ↓ Database
                     │   [Returns list of patient IDs]
                     │   size() = 23
                     │
                     │   cdsService.getPendingCdsActions()
                     │   ↓ SQL Query ↓ Database
                     │   [Returns list of action records]
                     │   size() = 3
                     │
                     │   new DashboardStats(5, 12, 23, 3)
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│         Spring Renders View                                 │
│         /module/cds/cds.gsp                                 │
│                                                              │
│  Available in model:                                        │
│  ✅ users                                                   │
│  ✅ stats (NOW AVAILABLE!)                                  │
│    - iitCount = 5                                           │
│    - missedCount = 12                                       │
│    - upcomingCount = 23                                     │
│    - pendingActionsCount = 3                                │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│         GSP Template Execution                              │
│                                                              │
│  Line 157: <div>${stats.iitCount}</div>                    │
│            Resolves to: <div>5</div> ✅                     │
│                                                              │
│  Line 161: <div>${stats.missedCount}</div>                 │
│            Resolves to: <div>12</div> ✅                    │
│                                                              │
│  Line 165: <div>${stats.upcomingCount}</div>               │
│            Resolves to: <div>23</div> ✅                    │
│                                                              │
│  Line 169: <div>${stats.pendingActionsCount}</div>         │
│            Resolves to: <div>3</div> ✅                     │
│                                                              │
│  ✅ Dashboard LOADS successfully!                           │
└─────────────────────────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│         HTML Response to Browser                            │
│                                                              │
│  Dashboard displays with statistics boxes:                 │
│  ┌─────────────────────────────┐                           │
│  │ Verge of IIT                │                           │
│  │        5                    │                           │
│  └─────────────────────────────┘                           │
│  ┌─────────────────────────────┐                           │
│  │ Missed Appointments         │                           │
│  │        12                   │                           │
│  └─────────────────────────────┘                           │
│  ┌─────────────────────────────┐                           │
│  │ Upcoming Appointments       │                           │
│  │        23                   │                           │
│  └─────────────────────────────┘                           │
│  ┌─────────────────────────────┐                           │
│  │ Pending Actions             │                           │
│  │        3                     │                           │
│  └─────────────────────────────┘                           │
│                                                              │
│  ✅ No errors, data displayed correctly!                    │
└─────────────────────────────────────────────────────────────┘
```

---

## Component Interactions

```
┌──────────────────────────────────────────────────────────────────┐
│                      Web Tier (OMOD)                             │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │  ClinicalDataSystemController                             │  │
│  │  ┌──────────────────────────────────────────────────────┐ │  │
│  │  │  @ModelAttribute("stats")                            │ │  │
│  │  │  getDashboardStats() {                               │ │  │
│  │  │    → cdsService.getIITPatientIds(90)                 │ │  │
│  │  │    → cdsService.getMissedAppointmentPatientIds(30)   │ │  │
│  │  │    → cdsService.getUpcomingAppointmentPatientIds(30) │ │  │
│  │  │    → cdsService.getPendingCdsActions()               │ │  │
│  │  │    → new DashboardStats(...)                         │ │  │
│  │  │  }                                                   │ │  │
│  │  └──────────────────────────────────────────────────────┘ │  │
│  └────────────┬───────────────────────────────────────────────┘  │
│               │                                                   │
└───────────────┼───────────────────────────────────────────────────┘
                │
                │ calls
                │
                ▼
┌──────────────────────────────────────────────────────────────────┐
│                   API/Service Tier (API)                         │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │  ClinicalDataSystemService (interface)                    │  │
│  │  ┌──────────────────────────────────────────────────────┐ │  │
│  │  │  getIITPatientIds(int lookbackDays)                  │ │  │
│  │  │  getMissedAppointmentPatientIds(int lastDays)        │ │  │
│  │  │  getUpcomingAppointmentPatientIds(int withinDays)    │ │  │
│  │  │  getPendingCdsActions()                              │ │  │
│  │  └──────────────────────────────────────────────────────┘ │  │
│  └────────────┬───────────────────────────────────────────────┘  │
│  ┌────────────┼───────────────────────────────────────────────┐  │
│  │  ClinicalDataSystemServiceImpl                            │  │
│  │  ┌──────────────────────────────────────────────────────┐ │  │
│  │  │  Implements above interface                          │ │  │
│  │  │  Uses DAO layer to query database                    │ │  │
│  │  └──────────────────────────────────────────────────────┘ │  │
│  └────────────┬───────────────────────────────────────────────┘  │
│               │                                                   │
└───────────────┼───────────────────────────────────────────────────┘
                │
                │ queries
                │
                ▼
┌──────────────────────────────────────────────────────────────────┐
│                    Persistence Tier (DAO)                        │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │  Hibernate/DAO Layer                                       │  │
│  │  ┌──────────────────────────────────────────────────────┐ │  │
│  │  │  Executes SQL queries against OpenMRS database      │ │  │
│  │  └──────────────────────────────────────────────────────┘ │  │
│  └────────────┬───────────────────────────────────────────────┘  │
│               │                                                   │
└───────────────┼───────────────────────────────────────────────────┘
                │
                │ retrieves data from
                │
                ▼
┌──────────────────────────────────────────────────────────────────┐
│                      OpenMRS Database                            │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │  Patient table                                             │  │
│  │  Appointment table                                         │  │
│  │  Tracking form data                                        │  │
│  │  CDS actions table                                         │  │
│  └────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────┘
```

---

## Data Flow

```
Controller Method Execution Timeline:

1. HTTP Request arrives
   └─► Spring DispatcherServlet

2. Route to handler method
   └─► ClinicalDataSystemController.onGet()

3. BEFORE handler executes, invoke @ModelAttribute methods:
   
   ├─► getUsers()
   │   ├─► userService.getAllUsers()
   │   ├─► Returns: List<User>
   │   └─► Added to model as "users"
   │
   └─► getDashboardStats()  ← NEW METHOD
       ├─► cdsService.getIITPatientIds(90)
       │   ├─► Queries database
       │   ├─► Returns: List<Integer> [patient IDs]
       │   └─► .size() = 5
       │
       ├─► cdsService.getMissedAppointmentPatientIds(30)
       │   ├─► Queries database
       │   ├─► Returns: List<Integer> [patient IDs]
       │   └─► .size() = 12
       │
       ├─► cdsService.getUpcomingAppointmentPatientIds(30)
       │   ├─► Queries database
       │   ├─► Returns: List<Integer> [patient IDs]
       │   └─► .size() = 23
       │
       ├─► cdsService.getPendingCdsActions()
       │   ├─► Queries database
       │   ├─► Returns: List<CdsActionRecord>
       │   └─► .size() = 3
       │
       ├─► new DashboardStats(5, 12, 23, 3)
       │   └─► Creates DTO with 4 counts
       │
       └─► Added to model as "stats"

4. Handler method executes
   └─► onGet() or onPost()
       └─► Returns view name: "/module/cds/cds"

5. View rendering
   └─► cds.gsp processes template
       ├─► ${stats.iitCount} = "5"
       ├─► ${stats.missedCount} = "12"
       ├─► ${stats.upcomingCount} = "23"
       ├─► ${stats.pendingActionsCount} = "3"
       └─► Renders complete HTML

6. HTML Response sent to browser
   └─► Dashboard displays with statistics ✅
```

---

## Error Handling Flow

```
getDashboardStats() {
    try {
        // Attempt to fetch statistics
        ├─► getIITPatientIds(90)
        ├─► getMissedAppointmentPatientIds(30)
        ├─► getUpcomingAppointmentPatientIds(30)
        └─► getPendingCdsActions()
        
        IF any throws exception:
        └─► Jump to catch block
    }
    catch (Exception e) {
        ├─► log.error(...) → Write to logs
        │
        └─► return new DashboardStats(0, 0, 0, 0)
            └─► Dashboard still renders with zeros
                (No crash, graceful degradation) ✅
    }
}

Without error handling:
    Exception thrown → Application crashes → Dashboard 500 error ❌

With error handling:
    Exception thrown → Logged → Returns fallback values → Dashboard OK ✅
```

---

## Spring Lifecycle with @ModelAttribute

```
1. Request Received
   ↓
2. DispatcherServlet determines handler method
   ↓
3. Identify ALL @ModelAttribute methods on handler class
   ↓
4. Invoke ALL @ModelAttribute methods
   ├─► getUsers() returns List<User>
   │   └─► model.addAttribute("users", result)
   │
   └─► getDashboardStats() returns DashboardStats
       └─► model.addAttribute("stats", result)
   ↓
5. Model now contains:
   {
     "users": [...User objects...],
     "stats": DashboardStats { 
       iitCount: 5,
       missedCount: 12,
       upcomingCount: 23,
       pendingActionsCount: 3
     }
   }
   ↓
6. Invoke handler method (onGet/onPost)
   ↓
7. Handler returns view name
   ↓
8. Render view with model
   ├─► ${users} is available
   └─► ${stats} is available ← NOW AVAILABLE!
   ↓
9. View template references ${stats.iitCount}
   └─► FOUND! No exception ✅

WITHOUT @ModelAttribute("stats"):
   Step 4 would skip getDashboardStats()
   Step 5 model would NOT have "stats" attribute
   Step 9 would throw MissingPropertyException ❌
```

---

This diagram shows how the fix resolves the issue by ensuring the `stats` object is created and added to the model before the view template tries to access it.

