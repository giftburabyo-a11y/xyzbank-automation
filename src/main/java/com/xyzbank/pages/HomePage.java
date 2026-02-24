package com.xyzbank.pages;

import org.openqa.selenium.By;

public class HomePage extends BasePage {

    // Confirmed real locators — buttons use visible text in the app
    private static final By CUSTOMER_LOGIN_BTN = By.xpath("//button[contains(text(),'Customer Login')]");
    private static final By MANAGER_LOGIN_BTN  = By.xpath("//button[contains(text(),'Bank Manager Login')]");

    public void open() {
        driver.get("https://www.globalsqa.com/angularJs-protractor/BankingProject/#/login");
    }

    public CustomerLoginPage clickCustomerLogin() {
        click(CUSTOMER_LOGIN_BTN);
        return new CustomerLoginPage();
    }

    public ManagerPage clickManagerLogin() {
        click(MANAGER_LOGIN_BTN);
        return new ManagerPage();
    }
}