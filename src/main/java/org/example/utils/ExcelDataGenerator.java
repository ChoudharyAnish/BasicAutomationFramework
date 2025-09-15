package org.example.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility to generate sample Excel test data file
 */
public class ExcelDataGenerator {
    
    public static void generateSampleTestData() {
        Workbook workbook = new XSSFWorkbook();
        
        // Create LoginData sheet
        Sheet loginSheet = workbook.createSheet("LoginData");
        createLoginDataSheet(loginSheet);
        
        // Create UserData sheet
        Sheet userSheet = workbook.createSheet("UserData");
        createUserDataSheet(userSheet);
        
        // Save the workbook
        try {
            FileOutputStream fileOut = new FileOutputStream("src/main/resources/testdata.xlsx");
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
            System.out.println("Sample test data Excel file created successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void createLoginDataSheet(Sheet sheet) {
        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("username");
        headerRow.createCell(1).setCellValue("password");
        headerRow.createCell(2).setCellValue("expectedResult");
        headerRow.createCell(3).setCellValue("testDescription");
        
        // Create data rows
        Object[][] loginData = {
            {"admin@example.com", "admin123", "success", "Valid admin login"},
            {"user@example.com", "user123", "success", "Valid user login"},
            {"test@example.com", "test123", "success", "Valid test user login"},
            {"invalid@example.com", "wrongpass", "failure", "Invalid password"},
            {"", "password123", "failure", "Empty username"},
            {"user@example.com", "", "failure", "Empty password"},
            {"", "", "failure", "Empty credentials"},
            {"notanemail", "password123", "failure", "Invalid email format"}
        };
        
        for (int i = 0; i < loginData.length; i++) {
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < loginData[i].length; j++) {
                row.createCell(j).setCellValue(loginData[i][j].toString());
            }
        }
        
        // Auto-size columns
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private static void createUserDataSheet(Sheet sheet) {
        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("firstName");
        headerRow.createCell(1).setCellValue("lastName");
        headerRow.createCell(2).setCellValue("email");
        headerRow.createCell(3).setCellValue("phone");
        headerRow.createCell(4).setCellValue("address");
        headerRow.createCell(5).setCellValue("city");
        headerRow.createCell(6).setCellValue("zipCode");
        
        // Create data rows
        Object[][] userData = {
            {"John", "Doe", "john.doe@example.com", "1234567890", "123 Main St", "New York", "10001"},
            {"Jane", "Smith", "jane.smith@example.com", "0987654321", "456 Oak Ave", "Los Angeles", "90001"},
            {"Mike", "Johnson", "mike.johnson@example.com", "5555551234", "789 Pine Rd", "Chicago", "60601"},
            {"Sarah", "Williams", "sarah.williams@example.com", "1112223333", "321 Elm St", "Houston", "77001"},
            {"David", "Brown", "david.brown@example.com", "4445556666", "654 Maple Dr", "Phoenix", "85001"}
        };
        
        for (int i = 0; i < userData.length; i++) {
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < userData[i].length; j++) {
                row.createCell(j).setCellValue(userData[i][j].toString());
            }
        }
        
        // Auto-size columns
        for (int i = 0; i < 7; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    public static void main(String[] args) {
        generateSampleTestData();
    }
}
