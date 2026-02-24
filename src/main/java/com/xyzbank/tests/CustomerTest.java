package com.xyzbank.tests;

import com.xyzbank.pages.AccountPage;
import com.xyzbank.pages.CustomerLoginPage;
import com.xyzbank.testdata.TestData;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@Epic("XYZ Bank")
@Feature("Customer Banking")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerTest extends BaseTest {

    private AccountPage accountPage;

    @BeforeEach
    public void loginAsHarry() {
        // Harry Potter is a pre-existing customer with accounts already created
        // We can log in and test immediately without any manager setup
        CustomerLoginPage loginPage = homePage.clickCustomerLogin();
        accountPage = loginPage.loginAs(TestData.HARRY_POTTER);
    }

    // ── TC-09 ─────────────────────────────────────────────────────────

    @Test @Order(1)
    @Story("Customer Login")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify customer can log in successfully and see their account dashboard")
    @DisplayName("TC-09: Successful customer login")
    public void testCustomerLogin() {
        // After login the URL changes away from the login page
        assertFalse(driver.getCurrentUrl().contains("login"),
                "After login URL should not still be the login page");

        // The account dashboard should show banking options
        assertTrue(driver.getPageSource().contains("Deposit"),
                "Dashboard should show Deposit tab after successful login");
    }

    // ── TC-10 ─────────────────────────────────────────────────────────

    @Test @Order(2)
    @Story("View Transactions")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify customer can navigate to the transactions tab and see their history")
    @DisplayName("TC-10: View transaction history")
    public void testViewTransactions() {
        // Make a deposit so there is at least one transaction to view
        accountPage.deposit(TestData.DEPOSIT_1000);
        accountPage.clickTransactionsTab();

        assertTrue(accountPage.getTransactionCount() > 0,
                "At least one transaction should be visible after making a deposit");
    }

    // ── TC-11 ─────────────────────────────────────────────────────────

    @Test @Order(3)
    @Story("Deposit Funds")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify depositing a valid positive amount shows success message and updates balance")
    @DisplayName("TC-11: Deposit valid amount updates balance")
    public void testDepositUpdatesBalance() {
        int before = accountPage.getBalance();

        accountPage.deposit(TestData.DEPOSIT_1000);

        assertTrue(accountPage.getStatusMessage().contains(TestData.DEPOSIT_SUCCESS_MSG),
                "Expected deposit success message. Got: " + accountPage.getStatusMessage());

        assertEquals(before + 1000, accountPage.getBalance(),
                "Balance should increase by the deposited amount");
    }

    // ── TC-12 ─────────────────────────────────────────────────────────

    @Test @Order(4)
    @Story("Deposit Funds")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify multiple deposits accumulate correctly in the account balance")
    @DisplayName("TC-12: Multiple deposits accumulate in balance")
    public void testMultipleDepositsAccumulate() {
        int before = accountPage.getBalance();

        accountPage.deposit(TestData.DEPOSIT_1000);
        accountPage.deposit(TestData.DEPOSIT_500);

        assertEquals(before + 1500, accountPage.getBalance(),
                "Balance should accumulate correctly across multiple deposits");
    }

    // ── TC-13 ─────────────────────────────────────────────────────────

    @Test @Order(5)
    @Story("Deposit Funds")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify depositing zero does not change the account balance")
    @DisplayName("TC-13: Deposit zero does not change balance")
    public void testDepositZeroNoChange() {
        int before = accountPage.getBalance();

        accountPage.deposit(TestData.AMOUNT_ZERO);

        assertEquals(before, accountPage.getBalance(),
                "Balance should not change when depositing zero");
    }

    // ── TC-14 ─────────────────────────────────────────────────────────

    @Test @Order(6)
    @Story("Withdraw Money")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify withdrawing a valid amount within balance updates balance correctly")
    @DisplayName("TC-14: Withdraw valid amount updates balance")
    public void testWithdrawUpdatesBalance() {
        accountPage.deposit(TestData.DEPOSIT_1000);
        int afterDeposit = accountPage.getBalance();

        accountPage.withdraw(TestData.WITHDRAW_200);

        assertTrue(accountPage.getStatusMessage().contains(TestData.WITHDRAW_SUCCESS_MSG),
                "Expected withdrawal success message. Got: " + accountPage.getStatusMessage());

        assertEquals(afterDeposit - 200, accountPage.getBalance(),
                "Balance should decrease by the withdrawal amount");
    }

    // ── TC-15 ─────────────────────────────────────────────────────────

    @Test @Order(7)
    @Story("Withdraw Money")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify customer can withdraw the exact balance amount leaving zero")
    @DisplayName("TC-15: Withdraw exact balance leaves zero")
    public void testWithdrawExactBalance() {
        accountPage.deposit("500");
        int balance = accountPage.getBalance();

        accountPage.withdraw(String.valueOf(balance));

        assertTrue(accountPage.getStatusMessage().contains(TestData.WITHDRAW_SUCCESS_MSG),
                "Withdrawing exact balance should succeed");

        assertEquals(0, accountPage.getBalance(),
                "Balance should be zero after withdrawing full amount");
    }

    // ── TC-16 ─────────────────────────────────────────────────────────

    @Test @Order(8)
    @Story("Withdraw Money")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify withdrawal that exceeds balance is rejected and balance remains unchanged")
    @DisplayName("TC-16: Withdraw more than balance fails")
    public void testWithdrawOverBalanceFails() {
        accountPage.deposit("100");
        int before = accountPage.getBalance();

        accountPage.withdraw(TestData.WITHDRAW_OVER);

        assertTrue(accountPage.getStatusMessage().contains(TestData.WITHDRAW_FAIL_MSG),
                "Expected transaction failed message. Got: " + accountPage.getStatusMessage());

        assertEquals(before, accountPage.getBalance(),
                "Balance should be unchanged after failed withdrawal");
    }

    // ── TC-17 ─────────────────────────────────────────────────────────

    @Test @Order(9)
    @Story("Withdraw Money")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify withdrawing zero does not change the account balance")
    @DisplayName("TC-17: Withdraw zero does not change balance")
    public void testWithdrawZeroNoChange() {
        accountPage.deposit(TestData.DEPOSIT_1000);
        int before = accountPage.getBalance();

        accountPage.withdraw(TestData.AMOUNT_ZERO);

        assertEquals(before, accountPage.getBalance(),
                "Balance should not change when withdrawing zero");
    }

    // ── TC-18 ─────────────────────────────────────────────────────────

    @Test @Order(10)
    @Story("Transaction Security")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify transaction history is read-only and the customer cannot edit past transactions")
    @DisplayName("TC-18: Transaction history is read-only")
    public void testTransactionHistoryReadOnly() {
        accountPage.deposit(TestData.DEPOSIT_1000);
        accountPage.clickTransactionsTab();

        // Navigation back button should exist
        assertTrue(accountPage.isBackButtonVisible(),
                "Back button should be visible on transactions page");

        // No editable fields should exist inside the transaction rows
        assertFalse(driver.getPageSource().contains("editTransaction"),
                "There should be no editable transaction fields in customer view");
    }

    // ── TC-19 ─────────────────────────────────────────────────────────

    @Test @Order(11)
    @Story("View Transactions")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that a successful deposit creates a Credit entry in transaction history")
    @DisplayName("TC-19: Deposit creates Credit transaction entry")
    public void testDepositCreatesCreditEntry() {
        accountPage.deposit(TestData.DEPOSIT_1000);
        accountPage.clickTransactionsTab();

        assertTrue(driver.getPageSource().contains("Credit"),
                "A deposit should appear as a Credit entry in transaction history");
    }

    // ── TC-20 ─────────────────────────────────────────────────────────

    @Test @Order(12)
    @Story("View Transactions")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that a successful withdrawal creates a Debit entry in transaction history")
    @DisplayName("TC-20: Withdrawal creates Debit transaction entry")
    public void testWithdrawCreatesDebitEntry() {
        accountPage.deposit(TestData.DEPOSIT_1000);
        accountPage.withdraw(TestData.WITHDRAW_200);
        accountPage.clickTransactionsTab();

        assertTrue(driver.getPageSource().contains("Debit"),
                "A withdrawal should appear as a Debit entry in transaction history");
    }
}