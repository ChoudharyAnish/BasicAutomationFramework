package org.example.tests;

import com.aventstack.extentreports.Status;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.base.APIBaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * API Tests for Products functionality
 * Testing APIs from: https://automationexercise.com/api_list
 */
public class ProductsAPITest extends APIBaseTest {
    
    /**
     * Test Case: API 1 - Get All Products List
     * 
     * API Details:
     * - URL: https://automationexercise.com/api/productsList
     * - Method: GET
     * - Expected Response Code: 200
     * - Expected Response: All products list in JSON format
     */
    @Test(description = "Verify Get All Products List API returns 200 and valid response")
    public void testGetAllProductsList() {
        
        // Step 1: Log what we're testing
        test.log(Status.INFO, "Testing API 1: Get All Products List");
        logRequest("GET", "/productsList");
        
        // Step 2: Make the API call using REST Assured
        response = given()
                .header("Content-Type", "application/json")  // Set content type
                .when()
                .get("/productsList")  // Make GET request to /productsList endpoint
                .then()
                .extract().response();  // Extract the response for further validation
        
        // Step 3: Log the response details
        logResponse(response);
        
        // Step 4: Validate response status code
        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 200, 
            "Expected status code 200 but got " + actualStatusCode);
        test.log(Status.PASS, "✓ Status code validation passed: " + actualStatusCode);
        
        // Step 5: Validate response time (should be reasonable)
        long responseTime = response.getTime();
        Assert.assertTrue(responseTime < 5000, 
            "Response time too slow: " + responseTime + "ms");
        test.log(Status.PASS, "✓ Response time validation passed: " + responseTime + "ms");
        
        // Step 6: Validate response body is not empty
        String responseBody = response.getBody().asString();
        Assert.assertFalse(responseBody.isEmpty(), "Response body should not be empty");
        test.log(Status.PASS, "✓ Response body validation passed: Not empty");
        
        // Step 7: Validate response contains expected content
        Assert.assertTrue(responseBody.contains("products"), 
            "Response should contain 'products' key");
        test.log(Status.PASS, "✓ Response content validation passed: Contains 'products'");
        
        // Step 8: Validate Content-Type header
        String contentType = response.getHeader("Content-Type");
        Assert.assertTrue(contentType.contains("application/json") || contentType.contains("text/html"), 
            "Content-Type should be JSON or HTML, but got: " + contentType);
        test.log(Status.PASS, "✓ Content-Type validation passed: " + contentType);
        
        test.log(Status.PASS, "🎉 All validations passed! API 1 test completed successfully");
    }
}
