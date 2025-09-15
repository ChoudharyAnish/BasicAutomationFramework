package org.example.pages;

import org.example.utils.WaitHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Page Object Model for Flipkart Search Page
 * Handles search functionality on Flipkart website
 */
public class FlipkartSearchPage {
    private WebDriver driver;
    private WaitHelper waitHelper;

    // Search Elements
    @FindBy(xpath = "//*[@class='Pke_EE']")
    private WebElement searchBox;

    // Constructor
    public FlipkartSearchPage(WebDriver driver) {
        this.driver = driver;
        this.waitHelper = new WaitHelper(driver);
        PageFactory.initElements(driver, this);
    }

    /**
     * Click on the search bar to focus it with basic retry logic
     */
    public void clickOnSearchBar() {
        try {
            // Wait for page to stabilize
            Thread.sleep(3000);
            
            // Try normal click first
            waitHelper.waitForElementToBeClickable(searchBox);
            searchBox.click();
            System.out.println("✅ Search bar clicked successfully");
            
        } catch (Exception e) {
            try {
                // Fallback: JavaScript click
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", searchBox);
                System.out.println("✅ Search bar clicked using JavaScript");
            } catch (Exception ex) {
                System.err.println("❌ Both click methods failed: " + ex.getMessage());
                throw new RuntimeException("Failed to click search bar", ex);
            }
        }
    }

    /**
     * Search for Nike Shoes specifically with stale element handling
     */
    public void searchForNikeShoes() {
        searchForProductRobust("Nike Shoes");
    }

    /**
     * Search for Camera specifically with stale element handling
     */
    public void searchForCameras() {
        searchForProductRobust("Camera");
    }
    
    /**
     * Robust search method that handles stale elements
     */
    private void searchForProductRobust(String productName) {
        try {
            // Wait and get fresh element reference
            Thread.sleep(1000);
            waitHelper.waitForElementToBeClickable(searchBox);
            
            // Clear any existing text
            searchBox.clear();
            
            // Type the product name
            searchBox.sendKeys(productName);
            searchBox.sendKeys(Keys.ENTER);
            
            System.out.println("✅ Successfully searched for: " + productName);
            
        } catch (Exception e) {
            // If stale element, try direct navigation as fallback
            try {
                String searchUrl = "https://www.flipkart.com/search?q=" + productName.replace(" ", "%20");
                driver.navigate().to(searchUrl);
                System.out.println("✅ Used direct navigation for: " + productName);
            } catch (Exception ex) {
                System.err.println("❌ Both search methods failed for: " + productName);
                throw new RuntimeException("Failed to search for: " + productName, ex);
            }
        }
    }

    /**
     * Generic search method for any product
     * @param productName - the product to search for
     */
    public void searchForProduct(String productName) {
        waitHelper.waitForElementToBeClickable(searchBox);
        searchBox.clear();
        searchBox.sendKeys(productName);
        searchBox.sendKeys(Keys.ENTER);
    }

    /**
     * Get the current page title
     * @return page title
     */
    public String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Get the current URL
     * @return current URL
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Check if the search page is loaded properly
     * @return true if search box is visible and clickable
     */
    public boolean isSearchPageLoaded() {
        try {
            waitHelper.waitForElementToBeVisible(searchBox);
            return searchBox.isDisplayed() && searchBox.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
}
