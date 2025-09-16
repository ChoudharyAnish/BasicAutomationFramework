package org.example.base;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.utils.ConfigReader;
import org.example.utils.ExtentManager;
import org.testng.ITestResult;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;

import java.lang.reflect.Method;

/**
 * Base class for API tests
 * This class sets up REST Assured configuration and integrates with ExtentReports
 */
public class APIBaseTest {
    
    protected ExtentTest test;
    protected Response response;
    
    @BeforeClass
    public void setupAPI() {
        // Set the base URI for all API requests
        RestAssured.baseURI = ConfigReader.getProperty("api.base.url");
        
        // Set default timeout
        RestAssured.config = RestAssured.config()
            .httpClient(io.restassured.config.HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", 
                    Integer.parseInt(ConfigReader.getProperty("api.timeout")) * 1000)
                .setParam("http.socket.timeout", 
                    Integer.parseInt(ConfigReader.getProperty("api.timeout")) * 1000));
        
        System.out.println("API Base URI set to: " + RestAssured.baseURI);
    }
    
    @BeforeMethod
    public void setupTest(Method method) {
        // Create ExtentTest for each test method
        test = ExtentManager.createTest(method.getName());
        test.log(Status.INFO, "Starting API test: " + method.getName());
        test.log(Status.INFO, "Base URI: " + RestAssured.baseURI);
    }
    
    @AfterMethod
    public void tearDownTest(ITestResult result) {
        if (test != null) {
            test.log(Status.INFO, "API test completed");
        }
        
        // Update the shared counters from BaseTest for accurate reporting
        synchronized (APIBaseTest.class) {
            BaseTest.incrementTotalTests();
            if (result.getStatus() == ITestResult.SUCCESS) {
                BaseTest.incrementPassedTests();
                if (test != null) test.log(Status.PASS, "✅ API Test Passed");
            } else if (result.getStatus() == ITestResult.FAILURE) {
                BaseTest.incrementFailedTests();
                if (test != null) test.log(Status.FAIL, "❌ API Test Failed");
            }
        }
    }
    
    /**
     * Helper method to log API request details
     */
    protected void logRequest(String method, String endpoint) {
        test.log(Status.INFO, "API Request: " + method + " " + endpoint);
    }
    
    /**
     * Helper method to log API response details
     */
    protected void logResponse(Response response) {
        test.log(Status.INFO, "Response Status Code: " + response.getStatusCode());
        test.log(Status.INFO, "Response Time: " + response.getTime() + " ms");
        
        // Log response body only if it's not too large
        String responseBody = response.getBody().asString();
        if (responseBody.length() < 500) {
            test.log(Status.INFO, "Response Body: " + responseBody);
        } else {
            test.log(Status.INFO, "Response Body: [Large response - " + responseBody.length() + " characters]");
        }
    }
}
