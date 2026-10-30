package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExcelUtilityRead {

    private ExcelUtilityRead() {
    }

    public static Map<String, String> getRowByTestCase(String filePath, String sheetName, String testCaseName) {
        return getRowDataByTestCase(filePath, sheetName, testCaseName);
    }

    public static Map<String, String> getRowDataByTestCase(String filePath, String sheetName, String testCaseName) {
        Map<String, String> rowData = new HashMap<>();

        try {
            FileInputStream file = new FileInputStream(filePath);
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                workbook.close();
                file.close();
                throw new RuntimeException("Sheet not found in Excel file: " + sheetName);
            }

            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                workbook.close();
                file.close();
                throw new RuntimeException("Header row is missing in sheet: " + sheetName);
            }

            int testCaseNameColumnIndex = -1;

            for (int column = 0; column < headerRow.getLastCellNum(); column++) {
                String headerName = getCellValueAsString(headerRow.getCell(column));

                if (headerName.equalsIgnoreCase("testCaseName")) {
                    testCaseNameColumnIndex = column;
                    break;
                }
            }

            if (testCaseNameColumnIndex == -1) {
                workbook.close();
                file.close();
                throw new RuntimeException("Column 'testCaseName' was not found in sheet: " + sheetName);
            }

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row currentRow = sheet.getRow(rowIndex);

                if (currentRow == null) {
                    continue;
                }

                String currentTestCaseName = getCellValueAsString(currentRow.getCell(testCaseNameColumnIndex));

                if (currentTestCaseName.equalsIgnoreCase(testCaseName)) {
                    for (int column = 0; column < headerRow.getLastCellNum(); column++) {
                        String key = getCellValueAsString(headerRow.getCell(column));
                        String value = getCellValueAsString(currentRow.getCell(column));

                        rowData.put(key, value);
                    }

                    workbook.close();
                    file.close();
                    return rowData;
                }
            }

            workbook.close();
            file.close();

            throw new RuntimeException("Test case '" + testCaseName + "' was not found in sheet: " + sheetName);

        } catch (IOException e) {
            throw new RuntimeException("Could not read Excel file: " + filePath + ". Error: " + e.getMessage());
        }
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }
}