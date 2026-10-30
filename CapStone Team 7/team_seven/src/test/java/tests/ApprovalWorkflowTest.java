package tests;

import base.BaseTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.ApprovalWorkflowPage;
import utils.ExcelUtilityRead;

import java.util.Map;

public class ApprovalWorkflowTest extends BaseTest {

    @DataProvider(name = "approvalWorkflowData")
    public Object[][] approvalWorkflowData() {
        Map<String, String> testData = ExcelUtilityRead.getRowDataByTestCase(
                "src/test/resources/testdata/LoginData.xlsx",
                "ApprovalWorkflow",
                "approvalWorkflow"
        );

        return new Object[][]{
                {testData}
        };
    }

    @Test(
            groups = {"regression"},
            dataProvider = "approvalWorkflowData",
            description = "Validate that a submitted timesheet can be approved"
    )
    public void validateSubmittedTimesheetCanBeApproved(Map<String, String> testData) {
        ApprovalWorkflowPage approvalWorkflowPage = new ApprovalWorkflowPage(page);

        approvalWorkflowPage.approveEmployeeTimesheet(
                testData.get("employeeName"),
                testData.get("projectName"),
                testData.get("activityName"),
                testData.get("hours"),
                testData.get("comment")
        );
    }
}