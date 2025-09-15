package org.example.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class to read configuration properties
 */
public class ConfigReader {
    private static Properties properties;
    private static final String CONFIG_FILE_PATH = "src/main/resources/config.properties";

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try {
            properties = new Properties();
            FileInputStream fileInputStream = new FileInputStream(CONFIG_FILE_PATH);
            properties.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load config.properties file");
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getBrowser() {
        return getProperty("browser");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless"));
    }

    public static String getBaseUrl() {
        return getProperty("base.url");
    }

    public static int getImplicitWait() {
        return Integer.parseInt(getProperty("implicit.wait"));
    }

    public static int getExplicitWait() {
        return Integer.parseInt(getProperty("explicit.wait"));
    }

    public static int getPageLoadTimeout() {
        return Integer.parseInt(getProperty("page.load.timeout"));
    }

    public static String getTestDataFile() {
        return getProperty("test.data.file");
    }

    public static String getReportPath() {
        return getProperty("report.path");
    }

    public static String getReportName() {
        return getProperty("report.name");
    }

    public static String getScreenshotPath() {
        return getProperty("screenshot.path");
    }

    public static int getThreadCount() {
        return Integer.parseInt(getProperty("thread.count"));
    }

    public static boolean isCIEnvironment() {
        return Boolean.parseBoolean(getProperty("ci.environment"));
    }

    public static int getRetryCount() {
        return Integer.parseInt(getProperty("retry.count"));
    }
}
