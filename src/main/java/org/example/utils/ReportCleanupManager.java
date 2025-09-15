package org.example.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class to manage report cleanup - keeps only the latest N reports
 * and automatically deletes older ones to save disk space
 */
public class ReportCleanupManager {
    
    private static final int DEFAULT_MAX_REPORTS = 3;
    private static final String REPORT_PREFIX = "Enhanced_AutomationReport_";
    private static final String REPORT_EXTENSION = ".html";
    
    /**
     * Cleans up old reports, keeping only the specified number of latest reports
     * 
     * @param reportDirectory The directory containing reports
     * @param maxReportsToKeep Maximum number of reports to keep (default: 3)
     * @return CleanupResult containing information about the cleanup operation
     */
    public static CleanupResult cleanupOldReports(String reportDirectory, int maxReportsToKeep) {
        CleanupResult result = new CleanupResult();
        
        try {
            File reportDir = new File(reportDirectory);
            
            // Create directory if it doesn't exist
            if (!reportDir.exists()) {
                System.out.println("[INFO] Report directory doesn't exist, creating: " + reportDirectory);
                reportDir.mkdirs();
                result.setMessage("Report directory created: " + reportDirectory);
                return result;
            }
            
            // Get all report files
            File[] reportFiles = reportDir.listFiles((dir, name) -> 
                name.startsWith(REPORT_PREFIX) && name.endsWith(REPORT_EXTENSION)
            );
            
            if (reportFiles == null || reportFiles.length == 0) {
                result.setMessage("No reports found in directory: " + reportDirectory);
                return result;
            }
            
            result.setTotalReportsFound(reportFiles.length);
            
            // If we have fewer reports than the limit, no cleanup needed
            if (reportFiles.length <= maxReportsToKeep) {
                result.setMessage(String.format("[INFO] Only %d report(s) found, no cleanup needed (keeping latest %d)", 
                    reportFiles.length, maxReportsToKeep));
                return result;
            }
            
            // Sort files by last modified time (newest first)
            List<File> sortedReports = Arrays.stream(reportFiles)
                .sorted((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()))
                .collect(Collectors.toList());
            
            // Keep the latest N reports, delete the rest
            List<File> reportsToKeep = sortedReports.subList(0, maxReportsToKeep);
            List<File> reportsToDelete = sortedReports.subList(maxReportsToKeep, sortedReports.size());
            
            result.setReportsKept(reportsToKeep.size());
            result.setReportsDeleted(reportsToDelete.size());
            
            // Log reports being kept
            System.out.println("[CLEANUP] Reports being kept (latest " + maxReportsToKeep + "):");
            for (int i = 0; i < reportsToKeep.size(); i++) {
                File report = reportsToKeep.get(i);
                String timestamp = extractTimestampFromFilename(report.getName());
                String formattedTime = formatTimestamp(timestamp);
                System.out.println(String.format("  %d. %s (%s)", 
                    i + 1, report.getName(), formattedTime));
            }
            
            // Delete old reports
            if (!reportsToDelete.isEmpty()) {
                System.out.println("[CLEANUP] Deleting " + reportsToDelete.size() + " old report(s):");
                
                for (File report : reportsToDelete) {
                    try {
                        String timestamp = extractTimestampFromFilename(report.getName());
                        String formattedTime = formatTimestamp(timestamp);
                        
                        if (report.delete()) {
                            System.out.println("  [OK] Deleted: " + report.getName() + " (" + formattedTime + ")");
                            result.addDeletedReport(report.getName());
                        } else {
                            System.out.println("  [ERROR] Failed to delete: " + report.getName());
                            result.addFailedDeletion(report.getName());
                        }
                    } catch (Exception e) {
                        System.out.println("  [ERROR] Error deleting " + report.getName() + ": " + e.getMessage());
                        result.addFailedDeletion(report.getName());
                    }
                }
            }
            
            // Set final result message
            if (result.getFailedDeletions().isEmpty()) {
                result.setMessage(String.format("[SUCCESS] Cleanup completed successfully! Kept %d latest reports, deleted %d old reports", 
                    result.getReportsKept(), result.getReportsDeleted()));
            } else {
                result.setMessage(String.format("[WARNING] Cleanup completed with %d failed deletions. Kept %d reports, deleted %d reports", 
                    result.getFailedDeletions().size(), result.getReportsKept(), 
                    result.getReportsDeleted() - result.getFailedDeletions().size()));
            }
            
        } catch (Exception e) {
            result.setMessage("[ERROR] Error during cleanup: " + e.getMessage());
            System.err.println("Error during report cleanup: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * Cleans up old reports using the default maximum (3 reports)
     */
    public static CleanupResult cleanupOldReports(String reportDirectory) {
        return cleanupOldReports(reportDirectory, DEFAULT_MAX_REPORTS);
    }
    
    /**
     * Cleans up old reports using configuration value
     */
    public static CleanupResult cleanupOldReportsFromConfig() {
        String reportPath = ConfigReader.getReportPath();
        int maxReports = getMaxReportsFromConfig();
        return cleanupOldReports(reportPath, maxReports);
    }
    
    /**
     * Gets the maximum reports to keep from configuration, with fallback to default
     */
    private static int getMaxReportsFromConfig() {
        try {
            String maxReportsProperty = ConfigReader.getProperty("report.max.keep");
            if (maxReportsProperty != null && !maxReportsProperty.trim().isEmpty()) {
                return Integer.parseInt(maxReportsProperty.trim());
            }
        } catch (NumberFormatException e) {
            System.out.println("[WARNING] Invalid report.max.keep configuration, using default: " + DEFAULT_MAX_REPORTS);
        }
        return DEFAULT_MAX_REPORTS;
    }
    
    /**
     * Extracts timestamp from report filename
     * Example: Enhanced_AutomationReport_2025-09-15_23-04-49.html -> 2025-09-15_23-04-49
     */
    private static String extractTimestampFromFilename(String filename) {
        try {
            if (filename.startsWith(REPORT_PREFIX) && filename.endsWith(REPORT_EXTENSION)) {
                return filename.substring(REPORT_PREFIX.length(), filename.length() - REPORT_EXTENSION.length());
            }
        } catch (Exception e) {
            // If extraction fails, return the filename itself
        }
        return filename;
    }
    
    /**
     * Formats timestamp for better readability
     * Example: 2025-09-15_23-04-49 -> Sep 15, 2025 11:04:49 PM
     */
    private static String formatTimestamp(String timestamp) {
        try {
            // Parse the timestamp format: yyyy-MM-dd_HH-mm-ss
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            LocalDateTime dateTime = LocalDateTime.parse(timestamp, inputFormatter);
            
            // Format for display
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm:ss a");
            return dateTime.format(outputFormatter);
        } catch (Exception e) {
            // If formatting fails, return original timestamp
            return timestamp;
        }
    }
    
    /**
     * Result class to hold cleanup operation information
     */
    public static class CleanupResult {
        private int totalReportsFound = 0;
        private int reportsKept = 0;
        private int reportsDeleted = 0;
        private List<String> deletedReports = new java.util.ArrayList<>();
        private List<String> failedDeletions = new java.util.ArrayList<>();
        private String message = "";
        
        // Getters and setters
        public int getTotalReportsFound() { return totalReportsFound; }
        public void setTotalReportsFound(int totalReportsFound) { this.totalReportsFound = totalReportsFound; }
        
        public int getReportsKept() { return reportsKept; }
        public void setReportsKept(int reportsKept) { this.reportsKept = reportsKept; }
        
        public int getReportsDeleted() { return reportsDeleted; }
        public void setReportsDeleted(int reportsDeleted) { this.reportsDeleted = reportsDeleted; }
        
        public List<String> getDeletedReports() { return deletedReports; }
        public void addDeletedReport(String reportName) { this.deletedReports.add(reportName); }
        
        public List<String> getFailedDeletions() { return failedDeletions; }
        public void addFailedDeletion(String reportName) { this.failedDeletions.add(reportName); }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        @Override
        public String toString() {
            return String.format("CleanupResult{total=%d, kept=%d, deleted=%d, failed=%d, message='%s'}", 
                totalReportsFound, reportsKept, reportsDeleted, failedDeletions.size(), message);
        }
    }
}
