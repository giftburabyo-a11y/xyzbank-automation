package com.xyzbank.base;

import com.xyzbank.pages.HomePage;
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
        log.debug("Initializing WebDriver...");
        driver   = createDriver();
        homePage = new HomePage(driver);
        homePage.open();
        log.debug("Browser opened and home page loaded");
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