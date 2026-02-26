package com.xyzbank.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;

public class SeleniumHelper {

    protected WebDriver driver;
    protected WebDriverWait wait;

    public SeleniumHelper(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String text) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        el.clear();
        el.sendKeys(text);
    }


    protected void selectByText(By locator, String text) {
        new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(locator)))
                .selectByVisibleText(text);
    }


    protected String acceptAlertAndGetText() {
        wait.until(ExpectedConditions.alertIsPresent());
        String text = driver.switchTo().alert().getText();
        driver.switchTo().alert().accept();
        return text;
    }
}