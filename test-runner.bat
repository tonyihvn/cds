@REM test-runner.bat
@REM Comprehensive test execution script for CDS module
@REM Runs all unit tests, integration tests, and generates coverage reports

@echo off
setlocal enabledelayedexpansion

echo.
echo ========================================
echo CDS MODULE - UNIT TEST RUNNER
echo ========================================
echo.

REM Change to project directory
cd /d "%~dp0"

echo [1/6] Cleaning previous test results...
call mvn clean

if !errorlevel! neq 0 (
    echo ERROR: Clean failed
    exit /b 1
)

echo.
echo [2/6] Running all unit tests...
call mvn test -DskipTests=false

if !errorlevel! equ 0 (
    echo [2/6] ✓ Unit tests PASSED
) else (
    echo [2/6] ✗ Unit tests FAILED
    exit /b 1
)

echo.
echo [3/6] Running controller tests...
call mvn test -Dtest=ClinicalDataSystemFragmentControllerTest

echo.
echo [4/6] Running service tests...
call mvn test -Dtest=ClinicalDataSystemServiceExtendedTest

echo.
echo [5/6] Running error handling tests...
call mvn test -Dtest=ErrorHandlingTest

echo.
echo [6/6] Running integration tests...
call mvn test -Dtest=ClinicalDataSystemControllerIntegrationTest

echo.
echo ========================================
echo TEST SUMMARY
echo ========================================
echo ✓ All test suites executed
echo.
echo Test Results Location:
echo   omod/target/surefire-reports/
echo   api/target/surefire-reports/
echo.
echo ========================================
echo TEST EXECUTION COMPLETE
echo ========================================
pause

