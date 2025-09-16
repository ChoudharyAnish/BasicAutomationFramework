package org.example.tests;

import com.aventstack.extentreports.Status;
import io.restassured.response.Response;
import org.example.base.APIBaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * API Tests for Brands functionality
 * Testing Brands APIs from: https://automationexercise.com/api_list
 */
public class BrandsAPITest extends APIBaseTest {
    
    /**
     * Test Case: API 3 - Get All Brands List
     * 
     * API Details:
     * - URL: https://automationexercise.com/api/brandsList
     * - Method: GET
     * - Expected Response Code: 200
     * - Expected Response: All brands list in JSON format
     */
    @Test(description = "Verify Get All Brands List API returns 200 and valid response")
    public void testGetAllBrandsList() {
        
        // Step 1: Log what we're testing
        test.log(Status.INFO, "🚀 Testing API 3: Get All Brands List");
        logRequest("GET", "/brandsList");
        
        // Step 2: Make the API call using REST Assured
        response = given()
                .header("Content-Type", "application/json")
                .when()
                .get("/brandsList")  // This calls: https://automationexercise.com/api/brandsList
                .then()
                .extract().response();
        
        // Step 3: Log the response details
        logResponse(response);
        
        // CONSOLE OUTPUT: Print response to terminal for learning
        System.out.println("=== API RESPONSE DETAILS ===");
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Response Time: " + response.getTime() + " ms");
        System.out.println("Response Body: ");
        System.out.println(response.getBody().asString());
        System.out.println("==========================");
        
        // Step 4: Validate response status code
        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 200, 
            "Expected status code 200 but got " + actualStatusCode);
        test.log(Status.PASS, "✅ Status code validation passed: " + actualStatusCode);
        
        // Step 5: Validate response time
        long responseTime = response.getTime();
        Assert.assertTrue(responseTime < 5000, 
            "Response time too slow: " + responseTime + "ms");
        test.log(Status.PASS, "✅ Response time validation passed: " + responseTime + "ms");
        
        // Step 6: Validate response body is not empty
        String responseBody = response.getBody().asString();
        Assert.assertFalse(responseBody.isEmpty(), "Response body should not be empty");
        test.log(Status.PASS, "✅ Response body validation passed: Not empty");
        
        // Step 7: Validate response contains expected content
        Assert.assertTrue(responseBody.contains("brands"), 
            "Response should contain 'brands' key");
        test.log(Status.PASS, "✅ Response content validation passed: Contains 'brands'");
        
        test.log(Status.PASS, "🎉 All validations passed! Brands API test completed successfully");
    }
}
