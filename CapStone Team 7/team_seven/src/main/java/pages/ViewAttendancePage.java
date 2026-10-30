package pages;

import com.microsoft.playwright.Page;
import org.testng.Assert;
import utils.WaitUtils;

public class ViewAttendancePage {

    private Page page;

    public ViewAttendancePage(Page page) {
        this.page = page;
    }

    public void openTimeModule() {
        WaitUtils.click(page, "attendance.timeMenu");
        WaitUtils.waitForPage(page);
    }

    public void openAttendanceMenu() {
        WaitUtils.click(page, "attendance.attendanceMenu");

        Assert.assertTrue(
                WaitUtils.isVisible(page, "attendance.myRecordsMenu"),
                "My Records option was not displayed after clicking Attendance."
        );
    }

    public void openMyRecordsPage() {
        WaitUtils.click(page, "attendance.myRecordsMenu");
        WaitUtils.waitForPage(page);
    }

    public void clickViewButton() {
        WaitUtils.click(page, "attendance.viewButton");
    }

    public boolean areAttendanceRecordsDisplayed() {
        return WaitUtils.isVisible(page, "attendance.recordsContainer");
    }

    public void viewMyAttendanceRecords() {
        openTimeModule();
        openAttendanceMenu();
        openMyRecordsPage();
        clickViewButton();
    }

    public void verifyAttendanceRecordsAreDisplayed() {
        Assert.assertTrue(
                areAttendanceRecordsDisplayed(),
                "Attendance records were not displayed."
        );
    }
}