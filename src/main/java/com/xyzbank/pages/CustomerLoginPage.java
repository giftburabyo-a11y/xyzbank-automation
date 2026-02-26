package com.xyzbank.pages;

import com.xyzbank.utils.SeleniumHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class CustomerLoginPage extends SeleniumHelper {

    private static final Logger log = LoggerFactory.getLogger(CustomerLoginPage.class);

    private static final By CUSTOMER_SELECT = By.id("userSelect");
    private static final By LOGIN_BTN       = By.xpath("//button[text()='Login']");
    private static final By ACCOUNT_SELECT  = By.id("accountSelect");
    private static final By DEPOSIT_TAB     = By.xpath("//button[contains(text(),'Deposit')]");

    public CustomerLoginPage(WebDriver driver) {
        super(driver);
    }

    public AccountPage loginAs(String customerName) {
        log.debug("Logging in as customer: {}", customerName);
        selectByText(CUSTOMER_SELECT, customerName);
        click(LOGIN_BTN);

        wait.until(d -> d.getCurrentUrl().contains("#/account"));
        log.debug("Login successful - account page loaded");

        log.debug("Waiting for account selector to appear...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(ACCOUNT_SELECT));

        // Use a longer 30-second wait specifically for options to populate
        log.debug("Waiting for account options to populate...");
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> {
            Select s = new Select(d.findElement(ACCOUNT_SELECT));
            int size = s.getOptions().size();
            log.debug("Account dropdown options count: {}", size);
            return size > 1;
        });

        Select dropdown = new Select(driver.findElement(ACCOUNT_SELECT));
        dropdown.selectByIndex(1);
        log.debug("First account selected — total options: {}", dropdown.getOptions().size());

        wait.until(ExpectedConditions.elementToBeClickable(DEPOSIT_TAB));
        log.debug("Account dashboard fully loaded and ready");

        return new AccountPage(driver);
    }
}