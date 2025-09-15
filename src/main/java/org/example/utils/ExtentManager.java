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

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        
        // Enhanced Theme and Styling
        sparkReporter.config().setTheme(Theme.DARK);
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
            /* Enhanced Header Styling */
            .navbar-brand {
                font-weight: bold;
                color: #fff !important;
            }
            
            /* Test Status Cards Enhancement */
            .card-panel {
                border-radius: 10px;
                box-shadow: 0 4px 8px rgba(0,0,0,0.1);
                transition: transform 0.2s;
            }
            
            .card-panel:hover {
                transform: translateY(-2px);
                box-shadow: 0 8px 16px rgba(0,0,0,0.2);
            }
            
            /* Pass Status Styling */
            .test-pass {
                background: linear-gradient(135deg, #28a745, #20c997) !important;
                color: white !important;
                border-left: 5px solid #155724 !important;
            }
            
            /* Fail Status Styling */
            .test-fail {
                background: linear-gradient(135deg, #dc3545, #fd7e14) !important;
                color: white !important;
                border-left: 5px solid #721c24 !important;
            }
            
            /* Skip Status Styling */
            .test-skip {
                background: linear-gradient(135deg, #ffc107, #fd7e14) !important;
                color: white !important;
                border-left: 5px solid #856404 !important;
            }
            
            /* Progress Bar Enhancement */
            .progress {
                height: 25px;
                border-radius: 15px;
                background: #f8f9fa;
                overflow: hidden;
            }
            
            .progress-bar {
                background: linear-gradient(45deg, #28a745, #20c997);
                transition: width 0.5s ease;
            }
            
            /* Test Details Enhancement */
            .test-detail {
                border: 1px solid #dee2e6;
                border-radius: 8px;
                margin: 10px 0;
                padding: 15px;
                background: #f8f9fa;
            }
            
            /* Dashboard Stats Cards */
            .stats-card {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                border-radius: 15px;
                padding: 20px;
                text-align: center;
                margin: 10px;
                box-shadow: 0 6px 12px rgba(0,0,0,0.15);
            }
            
            .stats-number {
                font-size: 2.5em;
                font-weight: bold;
                margin-bottom: 5px;
            }
            
            .stats-label {
                font-size: 1.1em;
                opacity: 0.9;
            }
            
            /* Custom Icons */
            .status-icon {
                font-size: 1.2em;
                margin-right: 8px;
            }
            
            /* Responsive Design */
            @media (max-width: 768px) {
                .stats-card {
                    margin: 5px 0;
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
}
