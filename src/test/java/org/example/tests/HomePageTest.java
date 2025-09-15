package org.example.tests;

import org.example.base.BaseTest;
import org.example.pages.HomePage;
import org.example.utils.ExcelReader;
import org.example.utils.ScreenshotHelper;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Test class for Login functionality
 */
public class HomePageTest extends BaseTest {
    private HomePage loginPage;

//    @Test(priority = 1, description = "Verify login page is loaded successfully")
//    public void verifyLoginPageLoad() {
//        loginPage = new LoginPage(getDriver());
//
//        logInfo("Verifying login page elements are loaded");
//
//
//        Assert.assertTrue(loginPage.isLoginPageLoaded(), "Login page is not loaded properly");
//        Assert.assertTrue(loginPage.isUsernameFieldEnabled(), "Username field is not enabled");
//        Assert.assertTrue(loginPage.isPasswordFieldEnabled(), "Password field is not enabled");
//        Assert.assertTrue(loginPage.isLoginButtonEnabled(), "Login button is not enabled");
//
//        logPass("Login page loaded successfully with all elements");
//    }

    @Test(priority = 2, description = "Verify login with valid credentials")
    public void testValidLogin() {
        loginPage = new HomePage(getDriver());

        loginPage.clickOnSearchBar();
        loginPage.searchForNikeShoes();

        // Take screenshot after searching for Nike Shoes
        String screenshotPath = ScreenshotHelper.captureScreenshot(getDriver(), "after_search_nike_shoes");
        test.addScreenCaptureFromPath(screenshotPath, "🔍 Nike Shoes Search Results");
        logInfo("Screenshot captured: " + screenshotPath);
        
        
        logInfo("Attempting login with valid credentials");
        // loginPage.login("validuser@example.com");
        
        // Add assertions based on your application behavior after successful login
        // For example: Assert.assertTrue(dashboardPage.isDashboardLoaded());
        logPass("Login attempt completed");
    }

//    @Test(priority = 3, description = "Verify login with invalid credentials")
//    public void testInvalidLogin() {
//        loginPage = new LoginPage(getDriver());
//
//        logInfo("Attempting login with invalid credentials");
//        loginPage.login("invalid@example.com", "wrongpassword");
//
//        // Verify error message is displayed
//        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message is not displayed for invalid login");
//
//        String errorMessage = loginPage.getErrorMessage();
//        Assert.assertFalse(errorMessage.isEmpty(), "Error message is empty");
//
//        logPass("Invalid login handled correctly with error message: " + errorMessage);
//    }
//
//    @Test(priority = 4, description = "Verify login with empty credentials")
//    public void testEmptyCredentials() {
//        loginPage = new LoginPage(getDriver());
//
//        logInfo("Attempting login with empty credentials");
//        loginPage.login("", "");
//
//        // Add assertions based on your application behavior
//        // This might show validation messages or disable the login button
//        logPass("Empty credentials test completed");
//    }
//
//    @Test(priority = 5, description = "Verify forgot password functionality")
//    public void testForgotPassword() {
//        loginPage = new LoginPage(getDriver());
//
//        logInfo("Testing forgot password link");
//        loginPage.clickForgotPasswordLink();
//
//        // Add assertions to verify navigation to forgot password page
//        // For example: Assert.assertTrue(driver.getCurrentUrl().contains("forgot-password"));
//        logPass("Forgot password link clicked successfully");
//    }
//
//    // Data-driven test using Excel (commented out as Excel file needs to be created first)
//    /*
//    @Test(priority = 6, dataProvider = "loginData", description = "Data-driven login test")
//    public void testLoginWithExcelData(Map<String, String> testData) {
//        loginPage = new LoginPage(getDriver());
//
//        String username = testData.get("username");
//        String password = testData.get("password");
//        String expectedResult = testData.get("expectedResult");
//
//        logInfo("Testing login with data: " + username);
//        loginPage.login(username, password);
//
//        if ("success".equalsIgnoreCase(expectedResult)) {
//            // Add success validation
//            logPass("Login successful for user: " + username);
//        } else {
//            Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
//                "Error message should be displayed for invalid credentials");
//            logPass("Login failed as expected for user: " + username);
//        }
//    }
//
//    @DataProvider(name = "loginData")
//    public Object[][] getLoginData() {
//        ExcelReader excelReader = new ExcelReader("src/main/resources/testdata.xlsx");
//        excelReader.setSheet("LoginData");
//        return excelReader.getDataForDataProvider();
//    }
//    */
//
//    // Simple data provider for demonstration
//    @Test(priority = 6, dataProvider = "simpleLoginData", description = "Simple data-driven login test")
//    public void testLoginWithSimpleData(String username, String password, String expectedResult) {
//        loginPage = new LoginPage(getDriver());
//
//        logInfo("Testing login with username: " + username);
//        loginPage.login(username, password);
//
//        if ("success".equalsIgnoreCase(expectedResult)) {
//            logPass("Login test completed for valid user: " + username);
//        } else {
//            logPass("Login test completed for invalid user: " + username);
//        }
//    }
//
//    @DataProvider(name = "simpleLoginData")
//    public Object[][] getSimpleLoginData() {
//        return new Object[][]{
//            {"user1@example.com", "password123", "success"},
//            {"user2@example.com", "wrongpassword", "failure"},
//            {"invalid@example.com", "password123", "failure"},
//            {"", "", "failure"}
//        };
//    }
}
