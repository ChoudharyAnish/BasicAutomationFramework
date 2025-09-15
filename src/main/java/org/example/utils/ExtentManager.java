package org.example.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ExtentReports manager for test reporting
 */
public class ExtentManager {
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static String reportPath;

    public static ExtentReports getInstance() {
        if (extent == null) {
            createInstance();
        }
        return extent;
    }

    private static void createInstance() {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        reportPath = ConfigReader.getReportPath() + "Enhanced_AutomationReport_" + timestamp + ".html";
        
        // Create directory if it doesn't exist
        File reportDir = new File(ConfigReader.getReportPath());
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }
        
        // Clean up old reports before creating new one
        cleanupOldReports();

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        
        // Enhanced Theme and Styling - Use STANDARD for better readability
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setDocumentTitle("🚀 Advanced Test Automation Report");
        sparkReporter.config().setReportName("📊 Test Execution Dashboard");
        sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
        
        // Enhanced Styling with CSS
        sparkReporter.config().setCss(getCustomCSS());
        sparkReporter.config().setJs(getCustomJS());
        
        // Report Configuration
        sparkReporter.config().setEncoding("utf-8");
        sparkReporter.config().setProtocol(com.aventstack.extentreports.reporter.configuration.Protocol.HTTPS);

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        
        // Enhanced System Information with Icons
        extent.setSystemInfo("🖥️ Operating System", System.getProperty("os.name") + " " + System.getProperty("os.version"));
        extent.setSystemInfo("☕ Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("🌐 Browser", ConfigReader.getBrowser().toUpperCase());
        extent.setSystemInfo("🔗 Test Environment", ConfigReader.getBaseUrl());
        extent.setSystemInfo("👤 Executed By", System.getProperty("user.name"));
        extent.setSystemInfo("📅 Execution Date", new SimpleDateFormat("EEEE, MMMM dd, yyyy").format(new Date()));
        extent.setSystemInfo("⏰ Start Time", new SimpleDateFormat("HH:mm:ss").format(new Date()));
        extent.setSystemInfo("🏗️ Framework", "Selenium WebDriver + TestNG + Maven");
        extent.setSystemInfo("📋 Report Version", "Enhanced v2.0");
    }
    
    /**
     * Custom CSS for enhanced visual styling
     */
    private static String getCustomCSS() {
        return """
            /* Professional Report Styling for Better Readability */
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background-color: #f8f9fa;
                color: #212529;
            }
            
            /* Header Enhancement */
            .navbar-brand {
                font-weight: 700;
                color: #2c3e50 !important;
                font-size: 1.5rem;
            }
            
            .navbar {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            }
            
            /* Dashboard Cards with High Contrast */
            .card-panel {
                border-radius: 12px;
                box-shadow: 0 4px 15px rgba(0,0,0,0.08);
                border: 1px solid #e9ecef;
                background-color: #ffffff;
                transition: all 0.3s ease;
                margin-bottom: 20px;
            }
            
            .card-panel:hover {
                transform: translateY(-3px);
                box-shadow: 0 8px 25px rgba(0,0,0,0.15);
            }
            
            /* High Contrast Status Styling */
            .test-pass, .pass {
                background-color: #28a745 !important;
                color: #ffffff !important;
                font-weight: 600 !important;
                padding: 8px 16px !important;
                border-radius: 6px !important;
                border: 2px solid #1e7e34 !important;
            }
            
            .test-fail, .fail {
                background-color: #dc3545 !important;
                color: #ffffff !important;
                font-weight: 600 !important;
                padding: 8px 16px !important;
                border-radius: 6px !important;
                border: 2px solid #c82333 !important;
            }
            
            .test-skip, .skip {
                background-color: #ffc107 !important;
                color: #212529 !important;
                font-weight: 600 !important;
                padding: 8px 16px !important;
                border-radius: 6px !important;
                border: 2px solid #e0a800 !important;
            }
            
            /* Test Content Readability */
            .test-content {
                background-color: #ffffff;
                border: 1px solid #dee2e6;
                border-radius: 8px;
                padding: 20px;
                margin: 15px 0;
                line-height: 1.6;
                color: #495057;
            }
            
            /* Step Details with Better Contrast */
            .step-details {
                background-color: #f8f9fa;
                border-left: 4px solid #007bff;
                padding: 15px;
                margin: 10px 0;
                border-radius: 0 8px 8px 0;
                color: #212529;
                font-size: 14px;
            }
            
            /* Dashboard Statistics Enhancement */
            .dashboard-view .row {
                margin: 20px 0;
            }
            
            .stats-card {
                background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
                color: #ffffff;
                border-radius: 15px;
                padding: 25px;
                text-align: center;
                margin: 15px;
                box-shadow: 0 8px 20px rgba(0,0,0,0.12);
                border: 1px solid rgba(255,255,255,0.2);
            }
            
            .stats-number {
                font-size: 3em;
                font-weight: 700;
                margin-bottom: 8px;
                text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
            }
            
            .stats-label {
                font-size: 1.2em;
                font-weight: 500;
                opacity: 0.95;
            }
            
            /* Progress Bar Professional Styling */
            .progress {
                height: 30px;
                border-radius: 15px;
                background-color: #e9ecef;
                border: 1px solid #ced4da;
                overflow: hidden;
            }
            
            .progress-bar {
                background: linear-gradient(45deg, #28a745, #20c997);
                color: #ffffff;
                font-weight: 600;
                line-height: 30px;
                text-align: center;
            }
            
            /* Table Enhancements */
            .table {
                background-color: #ffffff;
                border: 1px solid #dee2e6;
                border-radius: 8px;
                overflow: hidden;
            }
            
            .table th {
                background-color: #495057;
                color: #ffffff;
                font-weight: 600;
                padding: 15px;
                border: none;
            }
            
            .table td {
                padding: 12px 15px;
                color: #495057;
                border-color: #e9ecef;
            }
            
            /* Log Messages Styling */
            .log-message {
                background-color: #f8f9fa;
                border: 1px solid #e9ecef;
                border-radius: 6px;
                padding: 12px;
                margin: 8px 0;
                font-family: 'Courier New', monospace;
                font-size: 13px;
                color: #495057;
            }
            
            /* Error Messages */
            .error-message {
                background-color: #f8d7da;
                border: 1px solid #f5c6cb;
                color: #721c24;
                border-radius: 6px;
                padding: 15px;
                margin: 10px 0;
                font-weight: 500;
            }
            
            /* Success Messages */
            .success-message {
                background-color: #d4edda;
                border: 1px solid #c3e6cb;
                color: #155724;
                border-radius: 6px;
                padding: 15px;
                margin: 10px 0;
                font-weight: 500;
            }
            
            /* Responsive Design */
            @media (max-width: 768px) {
                .stats-card {
                    margin: 10px 5px;
                    padding: 20px;
                }
                
                .stats-number {
                    font-size: 2.5em;
                }
                
                .card-panel {
                    margin: 10px 0;
                }
            }
            """;
    }
    
    /**
     * Custom JavaScript for enhanced functionality
     */
    private static String getCustomJS() {
        return """
            // Enhanced Test Statistics Display
            document.addEventListener('DOMContentLoaded', function() {
                // Add animated counters for statistics
                const stats = document.querySelectorAll('.stats-number');
                stats.forEach(stat => {
                    const target = parseInt(stat.textContent);
                    let current = 0;
                    const increment = target / 100;
                    const timer = setInterval(() => {
                        current += increment;
                        if (current >= target) {
                            current = target;
                            clearInterval(timer);
                        }
                        stat.textContent = Math.floor(current);
                    }, 20);
                });
                
                // Add success rate calculation
                setTimeout(() => {
                    const passedTests = document.querySelector('[data-test-status="pass"]')?.textContent || '0';
                    const totalTests = document.querySelector('[data-total-tests]')?.textContent || '0';
                    const successRate = totalTests > 0 ? ((parseInt(passedTests) / parseInt(totalTests)) * 100).toFixed(1) : '0';
                    
                    // Create success rate display
                    const successRateElement = document.createElement('div');
                    successRateElement.className = 'stats-card';
                    successRateElement.innerHTML = `
                        <div class="stats-number">${successRate}%</div>
                        <div class="stats-label">🎯 Success Rate</div>
                    `;
                    
                    const statsContainer = document.querySelector('.dashboard-view .row');
                    if (statsContainer) {
                        statsContainer.appendChild(successRateElement);
                    }
                }, 1000);
            });
            """;
    }

    public static ExtentTest createTest(String testName) {
        ExtentTest extentTest = getInstance().createTest(testName);
        test.set(extentTest);
        return extentTest;
    }

    public static ExtentTest createTest(String testName, String description) {
        ExtentTest extentTest = getInstance().createTest(testName, description);
        test.set(extentTest);
        return extentTest;
    }

    public static ExtentTest getTest() {
        return test.get();
    }

    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }

    public static String getReportPath() {
        return reportPath;
    }

    public static void removeTest() {
        test.remove();
    }
    
    /**
     * Cleans up old report files, keeping only the latest N reports as configured
     */
    private static void cleanupOldReports() {
        try {
            System.out.println("[CLEANUP] Starting automatic report cleanup...");
            ReportCleanupManager.CleanupResult result = ReportCleanupManager.cleanupOldReportsFromConfig();
            System.out.println(result.getMessage());
        } catch (Exception e) {
            System.err.println("[WARNING] Failed to cleanup old reports: " + e.getMessage());
            // Don't fail the test execution if cleanup fails
        }
    }
}
