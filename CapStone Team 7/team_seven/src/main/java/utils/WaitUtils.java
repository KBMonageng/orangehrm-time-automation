package utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;

public class WaitUtils {

    private WaitUtils() {
    }

    public static void waitForPage(Page page) {
        try {
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        } catch (Exception ignored) {
            // OrangeHRM demo sometimes keeps background requests open. The element-level waits below are the real synchronisation.
        }
    }

    public static void waitForSeconds(Page page, int seconds) {
        page.waitForTimeout(seconds * 1000.0);
    }

    public static void waitForTwoSeconds(Page page) {
        waitForSeconds(page, 2);
    }

    public static void waitForFiveSeconds(Page page) {
        waitForSeconds(page, 5);
    }

    public static Locator find(Page page, String locatorKey) {
        String actualLocator = LocatorLoader.getLocator(locatorKey);
        return page.locator(actualLocator).first();
    }

    public static void click(Page page, String locatorKey) {
        Locator element = find(page, locatorKey);
        element.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        element.click();
    }

    public static void fill(Page page, String locatorKey, String value) {
        Locator element = find(page, locatorKey);
        element.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        element.fill(value == null ? "" : value);
    }

    public static void clearAndFill(Page page, String locatorKey, String value) {
        Locator element = find(page, locatorKey);
        element.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        element.click();
        element.press("Control+A");
        element.press("Backspace");
        element.fill(value == null ? "" : value);
    }

    public static String text(Page page, String locatorKey) {
        Locator element = find(page, locatorKey);
        element.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        return element.innerText();
    }

    public static boolean isVisible(Page page, String locatorKey) {
        try {
            Locator element = find(page, locatorKey);
            element.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(8000));
            return element.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isVisibleNow(Page page, String locatorKey) {
        try {
            return find(page, locatorKey).isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isDisplayed(Page page, String locatorKey) {
        return isVisibleNow(page, locatorKey);
    }

    public static String optionalText(Page page, String locatorKey) {
        try {
            if (isVisibleNow(page, locatorKey)) {
                return text(page, locatorKey);
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    public static void selectFirstOptionAfterTyping(Page page, String locatorKey, String value) {
        clearAndFill(page, locatorKey, value);
        waitForTwoSeconds(page);
        page.keyboard().press("ArrowDown");
        page.keyboard().press("Enter");
    }

    public static boolean urlContains(Page page, String expectedText) {
        return page.url().contains(expectedText);
    }
}
