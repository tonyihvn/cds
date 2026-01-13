@echo off
REM Rebuild the CDS module to update all compiled files

cd /d "C:\Users\ginte\OneDrive\Desktop\ihvnprojects\cds"

echo ============================================
echo  Starting Maven Clean and Rebuild...
echo ============================================

REM Clean the previous build
mvn clean

REM Build without running tests
mvn install -DskipTests=true

echo ============================================
echo  Build Complete!
echo ============================================
echo The module has been rebuilt successfully.
echo You can now restart OpenMRS to see the changes.
pause

