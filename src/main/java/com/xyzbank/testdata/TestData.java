package com.xyzbank.testdata;

public class TestData {

    public static final String URL =
            "https://www.globalsqa.com/angularJs-protractor/BankingProject/#/login";

    // ── Pre-existing customers in the XYZ Bank app ─────────────────────
    // These are loaded by default — no setup needed to use them
    public static final String HARRY_POTTER       = "Harry Potter";
    public static final String HERMOINE_GRANGER   = "Hermoine Granger";
    public static final String RON_WEASLY         = "Ron Weasly";
    public static final String ALBUS_DUMBLEDORE   = "Albus Dumbledore";
    public static final String NEVILLE_LONGBOTTOM = "Neville Longbottom";

    // ── Valid new customer data ─────────────────────────────────────────
    public static final String VALID_FIRST    = "John";
    public static final String VALID_LAST     = "Smith";
    public static final String VALID_POSTCODE = "12345";

    // ── Separate customer used only for the delete test ─────────────────
    // We never delete a real customer — always a freshly created one
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

    // ── Currency options in the Open Account dropdown ───────────────────
    public static final String DOLLAR = "Dollar";
    public static final String POUND  = "Pound";
    public static final String RUPEE  = "Rupee";

    // ── Expected messages shown by the app ──────────────────────────────
    public static final String CUSTOMER_ADDED_MSG   = "Customer added successfully";
    public static final String ACCOUNT_CREATED_MSG  = "Account created successfully";
    public static final String DEPOSIT_SUCCESS_MSG  = "Deposit Successful";
    public static final String WITHDRAW_SUCCESS_MSG = "Transaction successful";
    public static final String WITHDRAW_FAIL_MSG    = "Transaction Failed";
}