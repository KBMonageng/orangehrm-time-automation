package tests;

import base.BaseTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.ClockInOutPage;
import utils.ExcelUtilityRead;

import java.util.Map;

public class ClockInOutTest extends BaseTest {

    @DataProvider(name = "clockInOutData")
    public Object[][] clockInOutData() {
        Map<String, String> testData = ExcelUtilityRead.getRowDataByTestCase(
                "src/test/resources/testdata/LoginData.xlsx",
                "ClockInOut",
                "clockInOut"
        );

        return new Object[][]{
                {testData}
        };
    }

    @Test(
            groups = {"smoke", "regression"},
            dataProvider = "clockInOutData",
            description = "Validate that user can punch in and punch out successfully"
    )
    public void validateClockInOutLog(Map<String, String> testData) {
        ClockInOutPage clockInOutPage = new ClockInOutPage(page);

        clockInOutPage.completePunchInOutFlow(
                testData.get("punchInComment"),
                testData.get("punchOutComment"),
                testData.get("expectedResult")
        );
    }
}