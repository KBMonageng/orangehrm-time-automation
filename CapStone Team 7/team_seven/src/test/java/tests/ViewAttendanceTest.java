package tests;

import base.BaseTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.ViewAttendancePage;
import utils.ExcelUtilityRead;

import java.util.Map;

public class ViewAttendanceTest extends BaseTest {

    @DataProvider(name = "viewAttendanceData")
    public Object[][] viewAttendanceData() {
        Map<String, String> testData = ExcelUtilityRead.getRowDataByTestCase(
                "src/test/resources/testdata/LoginData.xlsx",
                "ViewAttendance",
                "viewAttendance"
        );

        return new Object[][]{
                {testData}
        };
    }

    @Test(
            groups = {"smoke", "regression"},
            dataProvider = "viewAttendanceData",
            description = "Validate that attendance records are displayed"
    )
    public void validateAttendanceEntriesAreDisplayed(Map<String, String> testData) {
        ViewAttendancePage viewAttendancePage = new ViewAttendancePage(page);

        viewAttendancePage.viewMyAttendanceRecords();
        viewAttendancePage.verifyAttendanceRecordsAreDisplayed();
    }
}