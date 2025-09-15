package org.example.pages;

import org.example.utils.WaitHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Page Object Model for Login Page
 */
public class HomePage {
    private WebDriver driver;
    private WaitHelper waitHelper;

    // Page Elements using @FindBy annotation
    @FindBy(xpath = "//*[@id='name']")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(xpath = "//*[@id='email']")
    private WebElement loginButton;

    @FindBy(xpath = "//div[@class='error-message']")
    private WebElement errorMessage;

    @FindBy(linkText = "Forgot Password?")
    private WebElement forgotPasswordLink;
    
    @FindBy(xpath = "//*[@class='Pke_EE']")
    private WebElement searchButton;


    // Alternative locators using By class
    private By usernameLocator = By.xpath("//*[@id='name']");
    private By passwordLocator = By.id("password");
    private By loginButtonLocator = By.xpath("//*[@id='email']");
    private By errorMessageLocator = By.xpath("//div[@class='error-message']");

    // Constructor
    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.waitHelper = new WaitHelper(driver);
        PageFactory.initElements(driver, this);
    }

    public void clickOnSearchBar(){
        waitHelper.waitForElementToBeClickable(searchButton);
        searchButton.click();
    }

    
    public void searchForNikeShoes(){
        waitHelper.waitForElementToBeClickable(searchButton);
        searchButton.sendKeys("Nike Shoes");
        searchButton.sendKeys(Keys.ENTER);
    }


    // Page Actions/Methods
    public void enterUsername(String username) {
        waitHelper.waitForElementToBeVisible(usernameLocator);
        usernameField.click();
        usernameField.clear();
        usernameField.sendKeys(username);
    }

    public void enterPassword(String password) {
        waitHelper.waitForElementToBeVisible(passwordLocator);
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    public void clickLoginButton() {
        waitHelper.waitForElementToBeClickable(loginButtonLocator);
        loginButton.click();
    }

    public void login(String username) {
        enterUsername(username);
        clickLoginButton();
    }

    public boolean isErrorMessageDisplayed() {
        try {
            return waitHelper.waitForElementToBeVisible(errorMessageLocator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorMessage() {
        if (isErrorMessageDisplayed()) {
            return errorMessage.getText();
        }
        return "";
    }

    public void clickForgotPasswordLink() {
        waitHelper.waitForElementToBeClickable(forgotPasswordLink);
        forgotPasswordLink.click();
    }

    public boolean isLoginPageLoaded() {
        try {
            waitHelper.waitForElementToBeVisible(usernameLocator);
            waitHelper.waitForElementToBeVisible(passwordLocator);
            waitHelper.waitForElementToBeVisible(loginButtonLocator);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    // Validation methods
    public boolean isUsernameFieldEnabled() {
        return usernameField.isEnabled();
    }

    public boolean isPasswordFieldEnabled() {
        return passwordField.isEnabled();
    }

    public boolean isLoginButtonEnabled() {
        return loginButton.isEnabled();
    }
}
