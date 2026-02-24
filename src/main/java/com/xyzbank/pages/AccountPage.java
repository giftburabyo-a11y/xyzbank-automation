package com.xyzbank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.util.List;

public class AccountPage extends BasePage {

    private static final Logger log = LoggerFactory.getLogger(AccountPage.class);

    private static final By TRANSACTIONS_TAB = By.xpath("//button[contains(text(),'Transactions')]");
    private static final By DEPOSIT_TAB      = By.xpath("//button[contains(text(),'Deposit')]");
    private static final By WITHDRAW_TAB     = By.xpath("//button[contains(text(),'Withdrawl')]");
    private static final By AMOUNT_INPUT     = By.xpath("//input[@type='number']");
    private static final By SUBMIT_BTN       = By.xpath("//button[@type='submit']");
    private static final By BALANCE          = By.xpath("(//div[contains(@class,'center')]//strong[contains(@class,'ng-binding')])[2]");
    private static final By LOGOUT_BTN       = By.xpath("//button[contains(text(),'Logout')]");
    private static final By TRANSACTION_ROWS = By.xpath("//table/tbody/tr");
    private static final By BACK_BTN         = By.xpath("//button[contains(text(),'Back')]");
    private static final By RESET_BTN        = By.xpath("//button[contains(text(),'Reset')]");
    private static final By ACCOUNT_SELECT   = By.id("accountSelect");

    public int getBalance() {
        log.debug("Reading account balance...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(BALANCE));
        int balance = Integer.parseInt(driver.findElement(BALANCE).getText().trim());
        log.debug("Current balance: {}", balance);
        return balance;
    }

    public AccountPage selectAccount(String accountNumber) {
        log.debug("Selecting account: {}", accountNumber);
        selectByText(ACCOUNT_SELECT, accountNumber);
        return this;
    }

    public AccountPage deposit(String amount) {
        log.debug("Depositing amount: {}", amount);
        click(DEPOSIT_TAB);
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(AMOUNT_INPUT));
        input.clear();
        input.sendKeys(amount);
        safeClick(SUBMIT_BTN);
        // Wait for status message to confirm deposit was processed
        waitForStatusMessage();
        log.debug("Deposit of {} completed", amount);
        return this;
    }

    public AccountPage withdraw(String amount) {
        log.debug("Withdrawing amount: {}", amount);
        click(WITHDRAW_TAB);
        // Wait for tab switch to complete and input to be ready
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(AMOUNT_INPUT));
        input.clear();
        input.sendKeys(amount);
        safeClick(SUBMIT_BTN);
        // Wait for status message to confirm withdrawal was processed
        waitForStatusMessage();
        log.debug("Withdrawal of {} completed", amount);
        return this;
    }

    /**
     * Clicks an element with retry logic to handle StaleElementReferenceException.
     * The DOM re-renders after tab switches in this Angular app, so we re-fetch
     * the element on each attempt rather than holding a stale reference.
     */
    private void safeClick(By locator) {
        int attempts = 0;
        while (attempts < 3) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
                log.debug("Clicked element: {}", locator);
                return;
            } catch (StaleElementReferenceException e) {
                attempts++;
                log.warn("StaleElementReferenceException on click attempt {}/3 for: {}", attempts, locator);
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            }
        }
        throw new RuntimeException("Failed to click element after 3 attempts: " + locator);
    }

    /**
     * Waits for any known status message to appear in the page source.
     * Uses page source scan instead of visibility check because Angular
     * uses ng-hide (element stays in DOM, just hidden) not ng-if.
     */
    private void waitForStatusMessage() {
        long end = System.currentTimeMillis() + 10_000;
        while (System.currentTimeMillis() < end) {
            String source = driver.getPageSource();
            if (source.contains("Deposit Successful")
                    || source.contains("Transaction successful")
                    || source.contains("Transaction Failed")) {
                return;
            }
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        }
        log.warn("No status message appeared within 10 seconds");
    }

    public String getStatusMessage() {
        String source = driver.getPageSource();
        if (source.contains("Deposit Successful"))   return "Deposit Successful";
        if (source.contains("Transaction successful")) return "Transaction successful";
        if (source.contains("Transaction Failed"))    return "Transaction Failed";
        log.warn("getStatusMessage: no known status message found in page source");
        return "";
    }

    public boolean isStatusMessageDisplayed() {
        return !getStatusMessage().isEmpty();
    }

    public AccountPage clickTransactionsTab() {
        log.debug("Clicking Transactions tab...");
        click(TRANSACTIONS_TAB);
        // Wait for transaction rows to begin loading
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(TRANSACTION_ROWS));
        } catch (Exception ignored) {
            log.warn("No transaction rows found after clicking Transactions tab");
        }
        return this;
    }

    public int getTransactionCount() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(TRANSACTION_ROWS));
        } catch (Exception ignored) {}
        int count = driver.findElements(TRANSACTION_ROWS).size();
        log.debug("Transaction row count: {}", count);
        return count;
    }

    public boolean isBackButtonVisible() {
        try {
            boolean visible = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(BACK_BTN)).isDisplayed();
            log.debug("Back button visible: {}", visible);
            return visible;
        } catch (Exception e) {
            log.warn("Back button not visible: {}", e.getMessage());
            return false;
        }
    }

    public boolean isResetButtonVisible() {
        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(RESET_BTN)).isDisplayed();
        } catch (Exception e) { return false; }
    }

    public HomePage logout() {
        log.debug("Logging out...");
        click(LOGOUT_BTN);
        return new HomePage();
    }
}