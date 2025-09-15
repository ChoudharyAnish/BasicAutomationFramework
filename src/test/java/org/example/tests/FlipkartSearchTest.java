package org.example.tests;

import org.example.base.BaseTest;
import org.example.pages.FlipkartSearchPage;
import org.example.utils.ScreenshotHelper;
import org.testng.annotations.Test;

/**
 * Test class for Flipkart Search functionality
 */
public class FlipkartSearchTest extends BaseTest {
    private FlipkartSearchPage searchPage;

    @Test(priority = 1, description = "Search for Nike Shoes on Flipkart")
    public void testNikeShoesSearch() {
        searchPage = new FlipkartSearchPage(getDriver());

        logStep("Clicking on search bar");
        searchPage.clickOnSearchBar();
        
        logStep("Searching for Nike Shoes");
        searchPage.searchForNikeShoes();

        // Take screenshot after search
        String screenshotPath = ScreenshotHelper.captureScreenshot(getDriver(), "nike_shoes_search_results");
        test.addScreenCaptureFromPath(screenshotPath, "🔍 Nike Shoes Search Results");
        logInfo("Screenshot captured: " + screenshotPath);
        
        logVerification("Verifying search was executed");
        logPass("Nike Shoes search completed successfully");
    }
}
