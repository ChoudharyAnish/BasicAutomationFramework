package org.example.base;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.example.utils.ConfigReader;
import org.example.utils.EmailNotifier;
import org.example.utils.ExtentManager;
import org.example.utils.ScreenshotHelper;
import org.example.utils.TelegramNotifier;
import org.example.utils.WaitHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.time.Duration;

/**
 * Base test class with WebDriver setup and teardown
 */
public class BaseTest {
    protected static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    protected WaitHelper waitHelper;
    protected ExtentTest test;
    protected TelegramNotifier telegramNotifier;
    protected EmailNotifier emailNotifier;
    
    // Simple counters for test results
    protected static int totalTests = 0;
    protected static int passedTests = 0;
    protected static int failedTests = 0;
    protected static java.util.List<String> testResults = new java.util.ArrayList<>();
    protected static long suiteStartTime = 0;

    @BeforeSuite
    public void beforeSuite() {
        ExtentManager.getInstance();
        
        // Initialize simple counters
        totalTests = 0;
        passedTests = 0;
        failedTests = 0;
        testResults.clear();
        suiteStartTime = System.currentTimeMillis();
        
        // Initialize Telegram notifier if enabled
        if (ConfigReader.isTelegramEnabled()) {
            String botToken = ConfigReader.getTelegramBotToken();
            String chatId = ConfigReader.getTelegramChatId();
            
            if (botToken != null && chatId != null) {
                telegramNotifier = new TelegramNotifier(botToken, chatId);
                System.out.println("[INFO] Telegram notifications enabled");
            } else {
                System.out.println("[WARNING] Telegram notifications disabled - missing environment variables");
                telegramNotifier = null;
            }
        }
        
        // Initialize Email notifier if enabled
        if (ConfigReader.isEmailEnabled()) {
            emailNotifier = new EmailNotifier();
            System.out.println("[INFO] Email notifications enabled");
        }
    }

    @BeforeMethod
    public void setUp(ITestResult result) {
        // Record start time for duration calculation
        result.setAttribute("startTime", System.currentTimeMillis());
        
        // Create ExtentTest instance with enhanced formatting
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName().substring(result.getTestClass().getName().lastIndexOf('.') + 1);
        String description = getTestDescription(result.getMethod());
        
        test = ExtentManager.createTest("🧪 " + className + " → " + testName, description);
        test.assignCategory(className);
        test.assignDevice(ConfigReader.getBrowser().toUpperCase());
        
        test.info("🚀 <b>Starting Test Execution</b>");
        test.info("📋 Test: " + testName);
        test.info("📦 Class: " + className);
        test.info("⏰ Start Time: " + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()));
        
        // Send Telegram start notification
        if (telegramNotifier != null) {
            telegramNotifier.sendTestStartNotification(testName);
        }
        
        // Setup WebDriver
        setupDriver();
        
        // Initialize WaitHelper
        waitHelper = new WaitHelper(getDriver());
        
        // Navigate to base URL
        getDriver().get(ConfigReader.getBaseUrl());
        test.pass("🌐 Successfully navigated to: " + ConfigReader.getBaseUrl());
    }
    
    private String getTestDescription(org.testng.ITestNGMethod method) {
        String description = method.getDescription();
        if (description != null && !description.isEmpty()) {
            return "📝 " + description;
        }
        return "🔬 Automated test execution";
    }

    private void setupDriver() {
        String browser = ConfigReader.getBrowser().toLowerCase();
        
        switch (browser) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                
                if (ConfigReader.isHeadless()) {
                    chromeOptions.addArguments("--headless");
                }
                
                // Chrome options for better stability
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--window-size=1920,1080");
                chromeOptions.addArguments("--disable-extensions");
                chromeOptions.addArguments("--disable-web-security");
                chromeOptions.addArguments("--allow-running-insecure-content");
                
                // For CI/CD environments
                if (ConfigReader.isCIEnvironment()) {
                    chromeOptions.addArguments("--headless");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    chromeOptions.addArguments("--no-sandbox");
                }
                
                driver.set(new ChromeDriver(chromeOptions));
                break;
                
            default:
                throw new RuntimeException("Browser '" + browser + "' is not supported");
        }
        
        // Set timeouts
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
        getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getPageLoadTimeout()));
        getDriver().manage().window().maximize();
        
        test.log(Status.INFO, "Browser '" + browser + "' launched successfully");
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        long endTime = System.currentTimeMillis();
        long duration = endTime - (long) result.getAttribute("startTime");
        
        if (result.getStatus() == ITestResult.FAILURE) {
            // Capture screenshot on failure
            String screenshotPath = ScreenshotHelper.captureScreenshot(getDriver(), result.getMethod().getMethodName());
            test.fail("❌ <b>TEST FAILED</b>");
            test.fail("<details><summary><b>Error Details</b></summary>" + 
                     "<pre>" + result.getThrowable().getMessage() + "</pre></details>");
            test.addScreenCaptureFromPath(screenshotPath, "💥 Failure Screenshot");
            test.info("⏱️ Test Duration: " + formatDuration(duration));
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.pass("✅ <b>TEST PASSED SUCCESSFULLY</b>");
            test.pass("🎉 All assertions completed without errors");
            test.info("⏱️ Test Duration: " + formatDuration(duration));
        } else if (result.getStatus() == ITestResult.SKIP) {
            test.skip("⏭️ <b>TEST SKIPPED</b>");
            test.skip("📝 Reason: " + result.getThrowable().getMessage());
        }
        
        // Add test execution summary
        test.info("🏁 Test execution completed at: " + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()));
        
        // Track test results with simple counters (no individual notifications)
        String testName = result.getMethod().getMethodName();
        totalTests++;
        
        if (result.getStatus() == ITestResult.SUCCESS) {
            passedTests++;
            testResults.add("[PASS] " + testName + " (" + formatDuration(duration) + ")");
        } else {
            failedTests++;
            String errorMsg = result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown error";
            testResults.add("[FAIL] " + testName + " (" + formatDuration(duration) + ")\n   ERROR: " + errorMsg);
        }
        
        // Close browser
        if (getDriver() != null) {
            getDriver().quit();
            driver.remove();
        }
        
        // Remove ExtentTest instance
        ExtentManager.removeTest();
    }
    
    private String formatDuration(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        
        if (minutes > 0) {
            return String.format("%d min %d sec", minutes, seconds);
        } else {
            return String.format("%d.%03d sec", seconds, milliseconds % 1000);
        }
    }

    @AfterSuite
    public void afterSuite() {
        ExtentManager.flush();
        String reportPath = ExtentManager.getReportPath();
        
        // Calculate suite metrics
        long suiteDuration = System.currentTimeMillis() - suiteStartTime;
        double successRate = totalTests > 0 ? (passedTests * 100.0 / totalTests) : 0.0;
        String timestamp = new java.text.SimpleDateFormat("MMM dd, yyyy hh:mm:ss a").format(new java.util.Date());
        
        System.out.println("Test execution completed. Report generated at: " + reportPath);
        
        // Send consolidated Telegram notification
        if (telegramNotifier != null) {
            System.out.println("[TELEGRAM] Sending consolidated notification...");
            String telegramMessage = buildTelegramSummary(successRate, suiteDuration, timestamp, reportPath);
            telegramNotifier.sendMessage(telegramMessage);
        }
        
        // Send consolidated Email notification  
        if (emailNotifier != null) {
            System.out.println("[EMAIL] Sending consolidated notification...");
            String emailSummary = buildEmailSummary(successRate, suiteDuration, timestamp);
            emailNotifier.sendTestReport("FlipkartSearchTests", failedTests == 0, suiteDuration, reportPath);
        }
        
        // Print suite summary to console
        System.out.println("\n" + "=".repeat(60));
        System.out.println("*** TEST SUITE SUMMARY ***");
        System.out.println("=".repeat(60));
        System.out.printf("Suite: FlipkartSearchTests%n");
        System.out.printf("[PASS] Passed: %d tests%n", passedTests);
        System.out.printf("[FAIL] Failed: %d tests%n", failedTests);
        System.out.printf("Success Rate: %.1f%%%n", successRate);
        System.out.printf("Duration: %s%n", formatDuration(suiteDuration));
        System.out.printf("Completed: %s%n", timestamp);
        System.out.println("=".repeat(60));
    }
    
    /**
     * Build Telegram summary message
     */
    private String buildTelegramSummary(double successRate, long duration, String timestamp, String reportPath) {
        StringBuilder message = new StringBuilder();
        
        // Header
        message.append("*** Test Suite Completed: FlipkartSearchTests ***\n\n");
        
        // Summary
        message.append("EXECUTION SUMMARY:\n");
        message.append("[PASS] Passed: ").append(passedTests).append(" tests\n");
        message.append("[FAIL] Failed: ").append(failedTests).append(" tests\n");
        message.append("Success Rate: ").append(String.format("%.1f%%", successRate)).append("\n\n");
        
        // Timing
        message.append("Duration: ").append(formatDuration(duration)).append("\n");
        message.append("Completed: ").append(timestamp).append("\n\n");
        
        // Test Details
        message.append("TEST DETAILS:\n");
        for (String testResult : testResults) {
            message.append(testResult).append("\n");
        }
        
        // Report
        String reportName = reportPath.substring(reportPath.lastIndexOf("/") + 1);
        message.append("\nReport: ").append(reportName);
        message.append("\n\nAutomated by BasicAutomationFramework");
        
        return message.toString();
    }
    
    /**
     * Build Email summary for subject line
     */
    private String buildEmailSummary(double successRate, long duration, String timestamp) {
        return String.format("FlipkartSearchTests - %d/%d Passed (%.1f%%) %s", 
            passedTests, totalTests, successRate, failedTests == 0 ? "[PASS]" : "[FAIL]");
    }

    // Enhanced utility methods for tests
    protected void logInfo(String message) {
        test.info("ℹ️ " + message);
    }

    protected void logPass(String message) {
        test.pass("✅ " + message);
    }

    protected void logFail(String message) {
        test.fail("❌ " + message);
    }

    protected void logWarning(String message) {
        test.warning("⚠️ " + message);
    }
    
    protected void logStep(String stepDescription) {
        test.info("👣 <b>Step:</b> " + stepDescription);
    }
    
    protected void logAction(String action) {
        test.info("🎯 <b>Action:</b> " + action);
    }
    
    protected void logVerification(String verification) {
        test.info("🔍 <b>Verification:</b> " + verification);
    }
    
    protected void logDebug(String debugInfo) {
        test.info("🐛 <b>Debug:</b> " + debugInfo);
    }
}
