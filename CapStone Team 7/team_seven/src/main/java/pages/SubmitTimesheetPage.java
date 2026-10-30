package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.testng.Assert;
import utils.ConfigReader;
import utils.WaitUtils;

public class SubmitTimesheetPage {

    private final Page page;

    public SubmitTimesheetPage(Page page) {
        this.page = page;
    }

    public void openTimeModule() {
        WaitUtils.click(page, "submit.timeMenu");
        WaitUtils.waitForPage(page);
    }

    public void openTimesheetsMenu() {
        WaitUtils.click(page, "submit.timesheetsMenu");
        Assert.assertTrue(WaitUtils.isVisible(page, "submit.myTimesheetsMenu"), "My Timesheets option was not displayed.");
    }

    public void openMyTimesheetsPage() {
        WaitUtils.click(page, "submit.myTimesheetsMenu");
        WaitUtils.waitForPage(page);
        WaitUtils.waitForTwoSeconds(page);
    }

    public void goToMyTimesheet() {
        openTimeModule();
        openTimesheetsMenu();
        openMyTimesheetsPage();
    }

    public void createOrResetIfNeeded() {
        if (WaitUtils.isVisibleNow(page, "submit.createTimesheetButton")) {
            WaitUtils.click(page, "submit.createTimesheetButton");
            WaitUtils.waitForPage(page);
            WaitUtils.waitForTwoSeconds(page);
        }

        if (WaitUtils.isVisibleNow(page, "submit.resetButton")) {
            WaitUtils.click(page, "submit.resetButton");
            WaitUtils.waitForPage(page);
            WaitUtils.waitForTwoSeconds(page);
        }
    }

    public void openEditModeIfAvailable() {
        if (WaitUtils.isVisibleNow(page, "submit.editButton")) {
            WaitUtils.click(page, "submit.editButton");
            WaitUtils.waitForPage(page);
            WaitUtils.waitForTwoSeconds(page);
        }

        if (WaitUtils.isVisibleNow(page, "submit.addRowButton")) {
            WaitUtils.click(page, "submit.addRowButton");
            WaitUtils.waitForTwoSeconds(page);
        }
    }

    public boolean isProjectInputVisible() {
        return WaitUtils.isVisibleNow(page, "submit.projectInput");
    }

    public void selectProject(String projectSearchValue) {
        WaitUtils.clearAndFill(page, "submit.projectInput", projectSearchValue);
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
        WaitUtils.click(page, "submit.activityDropdown");
        WaitUtils.waitForTwoSeconds(page);

        String[] preferredActivities = ConfigReader.getArray("preferred.activities");
        for (String activity : preferredActivities) {
            Locator option = page.getByText(activity).first();
            if (option.isVisible()) {
                option.click();
                WaitUtils.waitForTwoSeconds(page);
                return;
            }
        }

        page.keyboard().press("ArrowDown");
        WaitUtils.waitForTwoSeconds(page);
        page.keyboard().press("ArrowDown");
        WaitUtils.waitForTwoSeconds(page);
        page.keyboard().press("Enter");
        WaitUtils.waitForTwoSeconds(page);
    }

    public void enterHoursForWeek(String hours) {
        Locator hourInputs = WaitUtils.find(page, "submit.hoursInputs");
        int numberOfInputs = hourInputs.count();
        Assert.assertTrue(numberOfInputs > 0, "No timesheet hour input fields were found.");

        int daysToFill = Math.min(numberOfInputs, 5);
        for (int index = 0; index < daysToFill; index++) {
            hourInputs.nth(index).click();
            hourInputs.nth(index).fill(hours);
        }
    }

    public void saveTimesheetIfAvailable() {
        if (WaitUtils.isVisibleNow(page, "submit.saveButton")) {
            WaitUtils.click(page, "submit.saveButton");
            WaitUtils.waitForTwoSeconds(page);
        }
    }

    public void enterTimesheetDataIfEditable(String projectName, String activityName, String hours) {
        if (isProjectInputVisible()) {
            selectProject(projectName);
            selectActivity(activityName);
            enterHoursForWeek(hours);
            saveTimesheetIfAvailable();
        }
    }

    public void clickSubmitIfAvailable() {
        if (WaitUtils.isVisibleNow(page, "submit.submitButton")) {
            WaitUtils.click(page, "submit.submitButton");
            WaitUtils.waitForPage(page);
            WaitUtils.waitForTwoSeconds(page);
        }
    }

    public void verifyTimesheetSubmittedOrVisible() {
        Assert.assertTrue(
                WaitUtils.isVisible(page, "submit.timesheetContainer")
                        || WaitUtils.isVisibleNow(page, "submit.submittedText")
                        || WaitUtils.isVisibleNow(page, "submit.approvedText")
                        || WaitUtils.isVisibleNow(page, "submit.editButton")
                        || WaitUtils.isVisibleNow(page, "submit.submitButton"),
                "Timesheet page was not visible after submit attempt."
        );
    }

    public void submitTimesheet(String projectName, String activityName, String hours) {
        goToMyTimesheet();
        createOrResetIfNeeded();
        openEditModeIfAvailable();
        enterTimesheetDataIfEditable(projectName, activityName, hours);
        clickSubmitIfAvailable();
        verifyTimesheetSubmittedOrVisible();
    }
}
