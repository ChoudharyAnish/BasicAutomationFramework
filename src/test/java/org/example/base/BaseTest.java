package org.example.base;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.example.utils.ConfigReader;
import org.example.utils.ExtentManager;
import org.example.utils.ScreenshotHelper;
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

    @BeforeSuite
    public void beforeSuite() {
        ExtentManager.getInstance();
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
        System.out.println("Test execution completed. Report generated at: " + ExtentManager.getReportPath());
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
