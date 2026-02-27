package com.xyzbank.base;

import com.xyzbank.pages.home.HomePage;
import com.xyzbank.pages.customer.CustomerLoginPage;
import com.xyzbank.pages.customer.AccountPage;
import com.xyzbank.pages.manger.ManagerPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Attachment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseTest {

    protected static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    protected WebDriver driver;
    protected HomePage homePage;
    protected CustomerLoginPage customerLoginPage;
    protected AccountPage accountPage;
    protected ManagerPage managerPage;

    private static WebDriver createDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");

        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
        }

        return new ChromeDriver(options);
    }

    @BeforeEach
    public void setUp(TestInfo testInfo) {
        log.info("========== START: {} ==========", testInfo.getDisplayName());
        driver = createDriver();

        homePage          = new HomePage(driver);
        customerLoginPage = new CustomerLoginPage(driver);
        accountPage       = new AccountPage(driver);
        managerPage       = new ManagerPage(driver);

        homePage.open();
        log.debug("Browser opened and all pages initialized");
    }

    @AfterEach
    public void tearDown(TestInfo testInfo) {
        log.debug("Taking screenshot before closing browser...");
        captureScreenshot();
        if (driver != null) {
            driver.quit();
            driver = null;
            log.debug("WebDriver closed");
        }
        log.info("========== END: {} ==========", testInfo.getDisplayName());
    }

    @Attachment(value = "Screenshot", type = "image/png")
    public byte[] captureScreenshot() {
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            log.debug("Screenshot captured successfully");
            return screenshot;
        } catch (Exception e) {
            log.warn("Failed to capture screenshot: {}", e.getMessage());
            return new byte[0];
        }
    }
}