package com.xyzbank.pages.customer;

import com.xyzbank.utils.PageHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class CustomerLoginPage {

    private static final Logger log = LoggerFactory.getLogger(CustomerLoginPage.class);
    private final WebDriver driver;
    private final PageHelper helper;

    @FindBy(id = "userSelect")
    private WebElement customerSelect;

    @FindBy(xpath = "//button[text()='Login']")
    private WebElement loginBtn;

    @FindBy(id = "accountSelect")
    private WebElement accountSelect;

    @FindBy(xpath = "//button[contains(text(),'Deposit')]")
    private WebElement depositTab;

    public CustomerLoginPage(WebDriver driver) {
        this.driver = driver;
        this.helper = new PageHelper(driver);
        PageFactory.initElements(driver, this);
    }

    public AccountPage loginAs(String customerName) {
        log.debug("Logging in as customer: {}", customerName);
        helper.selectByText(customerSelect, customerName);
        helper.click(loginBtn);

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> d.getCurrentUrl().contains("#/account"));
        log.debug("Login successful - account page loaded");

        helper.isVisible(accountSelect);

        log.debug("Waiting for account options to populate...");
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> {
            Select s = new Select(d.findElement(By.id("accountSelect")));
            int size = s.getOptions().size();
            log.debug("Account dropdown options count: {}", size);
            return size > 1;
        });

        Select dropdown = new Select(driver.findElement(By.id("accountSelect")));
        dropdown.selectByIndex(1);
        log.debug("First account selected — total options: {}", dropdown.getOptions().size());

        helper.isClickable(depositTab);
        log.debug("Account dashboard fully loaded and ready");

        return new AccountPage(driver);
    }
}