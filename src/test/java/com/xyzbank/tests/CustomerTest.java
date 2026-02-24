package com.xyzbank.tests;

import com.xyzbank.pages.AccountPage;
import com.xyzbank.pages.ManagerPage;
import com.xyzbank.testdata.TestData;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

@Epic("XYZ Bank")
@Feature("Customer Banking")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerTest extends BaseTest {

    private AccountPage accountPage;
    private String customerFullName;

    @BeforeEach
    public void createCustomerAndLogin() {
        String firstName = TestData.uniqueFirst();
        String lastName  = TestData.uniqueLast();
        customerFullName = firstName + " " + lastName;

        ManagerPage manager = homePage.clickManagerLogin();
        manager.addCustomer(firstName, lastName, TestData.uniquePostcode());
        manager.openAccount(customerFullName, TestData.DOLLAR);

        homePage.open();
        accountPage = homePage.clickCustomerLogin().loginAs(customerFullName);
    }

    @Test @Order(1)
    @Story("Customer Login") @Severity(SeverityLevel.BLOCKER)
    @Description("Verify customer can log in successfully and see their account dashboard")
    @DisplayName("TC-09: Successful customer login")
    public void testCustomerLogin() {
        assertFalse(driver.getCurrentUrl().contains("login"),
                "After login URL should not still be the login page");
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> d.getPageSource().contains("Deposit"));
        assertTrue(driver.getPageSource().contains("Deposit"),
                "Dashboard should show Deposit tab after successful login");
    }

    @Test @Order(2)
    @Story("View Transactions") @Severity(SeverityLevel.NORMAL)
    @Description("Verify customer can navigate to the transactions tab and see their history")
    @DisplayName("TC-10: View transaction history")
    public void testViewTransactions() {
        accountPage.deposit(TestData.DEPOSIT_1000);
        accountPage.clickTransactionsTab();
        assertTrue(accountPage.getTransactionCount() > 0,
                "At least one transaction should be visible after making a deposit");
    }

    @Test @Order(3)
    @Story("Deposit Funds") @Severity(SeverityLevel.CRITICAL)
    @Description("Verify depositing a valid positive amount shows success message and updates balance")
    @DisplayName("TC-11: Deposit valid amount updates balance")
    public void testDepositUpdatesBalance() {
        accountPage.deposit(TestData.DEPOSIT_1000);
        assertTrue(accountPage.getStatusMessage().contains(TestData.DEPOSIT_SUCCESS_MSG),
                "Expected deposit success message. Got: " + accountPage.getStatusMessage());
        assertEquals(1000, accountPage.getBalance(),
                "Balance should be 1000 after depositing 1000 into a zero balance account");
    }

    @Test @Order(4)
    @Story("Deposit Funds") @Severity(SeverityLevel.NORMAL)
    @Description("Verify multiple deposits accumulate correctly in the account balance")
    @DisplayName("TC-12: Multiple deposits accumulate in balance")
    public void testMultipleDepositsAccumulate() {
        accountPage.deposit(TestData.DEPOSIT_1000);
        accountPage.deposit(TestData.DEPOSIT_500);
        assertEquals(1500, accountPage.getBalance(),
                "Balance should be 1500 after depositing 1000 then 500");
    }

    @Test @Order(5)
    @Story("Deposit Funds") @Severity(SeverityLevel.CRITICAL)
    @Description("Verify depositing zero does not change the account balance")
    @DisplayName("TC-13: Deposit zero does not change balance")
    public void testDepositZeroNoChange() {
        accountPage.deposit(TestData.AMOUNT_ZERO);
        assertEquals(0, accountPage.getBalance(),
                "Balance should remain 0 when depositing zero into a fresh account");
    }

    @Test @Order(6)
    @Story("Withdraw Money") @Severity(SeverityLevel.CRITICAL)
    @Description("Verify withdrawing a valid amount within balance updates balance correctly")
    @DisplayName("TC-14: Withdraw valid amount updates balance")
    public void testWithdrawUpdatesBalance() {
        accountPage.deposit(TestData.DEPOSIT_1000);
        accountPage.withdraw(TestData.WITHDRAW_200);
        assertTrue(accountPage.getStatusMessage().contains(TestData.WITHDRAW_SUCCESS_MSG),
                "Expected withdrawal success message. Got: " + accountPage.getStatusMessage());
        assertEquals(800, accountPage.getBalance(),
                "Balance should be 800 after depositing 1000 and withdrawing 200");
    }

    @Test @Order(7)
    @Story("Withdraw Money") @Severity(SeverityLevel.NORMAL)
    @Description("Verify customer can withdraw the exact balance amount leaving zero")
    @DisplayName("TC-15: Withdraw exact balance leaves zero")
    public void testWithdrawExactBalance() {
        accountPage.deposit(TestData.DEPOSIT_1000);
        accountPage.withdraw(TestData.DEPOSIT_1000);
        assertTrue(accountPage.getStatusMessage().contains(TestData.WITHDRAW_SUCCESS_MSG),
                "Withdrawing exact balance should succeed");
        assertEquals(0, accountPage.getBalance(),
                "Balance should be zero after withdrawing full amount");
    }

    @Test @Order(8)
    @Story("Withdraw Money") @Severity(SeverityLevel.CRITICAL)
    @Description("Verify withdrawal that exceeds balance is rejected and balance remains unchanged")
    @DisplayName("TC-16: Withdraw more than balance fails")
    public void testWithdrawOverBalanceFails() {
        accountPage.deposit(TestData.DEPOSIT_1000);
        accountPage.withdraw(TestData.WITHDRAW_OVER);
        assertTrue(accountPage.getStatusMessage().contains(TestData.WITHDRAW_FAIL_MSG),
                "Expected transaction failed message. Got: " + accountPage.getStatusMessage());
        assertEquals(1000, accountPage.getBalance(),
                "Balance should be unchanged after failed withdrawal");
    }

    @Test @Order(9)
    @Story("Withdraw Money") @Severity(SeverityLevel.NORMAL)
    @Description("Verify withdrawing zero does not change the account balance")
    @DisplayName("TC-17: Withdraw zero does not change balance")
    public void testWithdrawZeroNoChange() {
        accountPage.deposit(TestData.DEPOSIT_1000);
        accountPage.withdraw(TestData.AMOUNT_ZERO);
        assertEquals(1000, accountPage.getBalance(),
                "Balance should not change when withdrawing zero");
    }

    @Test @Order(10)
    @Story("Transaction Security") @Severity(SeverityLevel.CRITICAL)
    @Description("Verify transaction history is read-only and the customer cannot edit past transactions")
    @DisplayName("TC-18: Transaction history is read-only")
    public void testTransactionHistoryReadOnly() {
        accountPage.deposit(TestData.DEPOSIT_1000);
        accountPage.clickTransactionsTab();
        assertTrue(accountPage.isBackButtonVisible(),
                "Back button should be visible on transactions page");
        assertFalse(driver.getPageSource().contains("editTransaction"),
                "There should be no editable transaction fields in customer view");
    }

    @Test @Order(11)
    @Story("View Transactions") @Severity(SeverityLevel.NORMAL)
    @Description("Verify that a successful deposit creates a Credit entry in transaction history")
    @DisplayName("TC-19: Deposit creates Credit transaction entry")
    public void testDepositCreatesCreditEntry() {
        accountPage.deposit(TestData.DEPOSIT_1000);
        accountPage.clickTransactionsTab();
        assertTrue(driver.getPageSource().contains("Credit"),
                "A deposit should appear as a Credit entry in transaction history");
    }

    @Test @Order(12)
    @Story("View Transactions") @Severity(SeverityLevel.NORMAL)
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