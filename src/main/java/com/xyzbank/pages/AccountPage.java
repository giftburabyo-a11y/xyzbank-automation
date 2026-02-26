package com.xyzbank.pages;

import com.xyzbank.utils.SeleniumHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountPage extends SeleniumHelper {

    private static final Logger log = LoggerFactory.getLogger(AccountPage.class);

    // ── Tabs ─────────────────────────────────────────────
    private static final By TRANSACTIONS_TAB = By.xpath("//button[contains(text(),'Transactions')]");
    private static final By DEPOSIT_TAB      = By.xpath("//button[contains(text(),'Deposit')]");
    private static final By WITHDRAW_TAB     = By.xpath("//button[contains(text(),'Withdrawl')]");

    // ── Form Elements ────────────────────────────────────
    private static final By AMOUNT_INPUT     = By.xpath("//input[@type='number' and @ng-model='amount']");
    private static final By SUBMIT_BTN       = By.xpath("//button[@type='submit']");

    // ── Account Info ─────────────────────────────────────
    private static final By BALANCE          = By.xpath("(//div[contains(@class,'center')]//strong[contains(@class,'ng-binding')])[2]");
    private static final By BACK_BTN         = By.xpath("//button[contains(text(),'Back')]");
    private static final By LOGOUT_BTN       = By.xpath("//button[contains(text(),'Logout')]");

    // ── Transactions Table ───────────────────────────────
    private static final By TRANSACTION_ROWS = By.xpath("//table/tbody/tr");

    public AccountPage(WebDriver driver) {
        super(driver);
    }

    // ── Balance ──────────────────────────────────────────
    public int getBalance() {
        log.debug("Reading account balance...");
        WebElement balanceEl = wait.until(ExpectedConditions.visibilityOfElementLocated(BALANCE));
        int balance = Integer.parseInt(balanceEl.getText().trim());
        log.debug("Current balance: {}", balance);
        return balance;
    }

    // ── Deposit ──────────────────────────────────────────
    public void deposit(String amount) {
        log.debug("Depositing amount: {}", amount);
        click(DEPOSIT_TAB);
        wait.until(ExpectedConditions.visibilityOfElementLocated(AMOUNT_INPUT));
        wait.until(ExpectedConditions.elementToBeClickable(AMOUNT_INPUT));
        type(AMOUNT_INPUT, amount);
        safeClick(SUBMIT_BTN);
        waitForStatusMessage();
        log.debug("Deposit of {} completed", amount);
    }

    // ── Withdraw ─────────────────────────────────────────
    public void withdraw(String amount) {
        log.debug("Withdrawing amount: {}", amount);
        click(WITHDRAW_TAB);
        wait.until(ExpectedConditions.visibilityOfElementLocated(AMOUNT_INPUT));
        wait.until(ExpectedConditions.elementToBeClickable(AMOUNT_INPUT));
        type(AMOUNT_INPUT, amount);
        safeClick(SUBMIT_BTN);
        waitForStatusMessage();
        log.debug("Withdrawal of {} completed", amount);
    }

    // ── Transactions ─────────────────────────────────────
    public void clickTransactionsTab() {
        log.debug("Clicking Transactions tab...");
        click(TRANSACTIONS_TAB);
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(TRANSACTION_ROWS));
        } catch (Exception e) {
            log.warn("No transaction rows found after clicking Transactions tab");
        }
    }

    public int getTransactionCount() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(TRANSACTION_ROWS));
        } catch (Exception ignored) {}
        int count = driver.findElements(TRANSACTION_ROWS)
                .stream()
                .filter(r -> !r.getText().trim().isEmpty())
                .toArray().length;
        log.debug("Transaction row count: {}", count);
        return count;
    }

    // ── Status Messages ──────────────────────────────────
    public String getStatusMessage() {
        String source = driver.getPageSource();
        if (source.contains("Deposit Successful"))    return "Deposit Successful";
        if (source.contains("Transaction successful")) return "Transaction successful";
        if (source.contains("Transaction Failed"))    return "Transaction Failed";
        log.warn("No known status message found in page source");
        return "";
    }

    // ── Navigation ───────────────────────────────────────
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

    public HomePage logout() {
        log.debug("Logging out...");
        click(LOGOUT_BTN);
        return new HomePage(driver);
    }

    // ── Private Helpers ──────────────────────────────────
    private void safeClick(By locator) {
        int attempts = 0;
        while (attempts < 3) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
                log.debug("Clicked element: {}", locator);
                return;
            } catch (StaleElementReferenceException e) {
                attempts++;
                log.warn("StaleElementReferenceException on attempt {}/3 for: {}", attempts, locator);
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            }
        }
        throw new RuntimeException("Failed to click element after 3 attempts: " + locator);
    }

    private void waitForStatusMessage() {
        long end = System.currentTimeMillis() + 10_000;
        while (System.currentTimeMillis() < end) {
            String source = driver.getPageSource();
            if (source.contains("Deposit Successful")
                    || source.contains("Transaction successful")
                    || source.contains("Transaction Failed")) {
                log.debug("Status message detected");
                return;
            }
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        }
        log.warn("No status message appeared within 10 seconds");
    }
}