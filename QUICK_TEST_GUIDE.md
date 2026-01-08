# 🚀 Quick Test Guide - CDS Module

## Get Started in 60 Seconds

### 1️⃣ Run All Tests
```bash
cd C:\Users\ginte\OneDrive\Desktop\ihvnprojects\cds
mvn clean test
```

**Expected Result**:
```
[INFO] Tests run: 85+
[INFO] Failures: 0 ✓
[INFO] BUILD SUCCESS
```

---

### 2️⃣ Run Specific Test Suite

**Run Controller Tests Only**:
```bash
mvn test -Dtest=ClinicalDataSystemFragmentControllerTest
```

**Run Service Tests Only**:
```bash
mvn test -Dtest=ClinicalDataSystemServiceExtendedTest
```

**Run Error Handling Tests**:
```bash
mvn test -Dtest=ErrorHandlingTest
```

**Run Integration Tests**:
```bash
mvn test -Dtest=ClinicalDataSystemControllerIntegrationTest
```

---

### 3️⃣ Use Test Runner Script
```bash
test-runner.bat
```

This runs all tests sequentially and generates reports.

---

## Test Files

| File | Purpose | Count |
|------|---------|-------|
| ClinicalDataSystemFragmentControllerTest | Controller logic | 10 |
| ClinicalDataSystemServiceExtendedTest | Service layer | 21 |
| ClinicalDataSystemDaoTest | Database access | 14 |
| DashboardStatsDTOTest | Data objects | 14 |
| ErrorHandlingTest | Error scenarios | 15 |
| ClinicalDataSystemControllerIntegrationTest | E2E workflows | 11 |
| **TOTAL** | | **85+** |

---

## Test Coverage

✅ **Happy Path**: Real data, all systems working  
✅ **Error Handling**: Database down, exceptions, timeouts  
✅ **Edge Cases**: Zero values, large numbers, null returns  
✅ **Performance**: 100+ calls, large datasets  
✅ **Integration**: Complete workflows, data consistency  

---

## What Gets Tested

### Controller
- ✅ Dashboard stats retrieval
- ✅ Mock data fallback
- ✅ Error recovery
- ✅ Multiple consecutive calls

### Service
- ✅ Patient ID queries
- ✅ Action records retrieval
- ✅ DAO interactions
- ✅ Different lookback periods

### DAO
- ✅ Hibernate queries
- ✅ Parameter binding
- ✅ Session management
- ✅ Transaction handling

### DTO
- ✅ Constructors
- ✅ Getters/Setters
- ✅ Data integrity
- ✅ Edge values

### Error Handling
- ✅ Connection failures
- ✅ Null pointer exceptions
- ✅ Partial failures
- ✅ Graceful degradation

---

## Pre-Deployment Testing

Before deploying to OpenMRS:

```bash
# 1. Clean and build
mvn clean install

# 2. Run all tests
mvn clean test

# 3. Check results
echo "If BUILD SUCCESS, you're ready to deploy!"
```

---

## Results Location

```
Test Reports:
├── omod/target/surefire-reports/
├── api/target/surefire-reports/
└── target/surefire-reports/

Results Formats:
├── TEST-*.xml (XML format)
└── *.txt (Text format)
```

---

## View Test Results

### In Maven Output
```bash
mvn test | grep -E "Tests run:|Failures:|Errors:"
```

### In Test Reports
Open: `omod/target/surefire-reports/`

### View Specific Report
```bash
# List all test results
dir omod\target\surefire-reports\

# Open a report
start omod\target\surefire-reports\TEST-*.txt
```

---

## Common Commands

| Command | Purpose |
|---------|---------|
| `mvn clean test` | Run all tests |
| `mvn test -Dtest=ClassName` | Run specific test |
| `mvn clean test -DskipTests=false` | Force run tests |
| `mvn test -X` | Debug mode |
| `mvn test -q` | Quiet mode (less output) |

---

## Troubleshooting

### Tests Won't Run
```bash
# Solution 1: Clean and rebuild
mvn clean
mvn test

# Solution 2: Check Java version
java -version

# Solution 3: Update Maven
mvn -v
```

### Test Failures
```bash
# Get detailed output
mvn test -e

# Run single failing test
mvn test -Dtest=TestClassName#testMethodName

# Debug mode
mvn test -X
```

### Memory Issues
```bash
# Increase heap size
set MAVEN_OPTS=-Xmx1024m
mvn test
```

---

## Expected Results

✅ **All Tests Should Pass**:
```
[INFO] Tests run: 85+
[INFO] Failures: 0
[INFO] Errors: 0
[INFO] Skipped: 0
[INFO] BUILD SUCCESS
```

❌ **If Any Fail**:
```
Check error message for:
1. Missing mock setup
2. Assertion failure
3. Exception thrown
4. Timeout

Fix issue and re-run: mvn test
```

---

## Using in IDE

### Eclipse
1. Right-click test class
2. Select: Run As → JUnit Test
3. View results in JUnit view

### IntelliJ
1. Right-click test class
2. Select: Run 'TestClassName'
3. View results in Run window

### VS Code
1. Install Test Runner for Java extension
2. Click ▶️ on test method
3. View results in output

---

## Test Patterns

### Testing Real Data
```java
when(service.getIITPatientIds(90)).thenReturn(createList(8));
DashboardStats result = controller.getDashboardStats();
assertEquals(8, result.getIitCount());
```

### Testing Errors
```java
when(service.getIITPatientIds(90))
    .thenThrow(new RuntimeException("Error"));
DashboardStats result = controller.getDashboardStats();
assertEquals(8, result.getIitCount()); // Falls back to mock
```

### Testing Multiple Calls
```java
when(service.getIITPatientIds(90))
    .thenReturn(list1)
    .thenReturn(list2)
    .thenReturn(list3);
```

---

## What to Test Before Deployment

- [ ] All 85+ tests pass
- [ ] No compilation errors
- [ ] No test timeouts
- [ ] Mock data fallback works
- [ ] Error handling verified
- [ ] Integration tests complete
- [ ] Performance acceptable

---

## Quick Reference

**Run all tests**:
```bash
mvn clean test
```

**Run one test class**:
```bash
mvn test -Dtest=ClinicalDataSystemFragmentControllerTest
```

**Run with full output**:
```bash
mvn clean test -e
```

**Check results**:
```bash
cat omod/target/surefire-reports/TEST-*.txt
```

---

## Next Steps

1. ✅ Run: `mvn clean test`
2. ✅ Verify all tests pass
3. ✅ Check test reports
4. ✅ Review error scenarios
5. ✅ Deploy to OpenMRS

---

## Support

**Need help?** Check:
- TEST_SUITE_DOCUMENTATION.md - Full test guide
- Individual test class comments
- Maven output for details
- Error messages for solutions

---

**Status**: ✅ **Ready to Test**  
**Total Tests**: 85+  
**Expected Time**: ~1 minute  

Run: `mvn clean test` now! 🚀

