package com.xyzbank.testdata;

import java.util.UUID;

public class TestData {

    public static final String URL =
            "https://www.globalsqa.com/angularJs-protractor/BankingProject/#/login";

    // ── Pre-existing customers in the XYZ Bank app ─────────────────────
    public static final String HARRY_POTTER       = "Harry Potter";
    public static final String HERMOINE_GRANGER   = "Hermoine Granger";
    public static final String RON_WEASLY         = "Ron Weasly";
    public static final String ALBUS_DUMBLEDORE   = "Albus Dumbledore";
    public static final String NEVILLE_LONGBOTTOM = "Neville Longbottom";

    // ── Unique customer generator for CustomerTest ──────────────────────
    // Each call returns a different name so tests never clash with each other
    // Example output: "TestA3f9b2 User"
    public static String uniqueFirst() {
        return "Test" + UUID.randomUUID().toString().replace("-", "").substring(0, 6);
    }
    public static String uniqueLast()     { return "User"; }
    public static String uniquePostcode() { return "10001"; }

    // ── Valid new customer data (used by ManagerTest) ───────────────────
    public static final String VALID_FIRST    = "John";
    public static final String VALID_LAST     = "Smith";
    public static final String VALID_POSTCODE = "12345";

    // ── Customer used only for the delete test ──────────────────────────
    public static final String DELETE_FIRST    = "DeleteMe";
    public static final String DELETE_LAST     = "TestUser";
    public static final String DELETE_POSTCODE = "99999";

    // ── Invalid data for negative/validation tests ──────────────────────
    public static final String NAME_WITH_NUMBERS = "John123";
    public static final String NAME_WITH_SPECIAL = "John@!#";
    public static final String POSTCODE_LETTERS  = "ABCDE";

    // ── Deposit and withdrawal amounts ──────────────────────────────────
    public static final String DEPOSIT_1000  = "1000";
    public static final String DEPOSIT_500   = "500";
    public static final String WITHDRAW_200  = "200";
    public static final String WITHDRAW_OVER = "99999";
    public static final String AMOUNT_ZERO   = "0";

    // ── Currency options ────────────────────────────────────────────────
    public static final String DOLLAR = "Dollar";
    public static final String POUND  = "Pound";
    public static final String RUPEE  = "Rupee";

    // ── Expected messages shown by the app ──────────────────────────────
    public static final String CUSTOMER_ADDED_MSG  = "Customer added successfully";
    public static final String ACCOUNT_CREATED_MSG = "Account created successfully";
    public static final String DEPOSIT_SUCCESS_MSG  = "Deposit Successful";
    public static final String WITHDRAW_SUCCESS_MSG = "Transaction successful";
    public static final String WITHDRAW_FAIL_MSG    = "Transaction Failed";
}