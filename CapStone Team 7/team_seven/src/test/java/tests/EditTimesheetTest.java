package tests;

import base.BaseTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.EditTimesheetPage;
import utils.ExcelUtilityRead;

import java.util.Map;

public class EditTimesheetTest extends BaseTest {

    @DataProvider(name = "editTimesheetData")
    public Object[][] editTimesheetData() {
        Map<String, String> testData = ExcelUtilityRead.getRowDataByTestCase(
                "src/test/resources/testdata/LoginData.xlsx",
                "EditTimesheet",
                "editTimesheet"
        );

        return new Object[][]{
                {testData}
        };
    }

    @Test(
            groups = {"regression"},
            dataProvider = "editTimesheetData",
            description = "Validate that user can edit and save a timesheet"
    )
    public void validateTimesheetEditsAreSaved(Map<String, String> testData) {
        EditTimesheetPage editTimesheetPage = new EditTimesheetPage(page);

        editTimesheetPage.editTimesheet(
                testData.get("projectName"),
                testData.get("activityName"),
                testData.get("hours"),
                testData.get("expectedResult")
        );
    }
}