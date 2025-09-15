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

    // Telegram Configuration (with environment variable fallback)
    public static String getTelegramBotToken() {
        String token = getProperty("telegram.bot.token");
        // If token contains ${}, try to get from environment variable
        if (token != null && token.contains("${TELEGRAM_BOT_TOKEN}")) {
            String envToken = System.getenv("TELEGRAM_BOT_TOKEN");
            if (envToken == null || envToken.isEmpty()) {
                System.err.println("⚠️ TELEGRAM_BOT_TOKEN environment variable not set! Telegram notifications will be disabled.");
                return null;
            }
            return envToken;
        }
        return token;
    }

    public static String getTelegramChatId() {
        String chatId = getProperty("telegram.chat.id");
        // If chatId contains ${}, try to get from environment variable
        if (chatId != null && chatId.contains("${TELEGRAM_CHAT_ID}")) {
            String envChatId = System.getenv("TELEGRAM_CHAT_ID");
            if (envChatId == null || envChatId.isEmpty()) {
                System.err.println("⚠️ TELEGRAM_CHAT_ID environment variable not set! Telegram notifications will be disabled.");
                return null;
            }
            return envChatId;
        }
        return chatId;
    }

    public static boolean isTelegramEnabled() {
        return Boolean.parseBoolean(getProperty("telegram.enabled"));
    }
}
