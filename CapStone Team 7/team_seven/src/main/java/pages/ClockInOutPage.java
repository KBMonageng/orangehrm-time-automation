package pages;

import com.microsoft.playwright.Page;
import org.testng.Assert;
import utils.ConfigReader;
import utils.WaitUtils;

public class ClockInOutPage {

    private Page page;

    public ClockInOutPage(Page page) {
        this.page = page;
    }

    public void openPunchInPage() {
        page.navigate(ConfigReader.getProperty("punchInUrl"));
        WaitUtils.waitForPage(page);
    }

    public void openPunchOutPage() {
        page.navigate(ConfigReader.getProperty("punchOutUrl"));
        WaitUtils.waitForPage(page);
    }

    public boolean isPunchInPageDisplayed() {
        return WaitUtils.isVisible(page, "punch.inHeading");
    }

    public boolean isPunchOutPageDisplayed() {
        return WaitUtils.isVisible(page, "punch.outHeading");
    }

    public void enterPunchComment(String comment) {
        WaitUtils.fill(page, "punch.comment", comment);
    }

    public void clickInButton() {
        WaitUtils.click(page, "punch.inButton");
    }

    public void clickOutButton() {
        WaitUtils.click(page, "punch.outButton");
    }

    public void verifySuccessMessage(String expectedMessage) {
        String actualMessage = WaitUtils.text(page, "punch.successMessage");

        Assert.assertTrue(
                actualMessage.contains(expectedMessage),
                "Expected message: " + expectedMessage + " but actual message was: " + actualMessage
        );
    }

    public void punchIn(String punchInComment, String expectedMessage) {
        openPunchInPage();

        if (isPunchInPageDisplayed()) {
            enterPunchComment(punchInComment);
            clickInButton();
            verifySuccessMessage(expectedMessage);
        }
    }

    public void punchOut(String punchOutComment, String expectedMessage) {
        openPunchOutPage();

        Assert.assertTrue(isPunchOutPageDisplayed(), "Punch Out page was not displayed.");

        enterPunchComment(punchOutComment);
        clickOutButton();
        verifySuccessMessage(expectedMessage);
    }

    public void completePunchInOutFlow(String punchInComment, String punchOutComment, String expectedMessage) {
        punchIn(punchInComment, expectedMessage);
        punchOut(punchOutComment, expectedMessage);
    }
}