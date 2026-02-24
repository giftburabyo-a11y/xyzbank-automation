package com.xyzbank.tests;

import com.xyzbank.pages.HomePage;
import com.xyzbank.utils.DriverManager;
import io.qameta.allure.Attachment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class BaseTest {

    protected HomePage homePage;
    protected WebDriver driver;

    @BeforeEach
    public void setUp() {
        driver   = DriverManager.getDriver();
        homePage = new HomePage();
        homePage.open();
    }

    @AfterEach
    public void tearDown() {
        // Always take screenshot at end of every test
        // Allure attaches it to the test report automatically
        captureScreenshot();
        DriverManager.quitDriver();
    }

    @Attachment(value = "Screenshot", type = "image/png")
    public byte[] captureScreenshot() {
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            return new byte[0];
        }
    }
}