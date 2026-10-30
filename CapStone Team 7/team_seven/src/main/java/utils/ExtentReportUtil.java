package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import java.io.File;

public class ExtentReportUtil {
    private static ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    private ExtentReportUtil() {
    }

    public static void startReport() {
        String reportFolder = ConfigReader.get("report.folder");
        File folder = new File(reportFolder);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        String reportPath = reportFolder + File.separator + ConfigReader.get("report.name");
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setDocumentTitle("Team Seven Time Module Report");
        sparkReporter.config().setReportName("OrangeHRM Team Seven Time Module Tests");

        extentReports = new ExtentReports();
        extentReports.attachReporter(sparkReporter);
        extentReports.setSystemInfo("Project", "OrangeHRM Capstone");
        extentReports.setSystemInfo("Team", "Team 7 - Time Module");
    }

    public static void createTest(String testName) {
        test.set(extentReports.createTest(testName));
    }

    public static void pass(String message) {
        test.get().pass(message);
    }

    public static void fail(String message, String screenshotPath) {
        test.get().fail(message);

        if (screenshotPath != null && screenshotPath.endsWith(".png")) {
            try {
                test.get().addScreenCaptureFromPath(screenshotPath);
            } catch (Exception error) {
                test.get().warning("Screenshot could not be attached: " + error.getMessage());
            }
        }
    }

    public static void skip(String message) {
        test.get().skip(message);
    }

    public static void flushReport() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }
}
