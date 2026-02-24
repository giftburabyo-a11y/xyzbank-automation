package com.xyzbank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CustomerLoginPage extends BasePage {

    private static final By CUSTOMER_SELECT = By.id("userSelect");
    private static final By LOGIN_BTN       = By.xpath("//button[text()='Login']");

    // Wait for the account page URL to confirm navigation succeeded
    public AccountPage loginAs(String customerName) {
        selectByText(CUSTOMER_SELECT, customerName);
        click(LOGIN_BTN);
        // Wait for URL to change to #/account instead of waiting for a specific button
        wait.until(d -> d.getCurrentUrl().contains("#/account"));
        return new AccountPage();
    }
}
