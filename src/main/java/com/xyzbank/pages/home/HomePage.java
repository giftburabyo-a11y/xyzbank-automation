package com.xyzbank.pages.home;

import com.xyzbank.pages.customer.CustomerLoginPage;
import com.xyzbank.pages.manger.ManagerPage;
import com.xyzbank.utils.PageHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomePage {

    private static final Logger log = LoggerFactory.getLogger(HomePage.class);
    private final WebDriver driver;
    private final PageHelper helper;

    @FindBy(xpath = "//button[contains(text(),'Customer Login')]")
    private WebElement customerLoginBtn;

    @FindBy(xpath = "//button[contains(text(),'Bank Manager Login')]")
    private WebElement managerLoginBtn;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.helper = new PageHelper(driver);
        PageFactory.initElements(driver, this);
    }

    public void open() {
        log.debug("Navigating to XYZ Bank login page...");
        driver.get("https://www.globalsqa.com/angularJs-protractor/BankingProject/#/login");
    }

    public CustomerLoginPage clickCustomerLogin() {
        log.debug("Clicking Customer Login button...");
        helper.click(customerLoginBtn);
        return new CustomerLoginPage(driver);
    }

    public ManagerPage clickManagerLogin() {
        log.debug("Clicking Bank Manager Login button...");
        helper.click(managerLoginBtn);
        return new ManagerPage(driver);
    }
}