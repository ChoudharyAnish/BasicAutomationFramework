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
     * Click on the search bar to focus it
     */
    public void clickOnSearchBar() {
        waitHelper.waitForElementToBeClickable(searchBox);
        searchBox.click();
    }

    /**
     * Search for Nike Shoes specifically
     */
    public void searchForNikeShoes() {
        waitHelper.waitForElementToBeClickable(searchBox);
        searchBox.sendKeys("Nike Shoes");
        searchBox.sendKeys(Keys.ENTER);
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
