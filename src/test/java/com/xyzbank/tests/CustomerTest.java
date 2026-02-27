package com.xyzbank.tests;

import com.xyzbank.base.BaseTest;
import com.xyzbank.testdata.TestData;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

@Epic("XYZ Bank")
@Feature("Customer Banking")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerTest extends BaseTest {

    private static final Logger log = LoggerFactory.getLogger(CustomerTest.class);

    @BeforeEach
    public void loginCustomer() {
        log.info("Logging in as pre-loaded customer: {}", TestData.HARRY_POTTER);
        accountPage = homePage.clickCustomerLogin().loginAs(TestData.HARRY_POTTER);
        log.info("Customer '{}' logged in successfully", TestData.HARRY_POTTER);
    }

    @Test @Order(1)
    @Story("Customer Login") @Severity(SeverityLevel.BLOCKER)
    @Description("Verify customer can log in successfully and see their account dashboard")
    @DisplayName("TC-09: Successful customer login")
    public void testCustomerLogin() {
        log.info("Verifying customer login for: {}", TestData.HARRY_POTTER);
        assertFalse(driver.getCurrentUrl().contains("login"),
                "After login URL should not still be the login page");
        assertTrue(driver.getPageSource().contains("Deposit"),
                "Dashboard should show Deposit tab after successful login");
        log.info("Login verified - dashboard loaded correctly");
    }

    @Test @Order(2)
    @Story("View Transactions") @Severity(SeverityLevel.NORMAL)
    @Description("Verify customer can navigate to the transactions tab and see their history")
    @DisplayName("TC-10: View transaction history")
    public void testViewTransactions() {
        log.info("Depositing {} to generate a transaction", TestData.DEPOSIT_1000);
        accountPage.deposit(TestData.DEPOSIT_1000);
        accountPage.clickTransactionsTab();
        int count = accountPage.getTransactionCount();
        log.debug("Transaction count found: {}", count);
        assertTrue(count > 0, "At least one transaction should be visible after making a deposit");
        log.info("Transaction history verified - {} transaction(s) found", count);
    }

    @Test @Order(3)
    @Story("Deposit Funds") @Severity(SeverityLevel.CRITICAL)
    @Description("Verify depositing a valid positive amount shows success message and updates balance")
    @DisplayName("TC-11: Deposit valid amount updates balance")
    public void testDepositUpdatesBalance() {
        log.info("Depositing amount: {}", TestData.DEPOSIT_1000);
        int balanceBefore = accountPage.getBalance();
        accountPage.deposit(TestData.DEPOSIT_1000);
        String status = accountPage.getStatusMessage();
        log.debug("Status message after deposit: '{}'", status);
        assertTrue(status.contains(TestData.DEPOSIT_SUCCESS_MSG),
                "Expected deposit success message. Got: " + status);
        int balanceAfter = accountPage.getBalance();
        log.debug("Balance before: {} after: {}", balanceBefore, balanceAfter);
        assertEquals(balanceBefore + 1000, balanceAfter,
                "Balance should increase by 1000 after deposit");
        log.info("Deposit verified - balance updated to {}", balanceAfter);
    }

    @Test @Order(4)
    @Story("Deposit Funds") @Severity(SeverityLevel.NORMAL)
    @Description("Verify multiple deposits accumulate correctly in the account balance")
    @DisplayName("TC-12: Multiple deposits accumulate in balance")
    public void testMultipleDepositsAccumulate() {
        log.info("Making two deposits: {} then {}", TestData.DEPOSIT_1000, TestData.DEPOSIT_500);
        int balanceBefore = accountPage.getBalance();
        accountPage.deposit(TestData.DEPOSIT_1000);
        accountPage.deposit(TestData.DEPOSIT_500);
        int balanceAfter = accountPage.getBalance();
        log.debug("Balance before: {} after: {}", balanceBefore, balanceAfter);
        assertEquals(balanceBefore + 1500, balanceAfter,
                "Balance should increase by 1500 after two deposits");
        log.info("Multiple deposits verified - final balance: {}", balanceAfter);
    }

    @Test @Order(5)
    @Story("Deposit Funds") @Severity(SeverityLevel.CRITICAL)
    @Description("Verify depositing zero does not change the account balance")
    @DisplayName("TC-13: Deposit zero does not change balance")
    public void testDepositZeroNoChange() {
        log.info("Depositing zero - expecting no balance change");
        int balanceBefore = accountPage.getBalance();
        accountPage.deposit(TestData.AMOUNT_ZERO);
        int balanceAfter = accountPage.getBalance();
        log.debug("Balance before: {} after: {}", balanceBefore, balanceAfter);
        assertEquals(balanceBefore, balanceAfter,
                "Balance should not change when depositing zero");
        log.info("Zero deposit verified - balance unchanged at {}", balanceAfter);
    }

    @Test @Order(6)
    @Story("Withdraw Money") @Severity(SeverityLevel.CRITICAL)
    @Description("Verify withdrawing a valid amount within balance updates balance correctly")
    @DisplayName("TC-14: Withdraw valid amount updates balance")
    public void testWithdrawUpdatesBalance() {
        log.info("Depositing {} then withdrawing {}", TestData.DEPOSIT_1000, TestData.WITHDRAW_200);
        accountPage.deposit(TestData.DEPOSIT_1000);
        int balanceAfterDeposit = accountPage.getBalance();
        accountPage.withdraw(TestData.WITHDRAW_200);
        String status = accountPage.getStatusMessage();
        log.debug("Status message after withdrawal: '{}'", status);
        assertTrue(status.contains(TestData.WITHDRAW_SUCCESS_MSG),
                "Expected withdrawal success message. Got: " + status);
        int balanceAfter = accountPage.getBalance();
        log.debug("Balance after deposit: {} after withdrawal: {}", balanceAfterDeposit, balanceAfter);
        assertEquals(balanceAfterDeposit - 200, balanceAfter,
                "Balance should decrease by 200 after withdrawal");
        log.info("Withdrawal verified - balance updated to {}", balanceAfter);
    }

    @Test @Order(7)
    @Story("Withdraw Money") @Severity(SeverityLevel.NORMAL)
    @Description("Verify customer can withdraw the exact balance amount leaving zero")
    @DisplayName("TC-15: Withdraw exact balance leaves zero")
    public void testWithdrawExactBalance() {
        log.info("Depositing {} then withdrawing same amount", TestData.DEPOSIT_1000);
        accountPage.deposit(TestData.DEPOSIT_1000);
        int balanceAfterDeposit = accountPage.getBalance();
        accountPage.withdraw(String.valueOf(balanceAfterDeposit));
        String status = accountPage.getStatusMessage();
        log.debug("Status after exact withdrawal: '{}'", status);
        assertTrue(status.contains(TestData.WITHDRAW_SUCCESS_MSG),
                "Withdrawing exact balance should succeed");
        int balanceAfter = accountPage.getBalance();
        assertEquals(0, balanceAfter,
                "Balance should be zero after withdrawing full amount");
        log.info("Exact balance withdrawal verified - balance is {}", balanceAfter);
    }

    @Test @Order(8)
    @Story("Withdraw Money") @Severity(SeverityLevel.CRITICAL)
    @Description("Verify withdrawal that exceeds balance is rejected and balance remains unchanged")
    @DisplayName("TC-16: Withdraw more than balance fails")
    public void testWithdrawOverBalanceFails() {
        log.info("Attempting to overdraw with {}", TestData.WITHDRAW_OVER);
        int balanceBefore = accountPage.getBalance();
        accountPage.withdraw(TestData.WITHDRAW_OVER);
        String status = accountPage.getStatusMessage();
        log.debug("Status after overdraw attempt: '{}'", status);
        assertTrue(status.contains(TestData.WITHDRAW_FAIL_MSG),
                "Expected transaction failed message. Got: " + status);
        int balanceAfter = accountPage.getBalance();
        assertEquals(balanceBefore, balanceAfter,
                "Balance should be unchanged after failed withdrawal");
        log.warn("Overdraw attempt correctly rejected - balance remains {}", balanceAfter);
    }

    @Test @Order(9)
    @Story("Withdraw Money") @Severity(SeverityLevel.NORMAL)
    @Description("Verify withdrawing zero does not change the account balance")
    @DisplayName("TC-17: Withdraw zero does not change balance")
    public void testWithdrawZeroNoChange() {
        log.info("Depositing {} then withdrawing zero", TestData.DEPOSIT_1000);
        accountPage.deposit(TestData.DEPOSIT_1000);
        int balanceAfterDeposit = accountPage.getBalance();
        accountPage.withdraw(TestData.AMOUNT_ZERO);
        int balanceAfter = accountPage.getBalance();
        log.debug("Balance after deposit: {} after zero withdrawal: {}", balanceAfterDeposit, balanceAfter);
        assertEquals(balanceAfterDeposit, balanceAfter,
                "Balance should not change when withdrawing zero");
        log.info("Zero withdrawal verified - balance unchanged at {}", balanceAfter);
    }

    @Test @Order(10)
    @Story("Transaction Security") @Severity(SeverityLevel.CRITICAL)
    @Description("Verify transaction history is read-only and the customer cannot edit past transactions")
    @DisplayName("TC-18: Transaction history is read-only")
    public void testTransactionHistoryReadOnly() {
        log.info("Checking transaction history is read-only");
        accountPage.deposit(TestData.DEPOSIT_1000);
        accountPage.clickTransactionsTab();
        boolean backVisible = accountPage.isBackButtonVisible();
        log.debug("Back button visible: {}", backVisible);
        assertTrue(backVisible, "Back button should be visible on transactions page");
        assertFalse(driver.getPageSource().contains("editTransaction"),
                "There should be no editable transaction fields");
        log.info("Transaction history read-only check passed");
    }

    @Test @Order(11)
    @Story("View Transactions") @Severity(SeverityLevel.NORMAL)
    @Description("Verify that a successful deposit creates a Credit entry in transaction history")
    @DisplayName("TC-19: Deposit creates Credit transaction entry")
    public void testDepositCreatesCreditEntry() {
        log.info("Verifying deposit creates a Credit entry in transaction history");
        accountPage.deposit(TestData.DEPOSIT_1000);
        accountPage.clickTransactionsTab();
        accountPage.getTransactionCount();
        boolean hasCredit = driver.getPageSource().contains("Credit");
        log.debug("Credit entry found in transaction history: {}", hasCredit);
        assertTrue(hasCredit, "A deposit should appear as a Credit entry in transaction history");
        log.info("Credit entry verified in transaction history");
    }

    @Test @Order(12)
    @Story("View Transactions") @Severity(SeverityLevel.NORMAL)
    @Description("Verify that a successful withdrawal creates a Debit entry in transaction history")
    @DisplayName("TC-20: Withdrawal creates Debit transaction entry")
    public void testWithdrawCreatesDebitEntry() {
        log.info("Verifying withdrawal creates a Debit entry in transaction history");
        accountPage.deposit(TestData.DEPOSIT_1000);
        accountPage.withdraw(TestData.WITHDRAW_200);
        accountPage.clickTransactionsTab();
        accountPage.getTransactionCount();
        boolean hasDebit = driver.getPageSource().contains("Debit");
        log.debug("Debit entry found in transaction history: {}", hasDebit);
        assertTrue(hasDebit, "A withdrawal should appear as a Debit entry in transaction history");
        log.info("Debit entry verified in transaction history");
    }
}