package com.xyzbank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;

public class ManagerPage extends BasePage {

    // ── Tabs ───────────────────────────────────────────────────────────
    private static final By ADD_CUSTOMER_TAB = By.xpath("//button[contains(text(),'Add Customer')]");
    private static final By OPEN_ACCOUNT_TAB = By.xpath("//button[contains(text(),'Open Account')]");
    private static final By CUSTOMERS_TAB    = By.xpath("//button[contains(text(),'Customers')]");

    // ── Add Customer form ──────────────────────────────────────────────
    // Confirmed locators — placeholder text matches exactly in the real app
    private static final By FIRST_NAME = By.xpath("//input[@placeholder='First Name']");
    private static final By LAST_NAME  = By.xpath("//input[@placeholder='Last Name']");
    private static final By POST_CODE  = By.xpath("//input[@placeholder='Post Code']");
    private static final By ADD_BTN    = By.xpath("//button[@type='submit']");

    // ── Open Account form ──────────────────────────────────────────────
    // id="userSelect" and id="currency" confirmed from real repos
    private static final By CUSTOMER_SELECT = By.id("userSelect");
    private static final By CURRENCY_SELECT = By.id("currency");
    private static final By PROCESS_BTN     = By.xpath("//button[text()='Process']");

    // ── Customer list ──────────────────────────────────────────────────
    private static final By SEARCH_INPUT  = By.xpath("//input[@placeholder='Search Customer']");
    private static final By CUSTOMER_ROWS = By.xpath("//table/tbody/tr");

    // ── Tab navigation ─────────────────────────────────────────────────
    public ManagerPage goToAddCustomer() {
        click(ADD_CUSTOMER_TAB);
        return this;
    }

    public ManagerPage goToOpenAccount() {
        click(OPEN_ACCOUNT_TAB);
        return this;
    }

    public ManagerPage goToCustomers() {
        click(CUSTOMERS_TAB);
        return this;
    }

    // ── Add Customer ───────────────────────────────────────────────────
    // After clicking submit the app shows a browser alert saying
    // "Customer added successfully with id:X" — we capture and return it
    public String addCustomer(String firstName, String lastName, String postCode) {
        goToAddCustomer();
        type(FIRST_NAME, firstName);
        type(LAST_NAME, lastName);
        type(POST_CODE, postCode);
        click(ADD_BTN);
        return acceptAlertAndGetText();
    }

    // ── Open Account ───────────────────────────────────────────────────
    // After clicking Process the app shows a browser alert saying
    // "Account created successfully with account Number:XXXX"
    public String openAccount(String customerFullName, String currency) {
        goToOpenAccount();
        selectByText(CUSTOMER_SELECT, customerFullName);
        selectByText(CURRENCY_SELECT, currency);
        click(PROCESS_BTN);
        return acceptAlertAndGetText();
    }

    // ── Customer list helpers ──────────────────────────────────────────
    public boolean isCustomerInList(String firstName) {
        goToCustomers();
        type(SEARCH_INPUT, firstName);
        List<WebElement> rows = driver.findElements(CUSTOMER_ROWS);
        return rows.stream().anyMatch(r -> r.getText().contains(firstName));
    }

    public boolean deleteCustomer(String firstName) {
        goToCustomers();
        type(SEARCH_INPUT, firstName);
        List<WebElement> rows = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(CUSTOMER_ROWS));
        for (WebElement row : rows) {
            if (row.getText().contains(firstName)) {
                row.findElement(By.xpath(".//button[text()='Delete']")).click();
                return true;
            }
        }
        return false;
    }
}