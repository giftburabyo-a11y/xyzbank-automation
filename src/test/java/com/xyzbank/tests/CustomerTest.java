package com.xyzbank.tests;

import com.xyzbank.pages.AccountPage;
import com.xyzbank.pages.ManagerPage;
import com.xyzbank.testdata.TestData;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

@Epic("XYZ Bank")
@Feature("Customer Banking")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerTest extends BaseTest {

    private static final Logger log = LoggerFactory.getLogger(CustomerTest.class);

    private AccountPage accountPage;
    private String customerFullName;

    @BeforeEach
    public void createCustomerAndLogin() {
        String firstName = TestData.uniqueFirst();
        String lastName  = TestData.uniqueLast();
        customerFullName = firstName + " " + lastName;
        log.info("Setting up test customer: {}", customerFullName);

        ManagerPage manager = homePage.clickManagerLogin();
        log.debug("Adding customer via manager page...");
        manager.addCustomer(firstName, lastName, TestData.uniquePostcode());

        log.debug("Opening {} account for customer: {}", TestData.DOLLAR, customerFullName);
        manager.openAccount(customerFullName, TestData.DOLLAR);

        homePage.open();
        log.debug("Logging in as customer: {}", customerFullName);
        accountPage = homePage.clickCustomerLogin().loginAs(customerFullName);
        log.info("Customer '{}' logged in successfully", customerFullName);
    }

    @Test @Order(1)
    @Story("Customer Login") @Severity(SeverityLevel.BLOCKER)
    @Description("Verify customer can log in successfully and see their account dashboard")
    @DisplayName("TC-09: Successful customer login")
    public void testCustomerLogin() {
        log.info("Verifying customer login for: {}", customerFullName);
        assertFalse(driver.getCurrentUrl().contains("login"),
                "After login URL should not still be the login page");
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> d.getPageSource().contains("Deposit"));
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
        accountPage.deposit(TestData.DEPOSIT_1000);
        String status = accountPage.getStatusMessage();
        log.debug("Status message after deposit: '{}'", status);
        assertTrue(status.contains(TestData.DEPOSIT_SUCCESS_MSG),
                "Expected deposit success message. Got: " + status);
        int balance = accountPage.getBalance();
        log.debug("Balance after deposit: {}", balance);
        assertEquals(1000, balance, "Balance should be 1000 after depositing 1000");
        log.info("Deposit verified - balance updated to {}", balance);
    }

    @Test @Order(4)
    @Story("Deposit Funds") @Severity(SeverityLevel.NORMAL)
    @Description("Verify multiple deposits accumulate correctly in the account balance")
    @DisplayName("TC-12: Multiple deposits accumulate in balance")
    public void testMultipleDepositsAccumulate() {
        log.info("Making two deposits: {} then {}", TestData.DEPOSIT_1000, TestData.DEPOSIT_500);
        accountPage.deposit(TestData.DEPOSIT_1000);
        accountPage.deposit(TestData.DEPOSIT_500);
        int balance = accountPage.getBalance();
        log.debug("Balance after two deposits: {}", balance);
        assertEquals(1500, balance, "Balance should be 1500 after depositing 1000 then 500");
        log.info("Multiple deposits verified - final balance: {}", balance);
    }

    @Test @Order(5)
    @Story("Deposit Funds") @Severity(SeverityLevel.CRITICAL)
    @Description("Verify depositing zero does not change the account balance")
    @DisplayName("TC-13: Deposit zero does not change balance")
    public void testDepositZeroNoChange() {
        log.info("Depositing zero - expecting no balance change");
        accountPage.deposit(TestData.AMOUNT_ZERO);
        int balance = accountPage.getBalance();
        log.debug("Balance after zero deposit: {}", balance);
        assertEquals(0, balance, "Balance should remain 0 when depositing zero");
        log.info("Zero deposit verified - balance unchanged at {}", balance);
    }

    @Test @Order(6)
    @Story("Withdraw Money") @Severity(SeverityLevel.CRITICAL)
    @Description("Verify withdrawing a valid amount within balance updates balance correctly")
    @DisplayName("TC-14: Withdraw valid amount updates balance")
    public void testWithdrawUpdatesBalance() {
        log.info("Depositing {} then withdrawing {}", TestData.DEPOSIT_1000, TestData.WITHDRAW_200);
        accountPage.deposit(TestData.DEPOSIT_1000);
        accountPage.withdraw(TestData.WITHDRAW_200);
        String status = accountPage.getStatusMessage();
        log.debug("Status message after withdrawal: '{}'", status);
        assertTrue(status.contains(TestData.WITHDRAW_SUCCESS_MSG),
                "Expected withdrawal success message. Got: " + status);
        int balance = accountPage.getBalance();
        log.debug("Balance after withdrawal: {}", balance);
        assertEquals(800, balance, "Balance should be 800 after depositing 1000 and withdrawing 200");
        log.info("Withdrawal verified - balance updated to {}", balance);
    }

    @Test @Order(7)
    @Story("Withdraw Money") @Severity(SeverityLevel.NORMAL)
    @Description("Verify customer can withdraw the exact balance amount leaving zero")
    @DisplayName("TC-15: Withdraw exact balance leaves zero")
    public void testWithdrawExactBalance() {
        log.info("Depositing {} then withdrawing same amount", TestData.DEPOSIT_1000);
        accountPage.deposit(TestData.DEPOSIT_1000);
        accountPage.withdraw(TestData.DEPOSIT_1000);
        String status = accountPage.getStatusMessage();
        log.debug("Status after exact withdrawal: '{}'", status);
        assertTrue(status.contains(TestData.WITHDRAW_SUCCESS_MSG),
                "Withdrawing exact balance should succeed");
        int balance = accountPage.getBalance();
        assertEquals(0, balance, "Balance should be zero after withdrawing full amount");
        log.info("Exact balance withdrawal verified - balance is {}", balance);
    }

    @Test @Order(8)
    @Story("Withdraw Money") @Severity(SeverityLevel.CRITICAL)
    @Description("Verify withdrawal that exceeds balance is rejected and balance remains unchanged")
    @DisplayName("TC-16: Withdraw more than balance fails")
    public void testWithdrawOverBalanceFails() {
        log.info("Depositing {} then attempting to overdraw with {}", TestData.DEPOSIT_1000, TestData.WITHDRAW_OVER);
        accountPage.deposit(TestData.DEPOSIT_1000);
        accountPage.withdraw(TestData.WITHDRAW_OVER);
        String status = accountPage.getStatusMessage();
        log.debug("Status after overdraw attempt: '{}'", status);
        assertTrue(status.contains(TestData.WITHDRAW_FAIL_MSG),
                "Expected transaction failed message. Got: " + status);
        int balance = accountPage.getBalance();
        assertEquals(1000, balance, "Balance should be unchanged after failed withdrawal");
        log.warn("Overdraw attempt correctly rejected - balance remains {}", balance);
    }

    @Test @Order(9)
    @Story("Withdraw Money") @Severity(SeverityLevel.NORMAL)
    @Description("Verify withdrawing zero does not change the account balance")
    @DisplayName("TC-17: Withdraw zero does not change balance")
    public void testWithdrawZeroNoChange() {
        log.info("Depositing {} then withdrawing zero", TestData.DEPOSIT_1000);
        accountPage.deposit(TestData.DEPOSIT_1000);
        accountPage.withdraw(TestData.AMOUNT_ZERO);
        int balance = accountPage.getBalance();
        log.debug("Balance after zero withdrawal: {}", balance);
        assertEquals(1000, balance, "Balance should not change when withdrawing zero");
        log.info("Zero withdrawal verified - balance unchanged at {}", balance);
    }

    @Test @Order(10)
    @Story("Transaction Security") @Severity(SeverityLevel.CRITICAL)
    @Description("Verify transaction history is read-only and the customer cannot edit past transactions")
    @DisplayName("TC-18: Transaction history is read-only")
    public void testTransactionHistoryReadOnly() {
        log.info("Checking transaction history is read-only for: {}", customerFullName);
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
