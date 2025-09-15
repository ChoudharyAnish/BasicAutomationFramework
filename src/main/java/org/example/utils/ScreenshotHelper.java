package org.example.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
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
            
            // Clean up old screenshots (keep only latest 3)
            cleanupOldScreenshots(screenshotDir);
            
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
    
    /**
     * Clean up old screenshots, keeping only the latest 3
     */
    private static void cleanupOldScreenshots(String screenshotDir) {
        try {
            File directory = new File(screenshotDir);
            File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
            
            if (files != null && files.length > 3) {
                // Sort files by last modified date (newest first)
                Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
                
                // Delete files beyond the latest 3
                for (int i = 3; i < files.length; i++) {
                    if (files[i].delete()) {
                        System.out.println("Deleted old screenshot: " + files[i].getName());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error cleaning up screenshots: " + e.getMessage());
        }
    }
}
