package com.xyzbank.pages;

import org.openqa.selenium.By;

public class CustomerLoginPage extends BasePage {

    // id="userSelect" confirmed from real automation repos of this app
    private static final By CUSTOMER_SELECT = By.id("userSelect");
    private static final By LOGIN_BTN       = By.xpath("//button[text()='Login']");

    public AccountPage loginAs(String customerName) {
        selectByText(CUSTOMER_SELECT, customerName);
        click(LOGIN_BTN);
        return new AccountPage();
    }
}