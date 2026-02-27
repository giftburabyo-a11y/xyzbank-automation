package com.xyzbank.pages.manger;

import com.xyzbank.utils.PageHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ManagerPage {

    private static final Logger log = LoggerFactory.getLogger(ManagerPage.class);
    private final WebDriver driver;
    private final PageHelper helper;

    @FindBy(xpath = "//button[contains(text(),'Add Customer')]")
    private WebElement addCustomerTab;

    @FindBy(xpath = "//button[contains(text(),'Open Account')]")
    private WebElement openAccountTab;

    @FindBy(xpath = "//button[contains(text(),'Customers')]")
    private WebElement customersTab;

    @FindBy(xpath = "//input[@placeholder='First Name']")
    private WebElement firstNameInput;

    @FindBy(xpath = "//input[@placeholder='Last Name']")
    private WebElement lastNameInput;

    @FindBy(xpath = "//input[@placeholder='Post Code']")
    private WebElement postCodeInput;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement addBtn;

    @FindBy(id = "userSelect")
    private WebElement customerSelect;

    @FindBy(id = "currency")
    private WebElement currencySelect;

    @FindBy(xpath = "//button[text()='Process']")
    private WebElement processBtn;

    @FindBy(xpath = "//input[@placeholder='Search Customer']")
    private WebElement searchInput;

    public ManagerPage(WebDriver driver) {
        this.driver = driver;
        this.helper = new PageHelper(driver);
        PageFactory.initElements(driver, this);
    }

    public void goToAddCustomer() {
        log.debug("Navigating to Add Customer tab...");
        helper.click(addCustomerTab);
    }

    public void goToOpenAccount() {
        log.debug("Navigating to Open Account tab...");
        helper.click(openAccountTab);
    }

    public void goToCustomers() {
        log.debug("Navigating to Customers tab...");
        helper.click(customersTab);
    }

    public String addCustomer(String firstName, String lastName, String postCode) {
        log.debug("Adding customer: {} {}", firstName, lastName);
        goToAddCustomer();
        helper.type(firstNameInput, firstName);
        helper.type(lastNameInput, lastName);
        helper.type(postCodeInput, postCode);
        helper.click(addBtn);
        String alert = helper.acceptAlertAndGetText();
        log.debug("Add customer alert: {}", alert);
        return alert;
    }

    public String openAccount(String customerFullName, String currency) {
        log.debug("Opening {} account for: {}", currency, customerFullName);
        goToOpenAccount();
        helper.selectByText(customerSelect, customerFullName);
        helper.selectByText(currencySelect, currency);
        helper.click(processBtn);
        String alert = helper.acceptAlertAndGetText();
        log.debug("Open account alert: {}", alert);
        return alert;
    }

    public boolean isCustomerInList(String firstName) {
        log.debug("Checking if customer '{}' is in list...", firstName);
        goToCustomers();
        helper.type(searchInput, firstName);
        List<WebElement> rows = driver.findElements(By.xpath("//table/tbody/tr"));
        boolean found = rows.stream().anyMatch(r -> r.getText().contains(firstName));
        log.debug("Customer '{}' found in list: {}", firstName, found);
        return found;
    }

    public boolean deleteCustomer(String firstName) {
        log.debug("Deleting customer: {}", firstName);
        goToCustomers();
        helper.type(searchInput, firstName);
        List<WebElement> rows = driver.findElements(By.xpath("//table/tbody/tr"));
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