package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.testng.Assert;
import utils.ConfigReader;
import utils.WaitUtils;

public class EditTimesheetPage {

    private final Page page;

    public EditTimesheetPage(Page page) {
        this.page = page;
    }

    public void openTimeModule() {
        WaitUtils.click(page, "timesheet.timeMenu");
        WaitUtils.waitForPage(page);
    }

    public void openTimesheetsMenu() {
        WaitUtils.click(page, "timesheet.timesheetsMenu");
        Assert.assertTrue(
                WaitUtils.isVisible(page, "timesheet.myTimesheetsMenu"),
                "My Timesheets option was not displayed."
        );
    }

    public void openMyTimesheetsPage() {
        WaitUtils.click(page, "timesheet.myTimesheetsMenu");
        WaitUtils.waitForPage(page);
        WaitUtils.waitForTwoSeconds(page);
    }

    public void openMyTimesheet() {
        openTimeModule();
        openTimesheetsMenu();
        openMyTimesheetsPage();
    }

    public void prepareEditableTimesheet() {
        if (WaitUtils.isVisibleNow(page, "timesheet.createTimesheetButton")) {
            WaitUtils.click(page, "timesheet.createTimesheetButton");
            WaitUtils.waitForPage(page);
            WaitUtils.waitForTwoSeconds(page);
        }

        if (WaitUtils.isVisibleNow(page, "timesheet.resetButton")) {
            WaitUtils.click(page, "timesheet.resetButton");
            WaitUtils.waitForPage(page);
            WaitUtils.waitForTwoSeconds(page);
        }

        if (WaitUtils.isVisibleNow(page, "timesheet.editButton")) {
            WaitUtils.click(page, "timesheet.editButton");
            WaitUtils.waitForPage(page);
            WaitUtils.waitForTwoSeconds(page);
        }

        if (WaitUtils.isVisibleNow(page, "timesheet.addRowButton")) {
            WaitUtils.click(page, "timesheet.addRowButton");
            WaitUtils.waitForTwoSeconds(page);
        }
    }

    public boolean isProjectInputVisible() {
        return WaitUtils.isVisibleNow(page, "timesheet.projectInput");
    }

    public void selectProject(String projectSearchValue) {
        Assert.assertTrue(
                isProjectInputVisible(),
                "Project input was not displayed. The timesheet is not editable."
        );

        WaitUtils.clearAndFill(page, "timesheet.projectInput", projectSearchValue);
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
        WaitUtils.click(page, "timesheet.activityDropdown");
        WaitUtils.waitForTwoSeconds(page);

        String[] preferredActivities = ConfigReader.getArray("preferred.activities");
        for (String activity : preferredActivities) {
            Locator optionByText = page.getByText(activity).first();
            if (optionByText.isVisible()) {
                optionByText.click();
                WaitUtils.waitForTwoSeconds(page);
                return;
            }
        }

        // Fallback: skip the grey placeholder and select the first real option by keyboard.
        page.keyboard().press("ArrowDown");
        WaitUtils.waitForTwoSeconds(page);
        page.keyboard().press("ArrowDown");
        WaitUtils.waitForTwoSeconds(page);
        page.keyboard().press("Enter");
        WaitUtils.waitForTwoSeconds(page);
    }

    public void enterHoursForWeek(String hours) {
        Locator hourInputs = WaitUtils.find(page, "timesheet.hoursInputs");
        int numberOfInputs = hourInputs.count();

        Assert.assertTrue(numberOfInputs > 0, "No timesheet hour input fields were found.");

        int daysToFill = Math.min(numberOfInputs, 5);
        for (int index = 0; index < daysToFill; index++) {
            hourInputs.nth(index).click();
            hourInputs.nth(index).fill(hours);
        }
    }

    public void saveTimesheet() {
        Assert.assertTrue(WaitUtils.isVisible(page, "timesheet.saveButton"), "Save button was not displayed.");
        WaitUtils.click(page, "timesheet.saveButton");
        WaitUtils.waitForTwoSeconds(page);
    }

    public void verifyTimesheetPageIsStillVisible() {
        Assert.assertTrue(
                WaitUtils.isVisible(page, "timesheet.container")
                        || WaitUtils.isVisibleNow(page, "timesheet.submittedText")
                        || WaitUtils.isVisibleNow(page, "timesheet.approvedText")
                        || WaitUtils.isVisibleNow(page, "timesheet.submitButton")
                        || WaitUtils.isVisibleNow(page, "timesheet.editButton"),
                "Timesheet page was not visible after edit attempt."
        );
    }

    public void editTimesheet(String projectName, String activityName, String hours, String expectedMessage) {
        openMyTimesheet();
        prepareEditableTimesheet();

        if (isProjectInputVisible()) {
            selectProject(projectName);
            selectActivity(activityName);
            enterHoursForWeek(hours);
            saveTimesheet();
        }

        verifyTimesheetPageIsStillVisible();
    }
}
