package com.xyzbank.tests;

import com.xyzbank.pages.HomePage;
import com.xyzbank.utils.DriverManager;
import io.qameta.allure.Attachment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseTest {

    protected static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    protected HomePage homePage;
    protected WebDriver driver;

    @BeforeEach
    public void setUp(TestInfo testInfo) {
        log.info("========== START: {} ==========", testInfo.getDisplayName());
        log.debug("Initializing WebDriver...");
        driver   = DriverManager.getDriver();
        homePage = new HomePage();
        homePage.open();
        log.debug("Browser opened and home page loaded");
    }

    @AfterEach
    public void tearDown(TestInfo testInfo) {
        log.debug("Taking screenshot before closing browser...");
        captureScreenshot();
        DriverManager.quitDriver();
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