package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties;

    static {
        try {
            properties = new Properties();

            FileInputStream file = new FileInputStream("config.properties");
            properties.load(file);
            file.close();

        } catch (IOException e) {
            throw new RuntimeException("Could not load config.properties file: " + e.getMessage());
        }
    }

    public static String get(String key) {
        String value = properties.getProperty(key);

        if (value == null) {
            throw new RuntimeException("Property not found in config.properties for key: " + key);
        }

        return value;
    }

    public static String getProperty(String key) {
        return get(key);
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static String[] getArray(String key) {
        String value = get(key);
        String[] rawValues = value.split("\\|");

        for (int index = 0; index < rawValues.length; index++) {
            rawValues[index] = rawValues[index].trim();
        }

        return rawValues;
    }
}