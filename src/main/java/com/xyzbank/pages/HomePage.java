package com.xyzbank.pages;

import com.xyzbank.utils.SeleniumHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomePage extends SeleniumHelper {

    private static final Logger log = LoggerFactory.getLogger(HomePage.class);

    private static final By CUSTOMER_LOGIN_BTN = By.xpath("//button[contains(text(),'Customer Login')]");
    private static final By MANAGER_LOGIN_BTN  = By.xpath("//button[contains(text(),'Bank Manager Login')]");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        log.debug("Navigating to XYZ Bank login page...");
        driver.get("https://www.globalsqa.com/angularJs-protractor/BankingProject/#/login");
    }

    public CustomerLoginPage clickCustomerLogin() {
        log.debug("Clicking Customer Login button...");
        click(CUSTOMER_LOGIN_BTN);
        return new CustomerLoginPage(driver);
    }

    public ManagerPage clickManagerLogin() {
        log.debug("Clicking Bank Manager Login button...");
        click(MANAGER_LOGIN_BTN);
        return new ManagerPage(driver);
    }
}