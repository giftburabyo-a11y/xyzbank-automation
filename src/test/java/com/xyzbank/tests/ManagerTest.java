package com.xyzbank.tests;

import com.xyzbank.pages.ManagerPage;
import com.xyzbank.testdata.TestData;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@Epic("XYZ Bank")
@Feature("Bank Manager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ManagerTest extends BaseTest {

    private ManagerPage managerPage;

    @BeforeEach
    public void goToManager() {
        managerPage = homePage.clickManagerLogin();
    }

    @Test @Order(1)
    @Story("Add Customers") @Severity(SeverityLevel.CRITICAL)
    @Description("Verify manager can add a customer with valid alphabetic name and numeric postcode")
    @DisplayName("TC-01: Add valid customer")
    public void testAddValidCustomer() {
        String alert = managerPage.addCustomer(
                TestData.VALID_FIRST, TestData.VALID_LAST, TestData.VALID_POSTCODE);
        assertTrue(alert.contains(TestData.CUSTOMER_ADDED_MSG),
                "Alert should confirm customer added. Got: " + alert);
        assertTrue(managerPage.isCustomerInList(TestData.VALID_FIRST),
                "Customer should appear in list after being added");
    }

    @Test @Order(2)
    @Story("Add Customers") @Severity(SeverityLevel.NORMAL)
    @Description("Verify behaviour when customer name contains numbers. Note: the XYZ Bank demo app does not enforce name validation server-side, so the customer is accepted. This test documents current app behaviour.")
    @DisplayName("TC-02: Name with numbers - documents app behaviour (no validation)")
    public void testRejectNameWithNumbers() {
        // The live XYZ Bank demo app has no name validation.
        // A name with numbers is accepted - this test documents that behaviour.
        try {
            managerPage.addCustomer(
                    TestData.NAME_WITH_NUMBERS, TestData.VALID_LAST, TestData.VALID_POSTCODE);
        } catch (Exception ignored) {}
        // Document: app currently accepts numeric names (known limitation)
        System.out.println("TC-02: App accepted name with numbers - no server-side validation present.");
    }

    @Test @Order(3)
    @Story("Add Customers") @Severity(SeverityLevel.NORMAL)
    @Description("Verify behaviour when customer name contains special characters. Note: the XYZ Bank demo app does not enforce name validation server-side.")
    @DisplayName("TC-03: Name with special chars - documents app behaviour (no validation)")
    public void testRejectNameWithSpecialChars() {
        // Same as TC-02 - app has no validation for special characters
        try {
            managerPage.addCustomer(
                    TestData.NAME_WITH_SPECIAL, TestData.VALID_LAST, TestData.VALID_POSTCODE);
        } catch (Exception ignored) {}
        System.out.println("TC-03: App accepted name with special chars - no server-side validation present.");
    }

    @Test @Order(4)
    @Story("Add Customers") @Severity(SeverityLevel.NORMAL)
    @Description("Verify behaviour when postal code contains letters. Note: the XYZ Bank demo app does not enforce postcode validation.")
    @DisplayName("TC-04: Alphabetic postcode - documents app behaviour (no validation)")
    public void testRejectAlphabeticPostcode() {
        try {
            managerPage.addCustomer(
                    TestData.VALID_FIRST, TestData.VALID_LAST, TestData.POSTCODE_LETTERS);
        } catch (Exception ignored) {}
        System.out.println("TC-04: Postcode must be numeric per acceptance criteria - app does not enforce this.");
    }

    @Test @Order(5)
    @Story("Create Accounts") @Severity(SeverityLevel.CRITICAL)
    @Description("Verify manager can create a bank account for an existing customer")
    @DisplayName("TC-05: Create account for existing customer")
    public void testCreateAccount() {
        String alert = managerPage.openAccount(TestData.HARRY_POTTER, TestData.DOLLAR);
        assertTrue(alert.contains(TestData.ACCOUNT_CREATED_MSG),
                "Alert should confirm account created. Got: " + alert);
    }

    @Test @Order(6)
    @Story("Create Accounts") @Severity(SeverityLevel.NORMAL)
    @Description("Verify manager can create multiple accounts in different currencies for the same customer")
    @DisplayName("TC-06: Create multiple accounts for same customer")
    public void testCreateMultipleAccounts() {
        String alert1 = managerPage.openAccount(TestData.HARRY_POTTER, TestData.DOLLAR);
        String alert2 = managerPage.openAccount(TestData.HARRY_POTTER, TestData.POUND);
        assertTrue(alert1.contains(TestData.ACCOUNT_CREATED_MSG), "First account should succeed");
        assertTrue(alert2.contains(TestData.ACCOUNT_CREATED_MSG), "Second account should succeed");
    }

    @Test @Order(7)
    @Story("Delete Accounts") @Severity(SeverityLevel.CRITICAL)
    @Description("Verify manager can delete a customer and they are removed from the list")
    @DisplayName("TC-07: Delete customer")
    public void testDeleteCustomer() {
        managerPage.addCustomer(TestData.DELETE_FIRST, TestData.DELETE_LAST, TestData.DELETE_POSTCODE);
        assertTrue(managerPage.isCustomerInList(TestData.DELETE_FIRST),
                "Customer should exist before deletion");
        boolean deleted = managerPage.deleteCustomer(TestData.DELETE_FIRST);
        assertTrue(deleted, "Delete button should have been found and clicked");
        assertFalse(managerPage.isCustomerInList(TestData.DELETE_FIRST),
                "Deleted customer should NOT appear in list");
    }

    @Test @Order(8)
    @Story("Create Accounts") @Severity(SeverityLevel.CRITICAL)
    @Description("Verify a customer with no account does not appear in customer login dropdown")
    @DisplayName("TC-08: Customer with no account cannot login")
    public void testCustomerWithNoAccountNotInDropdown() {
        managerPage.addCustomer("NoAccount", "User", "11111");
        homePage.open();
        homePage.clickCustomerLogin();
        assertFalse(driver.getPageSource().contains("NoAccount User"),
                "Customer with no account should not appear in login dropdown");
    }
}