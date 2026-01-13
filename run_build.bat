@echo off
setlocal enabledelayedexpansion

cd /d "C:\Users\ginte\OneDrive\Desktop\ihvnprojects\cds"

echo Starting Maven clean install...
mvn clean install -DskipTests=true > build_log_full.txt 2>&1

echo Build exit code: %ERRORLEVEL% >> build_log_full.txt

if %ERRORLEVEL% equ 0 (
    echo BUILD SUCCESS >> build_log_full.txt
) else (
    echo BUILD FAILED >> build_log_full.txt
)

type build_log_full.txt | findstr /C:"BUILD" /C:"ERROR" /C:"SUCCESS" /C:"FAILURE" /C:"Tests run:"

