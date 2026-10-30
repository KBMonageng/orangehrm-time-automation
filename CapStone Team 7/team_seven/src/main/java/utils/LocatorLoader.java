package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LocatorLoader {

    private static Properties properties;

    static {
        try {
            properties = new Properties();

            FileInputStream file = new FileInputStream("locators.properties");
            properties.load(file);

        } catch (IOException e) {
            throw new RuntimeException("Could not load locators.properties file: " + e.getMessage());
        }
    }

    public static String getLocator(String key) {
        String locator = properties.getProperty(key);

        if (locator == null) {
            throw new RuntimeException("Locator not found in locators.properties for key: " + key);
        }

        return locator;
    }
}