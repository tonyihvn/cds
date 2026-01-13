@echo off
REM Quick rebuild script for CDS Module with patientDashboard fix

cd /d "C:\Users\ginte\OneDrive\Desktop\ihvnprojects\cds"

echo.
echo ============================================
echo  CDS Module - Quick Rebuild
echo ============================================
echo.
echo This script will rebuild the CDS module with
echo the patientDashboard reference fix.
echo.

echo Step 1: Cleaning previous build...
mvn clean

echo.
echo Step 2: Building module (skipping tests)...
mvn install -DskipTests=true

echo.
echo ============================================
echo  Build Complete!
echo ============================================
echo.
echo Module ready at: omod\target\cds-1.0.0-SNAPSHOT.omod
echo.
echo Next steps:
echo 1. Copy the .omod file to OpenMRS modules directory
echo 2. Restart OpenMRS
echo 3. Test the Patient Dashboard link
echo.
pause

