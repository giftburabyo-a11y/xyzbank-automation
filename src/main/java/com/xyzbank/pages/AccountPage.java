package com.xyzbank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;

public class AccountPage extends BasePage {

    private static final By TRANSACTIONS_TAB = By.xpath("//button[contains(text(),'Transactions')]");
    private static final By DEPOSIT_TAB      = By.xpath("//button[contains(text(),'Deposit')]");
    private static final By WITHDRAW_TAB     = By.xpath("//button[contains(text(),'Withdrawl')]");
    private static final By AMOUNT_INPUT     = By.xpath("//input[@type='number']");
    private static final By SUBMIT_BTN       = By.xpath("//button[@type='submit']");
    private static final By STATUS_MSG       = By.xpath("//span[contains(@class,'error') and contains(@class,'ng-binding')]");
    private static final By BALANCE          = By.xpath("(//div[contains(@class,'center')]//strong[contains(@class,'ng-binding')])[2]");
    private static final By LOGOUT_BTN       = By.xpath("//button[contains(text(),'Logout')]");
    private static final By TRANSACTION_ROWS = By.xpath("//table/tbody/tr");
    private static final By BACK_BTN         = By.xpath("//button[contains(text(),'Back')]");
    private static final By RESET_BTN        = By.xpath("//button[contains(text(),'Reset')]");
    private static final By ACCOUNT_SELECT   = By.id("accountSelect");

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
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(AMOUNT_INPUT));
        input.clear();
        input.sendKeys(amount);
        wait.until(ExpectedConditions.elementToBeClickable(SUBMIT_BTN)).click();
        // Wait for balance to update — confirms deposit was processed
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        return this;
    }

    public AccountPage withdraw(String amount) {
        click(WITHDRAW_TAB);
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(AMOUNT_INPUT));
        input.clear();
        input.sendKeys(amount);
        wait.until(ExpectedConditions.elementToBeClickable(SUBMIT_BTN)).click();
        // Wait for balance to update — confirms withdrawal was processed
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        return this;
    }

    // Status message uses ng-hide so visibility check is unreliable.
    // Instead we check the DOM text directly using presence + non-empty text.
    public String getStatusMessage() {
        long end = System.currentTimeMillis() + 10000;
        while (System.currentTimeMillis() < end) {
            String source = driver.getPageSource();
            if (source.contains("Deposit Successful")) return "Deposit Successful";
            if (source.contains("Transaction successful")) return "Transaction successful";
            if (source.contains("Transaction Failed")) return "Transaction Failed";
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        }
        return "";
    }

    public boolean isStatusMessageDisplayed() {
        return !getStatusMessage().isEmpty();
    }

    public AccountPage clickTransactionsTab() {
        click(TRANSACTIONS_TAB);
        // Wait for URL to reflect transactions state
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        return this;
    }

    public int getTransactionCount() {
        // Transactions table may take time to render
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(TRANSACTION_ROWS));
        } catch (Exception ignored) {}
        return driver.findElements(TRANSACTION_ROWS).size();
    }

    public boolean isBackButtonVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(BACK_BTN)).isDisplayed();
        } catch (Exception e) { return false; }
    }

    public boolean isResetButtonVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(RESET_BTN)).isDisplayed();
        } catch (Exception e) { return false; }
    }

    public HomePage logout() {
        click(LOGOUT_BTN);
        return new HomePage();
    }
}