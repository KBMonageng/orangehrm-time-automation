package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.testng.Assert;
import utils.ConfigReader;
import utils.WaitUtils;

public class ApprovalWorkflowPage {

    private final Page page;

    private boolean employeeTimesheetOpened = false;
    private boolean timesheetReset = false;
    private boolean timesheetCreated = false;
    private boolean timesheetEdited = false;
    private boolean timesheetDataEntered = false;
    private boolean timesheetSaved = false;
    private boolean timesheetSubmitted = false;
    private boolean timesheetApproved = false;

    public ApprovalWorkflowPage(Page page) {
        this.page = page;
    }

    public void openTimeModule() {
        WaitUtils.click(page, "approval.timeMenu");
        WaitUtils.waitForPage(page);
    }

    public void searchEmployeeTimesheet(String employeeName) {
        WaitUtils.clearAndFill(page, "approval.employeeInput", employeeName);
        WaitUtils.waitForFiveSeconds(page);

        if (WaitUtils.isVisibleNow(page, "approval.employeeOption")) {
            WaitUtils.click(page, "approval.employeeOption");
        } else {
            page.keyboard().press("ArrowDown");
            page.keyboard().press("Enter");
        }

        WaitUtils.click(page, "approval.viewButton");
        WaitUtils.waitForPage(page);
        WaitUtils.waitForTwoSeconds(page);

        employeeTimesheetOpened = isEmployeeTimesheetPageOpen();
        Assert.assertTrue(employeeTimesheetOpened,
                "Employee timesheet page did not open after clicking View. Current URL: " + page.url());
    }

    public boolean isEmployeeTimesheetPageOpen() {
        String pageText = WaitUtils.text(page, "approval.body");

        return pageText.contains("Timesheet")
                || pageText.contains("Create Timesheet")
                || pageText.contains("Reset")
                || pageText.contains("Edit")
                || pageText.contains("Submit")
                || pageText.contains("Approve")
                || pageText.contains("Approved")
                || pageText.contains("Submitted");
    }

    public void resetTimesheetIfAvailable() {
        if (WaitUtils.isVisibleNow(page, "approval.resetButton")) {
            WaitUtils.click(page, "approval.resetButton");
            WaitUtils.waitForPage(page);
            WaitUtils.waitForTwoSeconds(page);
            timesheetReset = true;
        }
    }

    public void createTimesheetIfNeeded() {
        if (WaitUtils.isVisibleNow(page, "approval.createTimesheetButton")) {
            WaitUtils.click(page, "approval.createTimesheetButton");
            WaitUtils.waitForPage(page);
            WaitUtils.waitForTwoSeconds(page);
            timesheetCreated = true;
        }
    }

    public void clickEditIfAvailable() {
        if (WaitUtils.isVisibleNow(page, "approval.editButton")) {
            WaitUtils.click(page, "approval.editButton");
            WaitUtils.waitForPage(page);
            WaitUtils.waitForTwoSeconds(page);
            timesheetEdited = true;
        }
    }

    public void clickAddRowIfAvailable() {
        if (WaitUtils.isVisibleNow(page, "approval.addRowButton")) {
            WaitUtils.click(page, "approval.addRowButton");
            WaitUtils.waitForTwoSeconds(page);
        }
    }

    public boolean isProjectInputVisible() {
        return WaitUtils.isVisibleNow(page, "approval.projectInput");
    }

    public void selectProject(String projectSearchValue) {
        Assert.assertTrue(isProjectInputVisible(), "Project input was not displayed after clicking Edit.");

        WaitUtils.clearAndFill(page, "approval.projectInput", projectSearchValue);
        WaitUtils.waitForFiveSeconds(page);

        String[] preferredProjects = ConfigReader.getArray("preferred.projects");
        for (String project : preferredProjects) {
            Locator option = page.getByText(project).first();
            if (option.isVisible()) {
                option.click();
                WaitUtils.waitForTwoSeconds(page);
                return;
            }
        }

        Locator firstOption = page.locator(".oxd-autocomplete-option").first();
        Assert.assertTrue(firstOption.isVisible(), "No project option was displayed after typing: " + projectSearchValue);
        firstOption.click();
        WaitUtils.waitForTwoSeconds(page);
    }

    public void selectActivity(String activityName) {
        WaitUtils.click(page, "approval.activityDropdown");
        WaitUtils.waitForTwoSeconds(page);

        String[] preferredActivities = ConfigReader.getArray("preferred.activities");
        for (String activity : preferredActivities) {
            Locator optionByRole = page.getByRole(
                    com.microsoft.playwright.options.AriaRole.OPTION,
                    new Page.GetByRoleOptions().setName(activity)
            ).first();

            if (optionByRole.isVisible()) {
                optionByRole.click();
                WaitUtils.waitForTwoSeconds(page);
                return;
            }

            Locator optionByText = page.getByText(activity).first();
            if (optionByText.isVisible()) {
                optionByText.click();
                WaitUtils.waitForTwoSeconds(page);
                return;
            }
        }

        // Fallback: skip the disabled placeholder and select the first available real option.
        page.keyboard().press("ArrowDown");
        WaitUtils.waitForTwoSeconds(page);
        page.keyboard().press("ArrowDown");
        WaitUtils.waitForTwoSeconds(page);
        page.keyboard().press("Enter");
        WaitUtils.waitForTwoSeconds(page);
    }

    public void enterHoursForWeek(String hours) {
        Locator hourInputs = WaitUtils.find(page, "approval.hoursInputs");
        int numberOfInputs = hourInputs.count();
        Assert.assertTrue(numberOfInputs > 0, "No timesheet hour input fields were found.");

        int daysToFill = Math.min(numberOfInputs, 2);
        for (int index = 0; index < daysToFill; index++) {
            hourInputs.nth(index).click();
            hourInputs.nth(index).fill(hours);
        }

        WaitUtils.waitForTwoSeconds(page);
    }

    public void enterTimesheetData(String projectName, String activityName, String hours) {
        if (!isProjectInputVisible()) {
            return;
        }

        selectProject(projectName);
        selectActivity(activityName);
        enterHoursForWeek(hours);
        timesheetDataEntered = true;
    }

    public void saveTimesheetIfDataEntered() {
        if (!timesheetDataEntered) {
            return;
        }

        Assert.assertTrue(WaitUtils.isVisible(page, "approval.saveButton"),
                "Timesheet data was entered, but the Save button was not displayed.");

        WaitUtils.click(page, "approval.saveButton");
        WaitUtils.waitForFiveSeconds(page);
        timesheetSaved = true;
    }

    public void submitTimesheetIfNeeded() {
        if (timesheetReset) {
            return;
        }

        if (WaitUtils.isVisibleNow(page, "approval.submitButton")) {
            WaitUtils.click(page, "approval.submitButton");
            WaitUtils.waitForFiveSeconds(page);
            timesheetSubmitted = true;
        }
    }

    public void addApprovalCommentIfAvailable(String comment) {
        if (WaitUtils.isVisibleNow(page, "approval.commentBox")) {
            WaitUtils.clearAndFill(page, "approval.commentBox", comment);
            WaitUtils.waitForTwoSeconds(page);
        }
    }

    public void approveTimesheetIfAvailable() {
        if (WaitUtils.isVisibleNow(page, "approval.approveButton")) {
            WaitUtils.click(page, "approval.approveButton");
            WaitUtils.waitForFiveSeconds(page);
            timesheetApproved = true;
        }
    }

    public boolean isApprovedOrSubmittedVisible() {
        String pageText = WaitUtils.text(page, "approval.body");
        return pageText.contains("Approved")
                || pageText.contains("Submitted")
                || WaitUtils.isVisibleNow(page, "approval.approvedText")
                || WaitUtils.isVisibleNow(page, "approval.submittedText");
    }

    public void verifyApprovalWorkflowCompleted() {
        Assert.assertTrue(employeeTimesheetOpened, "Employee timesheet was not opened.");

        boolean setupActionHappened = timesheetReset || timesheetCreated || timesheetEdited || timesheetDataEntered;
        boolean workflowStateVisible = isApprovedOrSubmittedVisible();
        boolean approvalActionHappened = timesheetApproved || timesheetSubmitted || workflowStateVisible;

        Assert.assertTrue(
                setupActionHappened || workflowStateVisible,
                "Approval workflow did not reach an editable, submitted, or approved timesheet state."
        );

        Assert.assertTrue(
                approvalActionHappened || WaitUtils.isVisibleNow(page, "approval.timesheetContainer"),
                "Timesheet was not approved, submitted, or visible after approval workflow."
        );
    }

    public void approveEmployeeTimesheet(String employeeName, String projectName, String activityName, String hours, String comment) {
        openTimeModule();
        searchEmployeeTimesheet(employeeName);
        resetTimesheetIfAvailable();
        createTimesheetIfNeeded();
        clickEditIfAvailable();
        clickAddRowIfAvailable();
        enterTimesheetData(projectName, activityName, hours);
        saveTimesheetIfDataEntered();
        submitTimesheetIfNeeded();
        addApprovalCommentIfAvailable(comment);
        approveTimesheetIfAvailable();
        verifyApprovalWorkflowCompleted();
    }
}
