package com.xyzbank.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class PageHelper {

    private static final Logger log = LoggerFactory.getLogger(PageHelper.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    public PageHelper(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void click(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        log.debug("Clicked element: {}", element);
    }

    public void type(WebElement element, String text) {
        wait.until(ExpectedConditions.visibilityOf(element)).clear();
        element.sendKeys(text);
        log.debug("Typed '{}' into element", text);
    }

    public String getText(WebElement element) {
        String text = wait.until(ExpectedConditions.visibilityOf(element)).getText();
        log.debug("Got text: {}", text);
        return text;
    }

    public void selectByText(WebElement element, String text) {
        new Select(wait.until(ExpectedConditions.visibilityOf(element)))
                .selectByVisibleText(text);
        log.debug("Selected '{}' from dropdown", text);
    }

    public String acceptAlertAndGetText() {
        wait.until(ExpectedConditions.alertIsPresent());
        String text = driver.switchTo().alert().getText();
        driver.switchTo().alert().accept();
        log.debug("Alert accepted with text: {}", text);
        return text;
    }

    public boolean isVisible(WebElement element) {
        try {
            boolean visible = wait.until(ExpectedConditions.visibilityOf(element)).isDisplayed();
            log.debug("Element visibility: {}", visible);
            return visible;
        } catch (Exception e) {
            log.warn("Element not visible: {}", e.getMessage());
            return false;
        }
    }

    public boolean isClickable(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            log.debug("Element is clickable");
            return true;
        } catch (Exception e) {
            log.warn("Element not clickable: {}", e.getMessage());
            return false;
        }
    }

    public void safeClick(WebElement element) {
        int attempts = 0;
        while (attempts < 3) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(element)).click();
                log.debug("Safe click succeeded on attempt {}", attempts + 1);
                return;
            } catch (StaleElementReferenceException e) {
                attempts++;
                log.warn("StaleElementReferenceException on attempt {}/3", attempts);
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            }
        }
        throw new RuntimeException("Failed to click element after 3 attempts");
    }

    public void waitForStatusMessage() {
        long end = System.currentTimeMillis() + 10_000;
        while (System.currentTimeMillis() < end) {
            String source = driver.getPageSource();
            if (source.contains("Deposit Successful")
                    || source.contains("Transaction successful")
                    || source.contains("Transaction Failed")) {
                log.debug("Status message detected");
                return;
            }
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        }
        log.warn("No status message appeared within 10 seconds");
    }

    public String getStatusMessage() {
        String source = driver.getPageSource();
        if (source.contains("Deposit Successful"))    return "Deposit Successful";
        if (source.contains("Transaction successful")) return "Transaction successful";
        if (source.contains("Transaction Failed"))    return "Transaction Failed";
        log.warn("No known status message found in page source");
        return "";
    }
}