package com.xyzbank.pages.customer;

import com.xyzbank.pages.home.HomePage;
import com.xyzbank.utils.PageHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountPage {

    private static final Logger log = LoggerFactory.getLogger(AccountPage.class);
    private final WebDriver driver;
    private final PageHelper helper;

    @FindBy(xpath = "//button[contains(text(),'Transactions')]")
    private WebElement transactionsTab;

    @FindBy(xpath = "//button[contains(text(),'Deposit')]")
    private WebElement depositTab;

    @FindBy(xpath = "//button[contains(text(),'Withdrawl')]")
    private WebElement withdrawTab;

    @FindBy(xpath = "//input[@type='number' and @ng-model='amount']")
    private WebElement amountInput;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement submitBtn;

    @FindBy(xpath = "(//div[contains(@class,'center')]//strong[contains(@class,'ng-binding')])[2]")
    private WebElement balance;

    @FindBy(xpath = "//button[contains(text(),'Back')]")
    private WebElement backBtn;

    @FindBy(xpath = "//button[contains(text(),'Logout')]")
    private WebElement logoutBtn;

    public AccountPage(WebDriver driver) {
        this.driver = driver;
        this.helper = new PageHelper(driver);
        PageFactory.initElements(driver, this);
    }

    public int getBalance() {
        log.debug("Reading account balance...");
        int value = Integer.parseInt(helper.getText(balance).trim());
        log.debug("Current balance: {}", value);
        return value;
    }

    public void deposit(String amount) {
        log.debug("Depositing amount: {}", amount);
        helper.click(depositTab);
        helper.type(amountInput, amount);
        helper.safeClick(submitBtn);
        helper.waitForStatusMessage();
        log.debug("Deposit of {} completed", amount);
    }

    public void withdraw(String amount) {
        log.debug("Withdrawing amount: {}", amount);
        helper.click(withdrawTab);
        helper.type(amountInput, amount);
        helper.safeClick(submitBtn);
        helper.waitForStatusMessage();
        log.debug("Withdrawal of {} completed", amount);
    }

    public void clickTransactionsTab() {
        log.debug("Clicking Transactions tab...");
        helper.click(transactionsTab);
    }

    public int getTransactionCount() {
        int count = (int) driver.findElements(By.xpath("//table/tbody/tr"))
                .stream()
                .filter(r -> !r.getText().trim().isEmpty())
                .count();
        log.debug("Transaction row count: {}", count);
        return count;
    }

    public String getStatusMessage() {
        return helper.getStatusMessage();
    }

    public boolean isBackButtonVisible() {
        boolean visible = helper.isVisible(backBtn);
        log.debug("Back button visible: {}", visible);
        return visible;
    }

    public HomePage logout() {
        log.debug("Logging out...");
        helper.click(logoutBtn);
        return new HomePage(driver);
    }
}