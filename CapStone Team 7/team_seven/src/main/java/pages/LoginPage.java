package pages;

import com.microsoft.playwright.Page;
import org.testng.Assert;
import utils.WaitUtils;

public class LoginPage {
    private final Page page;

    public LoginPage(Page page) {
        this.page = page;
    }

    public void enterUsername(String username) {
        WaitUtils.fill(page, "login.username", username);
    }

    public void enterPassword(String password) {
        WaitUtils.fill(page, "login.password", password);
    }

    public void clickLoginButton() {
        WaitUtils.click(page, "login.button");
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
        WaitUtils.waitForPage(page);
    }

    public void verifyDashboardIsDisplayed(String expectedHeading) {
        String actualHeading = WaitUtils.text(page, "common.page.heading");
        Assert.assertTrue(actualHeading.contains(expectedHeading),
                "Expected dashboard heading to contain: " + expectedHeading + " but found: " + actualHeading);
    }
}
