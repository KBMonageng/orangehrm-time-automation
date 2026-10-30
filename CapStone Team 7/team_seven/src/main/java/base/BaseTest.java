package base;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import pages.LoginPage;
import utils.ConfigReader;
import utils.ExcelUtilityRead;
import utils.ExtentReportUtil;
import utils.LoggerUtil;
import utils.ScreenshotUtil;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Logger;

public class BaseTest {
    protected Page page;
    private DriverFactory driverFactory;
    private final Logger logger = LoggerUtil.getLogger(BaseTest.class);

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        ExtentReportUtil.startReport();
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        logger.info("Starting test: " + method.getName());
        ExtentReportUtil.createTest(method.getName());

        driverFactory = new DriverFactory();
        page = driverFactory.startBrowser();

        page.navigate(ConfigReader.get("app.url"), new Page.NavigateOptions()
                .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                .setTimeout(Double.parseDouble(ConfigReader.get("navigation.timeout"))));

        loginAsAdmin();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.SUCCESS) {
            ExtentReportUtil.pass("Test passed successfully.");
            logger.info("Test passed: " + result.getName());

        } else if (result.getStatus() == ITestResult.FAILURE) {
            logger.warning("Test failed: " + result.getName());

            try {
                String errorMessage = "Test failed.";

                if (result.getThrowable() != null && result.getThrowable().getMessage() != null) {
                    errorMessage = result.getThrowable().getMessage();
                }

                String screenshotPath = ScreenshotUtil.captureScreenshot(page, result.getName());
                ExtentReportUtil.fail(errorMessage, screenshotPath);

            } catch (Exception e) {
                logger.warning(
                        "Could not capture screenshot because page/browser was already closed: " + e.getMessage());

                String errorMessage = "Test failed, but screenshot could not be captured.";

                if (result.getThrowable() != null && result.getThrowable().getMessage() != null) {
                    errorMessage = result.getThrowable().getMessage();
                }

                ExtentReportUtil.fail(errorMessage, "");
            }

        } else if (result.getStatus() == ITestResult.SKIP) {
            ExtentReportUtil.skip("Test skipped: " + result.getName());
            logger.info("Test skipped: " + result.getName());
        }

        if (driverFactory != null) {
            driverFactory.closeBrowser();
        }
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        ExtentReportUtil.flushReport();
    }

    private void loginAsAdmin() {
        String excelPath = ConfigReader.get("login.excel.path");
        String sheetName = ConfigReader.get("login.sheet.name");
        String testCaseName = ConfigReader.get("valid.login.testcase");

        Map<String, String> loginData = ExcelUtilityRead.getRowByTestCase(excelPath, sheetName, testCaseName);

        LoginPage loginPage = new LoginPage(page);
        loginPage.login(loginData.get("username"), loginData.get("password"));
        loginPage.verifyDashboardIsDisplayed(loginData.get("expectedResult"));
    }
}
