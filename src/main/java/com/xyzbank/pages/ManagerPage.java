package com.xyzbank.pages;

import com.xyzbank.utils.SeleniumHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ManagerPage extends SeleniumHelper {

    private static final Logger log = LoggerFactory.getLogger(ManagerPage.class);

    private static final By ADD_CUSTOMER_TAB = By.xpath("//button[contains(text(),'Add Customer')]");
    private static final By OPEN_ACCOUNT_TAB = By.xpath("//button[contains(text(),'Open Account')]");
    private static final By CUSTOMERS_TAB    = By.xpath("//button[contains(text(),'Customers')]");

    private static final By FIRST_NAME = By.xpath("//input[@placeholder='First Name']");
    private static final By LAST_NAME  = By.xpath("//input[@placeholder='Last Name']");
    private static final By POST_CODE  = By.xpath("//input[@placeholder='Post Code']");
    private static final By ADD_BTN    = By.xpath("//button[@type='submit']");

    private static final By CUSTOMER_SELECT = By.id("userSelect");
    private static final By CURRENCY_SELECT = By.id("currency");
    private static final By PROCESS_BTN     = By.xpath("//button[text()='Process']");

    private static final By SEARCH_INPUT  = By.xpath("//input[@placeholder='Search Customer']");
    private static final By CUSTOMER_ROWS = By.xpath("//table/tbody/tr");

    public ManagerPage(WebDriver driver) {
        super(driver);
    }

    public void goToAddCustomer() {
        log.debug("Navigating to Add Customer tab...");
        click(ADD_CUSTOMER_TAB);
    }

    public void goToOpenAccount() {
        log.debug("Navigating to Open Account tab...");
        click(OPEN_ACCOUNT_TAB);
    }

    public void goToCustomers() {
        log.debug("Navigating to Customers tab...");
        click(CUSTOMERS_TAB);
    }

    public String addCustomer(String firstName, String lastName, String postCode) {
        log.debug("Adding customer: {} {}", firstName, lastName);
        goToAddCustomer();
        type(FIRST_NAME, firstName);
        type(LAST_NAME, lastName);
        type(POST_CODE, postCode);
        click(ADD_BTN);
        String alert = acceptAlertAndGetText();
        log.debug("Add customer alert: {}", alert);
        return alert;
    }

    public String openAccount(String customerFullName, String currency) {
        log.debug("Opening {} account for: {}", currency, customerFullName);
        goToOpenAccount();
        selectByText(CUSTOMER_SELECT, customerFullName);
        selectByText(CURRENCY_SELECT, currency);
        click(PROCESS_BTN);
        String alert = acceptAlertAndGetText();
        log.debug("Open account alert: {}", alert);
        return alert;
    }

    public boolean isCustomerInList(String firstName) {
        log.debug("Checking if customer '{}' is in list...", firstName);
        goToCustomers();
        type(SEARCH_INPUT, firstName);
        List<WebElement> rows = driver.findElements(CUSTOMER_ROWS);
        boolean found = rows.stream().anyMatch(r -> r.getText().contains(firstName));
        log.debug("Customer '{}' found in list: {}", firstName, found);
        return found;
    }

    public boolean deleteCustomer(String firstName) {
        log.debug("Deleting customer: {}", firstName);
        goToCustomers();
        type(SEARCH_INPUT, firstName);
        List<WebElement> rows = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(CUSTOMER_ROWS));
        for (WebElement row : rows) {
            if (row.getText().contains(firstName)) {
                row.findElement(By.xpath(".//button[text()='Delete']")).click();
                log.debug("Delete button clicked for: {}", firstName);
                return true;
            }
        }
        log.warn("Customer '{}' not found for deletion", firstName);
        return false;
    }
}
