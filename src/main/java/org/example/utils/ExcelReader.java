package org.example.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to read and write Excel files
 */
public class ExcelReader {
    private Workbook workbook;
    private Sheet sheet;
    private String filePath;

    public ExcelReader(String filePath) {
        this.filePath = filePath;
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load Excel file: " + filePath);
        }
    }

    public void setSheet(String sheetName) {
        sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            throw new RuntimeException("Sheet '" + sheetName + "' not found in Excel file");
        }
    }

    public void setSheet(int sheetIndex) {
        sheet = workbook.getSheetAt(sheetIndex);
        if (sheet == null) {
            throw new RuntimeException("Sheet at index " + sheetIndex + " not found in Excel file");
        }
    }

    public int getRowCount() {
        return sheet.getLastRowNum() + 1;
    }

    public int getColumnCount() {
        return sheet.getRow(0).getLastCellNum();
    }

    public String getCellData(int rowNum, int colNum) {
        Cell cell = sheet.getRow(rowNum).getCell(colNum);
        return getCellValueAsString(cell);
    }

    public String getCellData(int rowNum, String columnName) {
        int colNum = getColumnIndex(columnName);
        return getCellData(rowNum, colNum);
    }

    private int getColumnIndex(String columnName) {
        Row headerRow = sheet.getRow(0);
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            if (getCellValueAsString(headerRow.getCell(i)).equals(columnName)) {
                return i;
            }
        }
        throw new RuntimeException("Column '" + columnName + "' not found in Excel sheet");
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    public List<Map<String, String>> getAllData() {
        List<Map<String, String>> data = new ArrayList<>();
        Row headerRow = sheet.getRow(0);
        
        for (int i = 1; i < getRowCount(); i++) {
            Map<String, String> rowData = new HashMap<>();
            Row row = sheet.getRow(i);
            
            for (int j = 0; j < getColumnCount(); j++) {
                String columnName = getCellValueAsString(headerRow.getCell(j));
                String cellValue = getCellValueAsString(row.getCell(j));
                rowData.put(columnName, cellValue);
            }
            data.add(rowData);
        }
        return data;
    }

    public Object[][] getDataForDataProvider() {
        List<Map<String, String>> data = getAllData();
        Object[][] dataArray = new Object[data.size()][1];
        
        for (int i = 0; i < data.size(); i++) {
            dataArray[i][0] = data.get(i);
        }
        return dataArray;
    }

    public void setCellData(int rowNum, int colNum, String data) {
        try {
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                row = sheet.createRow(rowNum);
            }
            
            Cell cell = row.getCell(colNum);
            if (cell == null) {
                cell = row.createCell(colNum);
            }
            
            cell.setCellValue(data);
            
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to write data to Excel file");
        }
    }

    public void close() {
        try {
            if (workbook != null) {
                workbook.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
