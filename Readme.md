# XYZ Bank Test Automation Framework

A Selenium-based UI test automation framework for the XYZ Bank web application, built with Java, JUnit 5, and Allure reporting.

## Tech Stack

- Java 11
- Selenium WebDriver
- JUnit 5
- Allure Reports
- WebDriverManager
- SLF4J logging

## Project Structure

```
src/
  main/java/com/xyzbank/
    pages/
      customer/     AccountPage, CustomerLoginPage
      home/         HomePage
      manger/       ManagerPage
    utils/          PageHelper
  test/java/com/xyzbank/
    base/           BaseTest
    testdata/       TestData
    tests/          CustomerTest, ManagerTest
```

## Design

The framework follows the **Page Object Model** pattern with composition. Each page class holds a `PageHelper` instance for all Selenium interactions, and uses `@FindBy` annotations with PageFactory for element declarations. `BaseTest` handles driver lifecycle and initializes all page objects before each test.

## Running Tests

Run all tests:
```bash
mvn test
```

Run in headless mode:
```bash
mvn test -Dheadless=true
```

Generate Allure report:
```bash
mvn allure:serve
```

## Test Coverage

- **Manager tests** (TC-01 to TC-08): Add customers, open accounts, delete customers
- **Customer tests** (TC-09 to TC-20): Login, deposit, withdraw, transaction history