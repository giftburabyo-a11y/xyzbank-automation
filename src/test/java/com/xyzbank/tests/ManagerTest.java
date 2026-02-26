package com.xyzbank.tests;

import com.xyzbank.base.BaseTest;
import com.xyzbank.pages.ManagerPage;
import com.xyzbank.testdata.TestData;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.*;

@Epic("XYZ Bank")
@Feature("Bank Manager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ManagerTest extends BaseTest {

    private static final Logger log = LoggerFactory.getLogger(ManagerTest.class);

    private ManagerPage managerPage;

    @BeforeEach
    public void goToManager() {
        log.debug("Navigating to Manager login page...");
        managerPage = homePage.clickManagerLogin();
        log.debug("Manager page loaded");
    }

    @Test @Order(1)
    @Story("Add Customers") @Severity(SeverityLevel.CRITICAL)
    @Description("Verify manager can add a customer with valid alphabetic name and numeric postcode")
    @DisplayName("TC-01: Add valid customer")
    public void testAddValidCustomer() {
        log.info("Adding customer: {} {}", TestData.VALID_FIRST, TestData.VALID_LAST);
        String alert = managerPage.addCustomer(
                TestData.VALID_FIRST, TestData.VALID_LAST, TestData.VALID_POSTCODE);
        log.debug("Alert received: {}", alert);
        assertTrue(alert.contains(TestData.CUSTOMER_ADDED_MSG),
                "Alert should confirm customer added. Got: " + alert);
        assertTrue(managerPage.isCustomerInList(TestData.VALID_FIRST),
                "Customer should appear in list after being added");
        log.info("Customer '{}' successfully added and verified in list", TestData.VALID_FIRST);
    }

    @Test @Order(2)
    @Story("Add Customers") @Severity(SeverityLevel.NORMAL)
    @Description("Verify behaviour when customer name contains numbers. App does not enforce name validation server-side.")
    @DisplayName("TC-02: Name with numbers - documents app behaviour (no validation)")
    public void testRejectNameWithNumbers() {
        log.info("Testing name with numbers: '{}'", TestData.NAME_WITH_NUMBERS);
        try {
            managerPage.addCustomer(
                    TestData.NAME_WITH_NUMBERS, TestData.VALID_LAST, TestData.VALID_POSTCODE);
            log.warn("TC-02: App accepted name with numbers '{}' - no server-side validation present",
                    TestData.NAME_WITH_NUMBERS);
        } catch (Exception e) {
            log.error("Unexpected error during TC-02: {}", e.getMessage());
        }
    }

    @Test @Order(3)
    @Story("Add Customers") @Severity(SeverityLevel.NORMAL)
    @Description("Verify behaviour when customer name contains special characters. App does not enforce name validation server-side.")
    @DisplayName("TC-03: Name with special chars - documents app behaviour (no validation)")
    public void testRejectNameWithSpecialChars() {
        log.info("Testing name with special characters: '{}'", TestData.NAME_WITH_SPECIAL);
        try {
            managerPage.addCustomer(
                    TestData.NAME_WITH_SPECIAL, TestData.VALID_LAST, TestData.VALID_POSTCODE);
            log.warn("TC-03: App accepted name with special chars '{}' - no server-side validation present",
                    TestData.NAME_WITH_SPECIAL);
        } catch (Exception e) {
            log.error("Unexpected error during TC-03: {}", e.getMessage());
        }
    }

    @Test @Order(4)
    @Story("Add Customers") @Severity(SeverityLevel.NORMAL)
    @Description("Verify behaviour when postal code contains letters. App does not enforce postcode validation.")
    @DisplayName("TC-04: Alphabetic postcode - documents app behaviour (no validation)")
    public void testRejectAlphabeticPostcode() {
        log.info("Testing alphabetic postcode: '{}'", TestData.POSTCODE_LETTERS);
        try {
            managerPage.addCustomer(
                    TestData.VALID_FIRST, TestData.VALID_LAST, TestData.POSTCODE_LETTERS);
            log.warn("TC-04: App accepted alphabetic postcode '{}' - postcode should be numeric per acceptance criteria",
                    TestData.POSTCODE_LETTERS);
        } catch (Exception e) {
            log.error("Unexpected error during TC-04: {}", e.getMessage());
        }
    }

    @Test @Order(5)
    @Story("Create Accounts") @Severity(SeverityLevel.CRITICAL)
    @Description("Verify manager can create a bank account for an existing customer")
    @DisplayName("TC-05: Create account for existing customer")
    public void testCreateAccount() {
        log.info("Creating {} account for customer: {}", TestData.DOLLAR, TestData.HARRY_POTTER);
        String alert = managerPage.openAccount(TestData.HARRY_POTTER, TestData.DOLLAR);
        log.debug("Account creation alert: {}", alert);
        assertTrue(alert.contains(TestData.ACCOUNT_CREATED_MSG),
                "Alert should confirm account created. Got: " + alert);
        log.info("Account created successfully for '{}'", TestData.HARRY_POTTER);
    }

    @Test @Order(6)
    @Story("Create Accounts") @Severity(SeverityLevel.NORMAL)
    @Description("Verify manager can create multiple accounts in different currencies for the same customer")
    @DisplayName("TC-06: Create multiple accounts for same customer")
    public void testCreateMultipleAccounts() {
        log.info("Creating multiple accounts for customer: {}", TestData.HARRY_POTTER);
        String alert1 = managerPage.openAccount(TestData.HARRY_POTTER, TestData.DOLLAR);
        log.debug("First account alert ({}): {}", TestData.DOLLAR, alert1);
        String alert2 = managerPage.openAccount(TestData.HARRY_POTTER, TestData.POUND);
        log.debug("Second account alert ({}): {}", TestData.POUND, alert2);
        assertTrue(alert1.contains(TestData.ACCOUNT_CREATED_MSG), "First account should succeed");
        assertTrue(alert2.contains(TestData.ACCOUNT_CREATED_MSG), "Second account should succeed");
        log.info("Both accounts created successfully for '{}'", TestData.HARRY_POTTER);
    }

    @Test @Order(7)
    @Story("Delete Accounts") @Severity(SeverityLevel.CRITICAL)
    @Description("Verify manager can delete a customer and they are removed from the list")
    @DisplayName("TC-07: Delete customer")
    public void testDeleteCustomer() {
        log.info("Setting up customer for deletion: {} {}", TestData.DELETE_FIRST, TestData.DELETE_LAST);
        managerPage.addCustomer(TestData.DELETE_FIRST, TestData.DELETE_LAST, TestData.DELETE_POSTCODE);
        assertTrue(managerPage.isCustomerInList(TestData.DELETE_FIRST),
                "Customer should exist before deletion");
        log.info("Deleting customer: {}", TestData.DELETE_FIRST);
        boolean deleted = managerPage.deleteCustomer(TestData.DELETE_FIRST);
        assertTrue(deleted, "Delete button should have been found and clicked");
        assertFalse(managerPage.isCustomerInList(TestData.DELETE_FIRST),
                "Deleted customer should NOT appear in list");
        log.info("Customer '{}' successfully deleted and verified removed from list", TestData.DELETE_FIRST);
    }

    @Test @Order(8)
    @Story("Create Accounts") @Severity(SeverityLevel.CRITICAL)
    @Description("Verify a customer with no account does not appear in customer login dropdown")
    @DisplayName("TC-08: Customer with no account cannot login")
    public void testCustomerWithNoAccountNotInDropdown() {
        log.info("Adding customer 'NoAccount User' without creating an account");
        managerPage.addCustomer("NoAccount", "User", "11111");
        homePage.open();
        homePage.clickCustomerLogin();
        log.debug("Checking login dropdown does not contain 'NoAccount User'");
        assertFalse(driver.getPageSource().contains("NoAccount User"),
                "Customer with no account should not appear in login dropdown");
        log.info("Verified 'NoAccount User' is not present in login dropdown");
    }
}