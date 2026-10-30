package utils;

import com.microsoft.playwright.Page;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtil {
    private ScreenshotUtil() {
    }

    public static String captureScreenshot(Page page, String testName) {
        if (page == null) {
            return "No screenshot captured because page was null.";
        }

        String folderPath = ConfigReader.get("screenshot.folder");
        File folder = new File(folderPath);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        String cleanTestName = testName.replaceAll("[^a-zA-Z0-9]", "_");
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String screenshotPath = folderPath + File.separator + cleanTestName + "_" + timestamp + ".png";

        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get(screenshotPath))
                .setFullPage(true));

        return screenshotPath;
    }
}
