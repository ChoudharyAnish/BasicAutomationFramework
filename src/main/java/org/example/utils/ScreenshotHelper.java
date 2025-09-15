package org.example.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for taking screenshots
 */
public class ScreenshotHelper {
    
    public static String captureScreenshot(WebDriver driver, String testName) {
        try {
            // Create screenshots directory if it doesn't exist
            String screenshotDir = ConfigReader.getScreenshotPath();
            File directory = new File(screenshotDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Generate timestamp for unique filename
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String fileName = testName + "_" + timestamp + ".png";
            String filePath = screenshotDir + fileName;

            // Take screenshot
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            byte[] sourceFile = takesScreenshot.getScreenshotAs(OutputType.BYTES);
            
            // Save screenshot
            Files.write(Paths.get(filePath), sourceFile);
            
            return filePath;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to capture screenshot: " + e.getMessage());
        }
    }

    public static String captureScreenshotAsBase64(WebDriver driver) {
        TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
        return takesScreenshot.getScreenshotAs(OutputType.BASE64);
    }

    public static void captureScreenshotOnFailure(WebDriver driver, String testName) {
        try {
            String screenshotPath = captureScreenshot(driver, testName + "_FAILED");
            System.out.println("Screenshot captured for failed test: " + screenshotPath);
        } catch (Exception e) {
            System.err.println("Failed to capture screenshot on test failure: " + e.getMessage());
        }
    }
}
