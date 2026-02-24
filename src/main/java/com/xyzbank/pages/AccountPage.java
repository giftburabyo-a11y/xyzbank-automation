package com.xyzbank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;

public class AccountPage extends BasePage {

    // Tabs  use contains() to handle AngularJS whitespace
    private static final By TRANSACTIONS_TAB = By.xpath("//button[contains(text(),'Transactions')]");
    private static final By DEPOSIT_TAB      = By.xpath("//button[contains(text(),'Deposit')]");
    private static final By WITHDRAW_TAB     = By.xpath("//button[contains(text(),'Withdrawl')]");

    // Form elements
    private static final By AMOUNT_INPUT = By.xpath("//input[@type='number']");
    private static final By SUBMIT_BTN   = By.xpath("//button[@type='submit']");

    // Status message  the app uses <span class="error ng-binding">
    private static final By STATUS_MSG = By.xpath("//span[contains(@class,'error') and contains(@class,'ng-binding')]");

    // Balance  2nd strong.ng-binding inside .center div
    // HTML: "Balance : <strong class="ng-binding">1000</strong>"
    private static final By BALANCE = By.xpath("(//div[contains(@class,'center')]//strong[contains(@class,'ng-binding')])[2]");

    // Logout
    private static final By LOGOUT_BTN = By.xpath("//button[contains(text(),'Logout')]");

    // Transactions page
    private static final By TRANSACTION_ROWS = By.xpath("//table/tbody/tr");
    private static final By BACK_BTN         = By.xpath("//button[contains(text(),'Back')]");
    private static final By RESET_BTN        = By.xpath("//button[contains(text(),'Reset')]");

    // Account dropdown
    private static final By ACCOUNT_SELECT = By.id("accountSelect");

    public int getBalance() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(BALANCE));
        return Integer.parseInt(driver.findElement(BALANCE).getText().trim());
    }

    public AccountPage selectAccount(String accountNumber) {
        selectByText(ACCOUNT_SELECT, accountNumber);
        return this;
    }

    public AccountPage deposit(String amount) {
        click(DEPOSIT_TAB);
        wait.until(ExpectedConditions.visibilityOfElementLocated(AMOUNT_INPUT));
        type(AMOUNT_INPUT, amount);
        click(SUBMIT_BTN);
        return this;
    }

    public AccountPage withdraw(String amount) {
        click(WITHDRAW_TAB);
        wait.until(ExpectedConditions.visibilityOfElementLocated(AMOUNT_INPUT));
        type(AMOUNT_INPUT, amount);
        click(SUBMIT_BTN);
        return this;
    }

    public String getStatusMessage() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(STATUS_MSG));
        return driver.findElement(STATUS_MSG).getText().trim();
    }

    public boolean isStatusMessageDisplayed() {
        return isDisplayed(STATUS_MSG);
    }

    public AccountPage clickTransactionsTab() {
        click(TRANSACTIONS_TAB);
        return this;
    }

    public int getTransactionCount() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(TRANSACTION_ROWS));
        return driver.findElements(TRANSACTION_ROWS).size();
    }

    public boolean isBackButtonVisible()  { return isDisplayed(BACK_BTN);  }
    public boolean isResetButtonVisible() { return isDisplayed(RESET_BTN); }

    public HomePage logout() {
        click(LOGOUT_BTN);
        return new HomePage();
    }
}
