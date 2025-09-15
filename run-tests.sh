#!/bin/bash
echo "Starting Automation Tests..."
echo

# Generate test data first
echo "Generating test data..."
./generate-testdata.sh

echo
echo "Running tests..."
mvn clean test

echo
echo "Test execution completed!"
echo "Check the following locations for reports:"
echo "- ExtentReports: test-output/ExtentReports/"
echo "- TestNG Reports: target/surefire-reports/"
echo "- Screenshots: test-output/screenshots/"
