@echo off
echo Starting Automation Tests...
echo.

REM Generate test data first
echo Generating test data...
call generate-testdata.bat

echo.
echo Running tests...
mvn clean test

echo.
echo Test execution completed!
echo Check the following locations for reports:
echo - ExtentReports: test-output/ExtentReports/
echo - TestNG Reports: target/surefire-reports/
echo - Screenshots: test-output/screenshots/

pause
