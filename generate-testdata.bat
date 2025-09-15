@echo off
echo Generating test data Excel file...
mvn compile exec:java -Dexec.mainClass="org.example.utils.ExcelDataGenerator"
echo Test data generation completed!
pause
