package base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import utils.ConfigReader;

public class DriverFactory {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    public Page startBrowser() {
        playwright = Playwright.create();

        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(ConfigReader.getBoolean("headless"))
                .setSlowMo(Double.parseDouble(ConfigReader.get("slow.mo")));

        String browserName = ConfigReader.get("browser").toLowerCase();

        if (browserName.equals("firefox")) {
            browser = playwright.firefox().launch(launchOptions);
        } else if (browserName.equals("webkit")) {
            browser = playwright.webkit().launch(launchOptions);
        } else {
            browser = playwright.chromium().launch(launchOptions);
        }

        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1366, 768));

        page = context.newPage();
        page.setDefaultTimeout(Double.parseDouble(ConfigReader.get("default.timeout")));
        page.setDefaultNavigationTimeout(Double.parseDouble(ConfigReader.get("navigation.timeout")));

        return page;
    }

    public void closeBrowser() {
        try {
            if (context != null) {
                context.close();
            }
        } catch (Exception ignored) {
        }

        try {
            if (browser != null) {
                browser.close();
            }
        } catch (Exception ignored) {
        }

        try {
            if (playwright != null) {
                playwright.close();
            }
        } catch (Exception ignored) {
        }
    }
}
