package com.xyzbank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;

public class AccountPage extends BasePage {

    // ── Tabs ──────────────────────────────────────────────────────────
    // IMPORTANT: "Withdrawl" is a real typo in the live XYZ Bank app
    // Do NOT fix it — it must match the button text exactly
    private static final By TRANSACTIONS_TAB = By.xpath("//button[text()='Transactions']");
    private static final By DEPOSIT_TAB      = By.xpath("//button[text()='Deposit']");
    private static final By WITHDRAW_TAB     = By.xpath("//button[text()='Withdrawl']");

    // ── Deposit / Withdraw form ────────────────────────────────────────
    private static final By AMOUNT_INPUT = By.xpath("//input[@type='number']");
    private static final By SUBMIT_BTN   = By.xpath("//button[@type='submit']");

    // ── Status message ─────────────────────────────────────────────────
    // The app uses class="error" for BOTH green success AND red failure messages
    private static final By STATUS_MSG = By.cssSelector(".error");

    // ── Balance ────────────────────────────────────────────────────────
    // The balance appears in a <strong> tag: "Balance : <strong>1000</strong>"
    // We use the xpath below to find the first strong sibling after "Balance"
    private static final By BALANCE = By.xpath(
            "//*[contains(text(),'Balance')]/following-sibling::strong[1]"
    );

    // ── Logout ─────────────────────────────────────────────────────────
    private static final By LOGOUT_BTN = By.xpath("//button[text()='Logout']");

    // ── Transactions page ──────────────────────────────────────────────
    private static final By TRANSACTION_ROWS = By.xpath("//table/tbody/tr");
    private static final By BACK_BTN         = By.xpath("//button[text()='Back']");
    private static final By RESET_BTN        = By.xpath("//button[text()='Reset']");

    // ── Account number dropdown (when customer has multiple accounts) ───
    private static final By ACCOUNT_SELECT = By.id("accountSelect");

    // ── Balance ────────────────────────────────────────────────────────
    public int getBalance() {
        String balanceText = getText(BALANCE).trim();
        return Integer.parseInt(balanceText);
    }

    // ── Account selection ───────────────────────────────────────────────
    // Harry Potter has multiple accounts — use this to pick a specific one
    public AccountPage selectAccount(String accountNumber) {
        selectByText(ACCOUNT_SELECT, accountNumber);
        return this;
    }

    // ── Deposit ────────────────────────────────────────────────────────
    public AccountPage deposit(String amount) {
        click(DEPOSIT_TAB);
        type(AMOUNT_INPUT, amount);
        click(SUBMIT_BTN);
        return this;
    }

    // ── Withdraw ───────────────────────────────────────────────────────
    public AccountPage withdraw(String amount) {
        click(WITHDRAW_TAB);
        type(AMOUNT_INPUT, amount);
        click(SUBMIT_BTN);
        return this;
    }

    // ── Status message ─────────────────────────────────────────────────
    public String getStatusMessage() {
        return getText(STATUS_MSG);
    }

    public boolean isStatusMessageDisplayed() {
        return isDisplayed(STATUS_MSG);
    }

    // ── Transactions ───────────────────────────────────────────────────
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

    // ── Logout ─────────────────────────────────────────────────────────
    public HomePage logout() {
        click(LOGOUT_BTN);
        return new HomePage();
    }
}