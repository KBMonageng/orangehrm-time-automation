package tests;

import base.BaseTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.SubmitTimesheetPage;
import utils.ExcelUtilityRead;

import java.util.Map;

public class SubmitTimesheetTest extends BaseTest {

    @DataProvider(name = "submitTimesheetData")
    public Object[][] submitTimesheetData() {
        Map<String, String> testData = ExcelUtilityRead.getRowDataByTestCase(
                "src/test/resources/testdata/LoginData.xlsx",
                "SubmitTimesheet",
                "submitTimesheet"
        );

        return new Object[][]{
                {testData}
        };
    }

    @Test(
            groups = {"regression"},
            dataProvider = "submitTimesheetData",
            description = "Validate that user can submit a timesheet successfully"
    )
    public void validateTimesheetSubmissionStatus(Map<String, String> testData) {
        SubmitTimesheetPage submitTimesheetPage = new SubmitTimesheetPage(page);

        submitTimesheetPage.submitTimesheet(
                testData.get("projectName"),
                testData.get("activityName"),
                testData.get("hours")
        );
    }
}