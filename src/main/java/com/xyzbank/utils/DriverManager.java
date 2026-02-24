package com.xyzbank.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.time.Duration;

public class DriverManager {

    private static final ThreadLocal<WebDriver> driverThread = new ThreadLocal<>();

    public static WebDriver getDriver() {
        if (driverThread.get() == null) {
            WebDriverManager.chromedriver().setup();

            ChromeOptions options = new ChromeOptions();

            // headless=true → Chrome runs invisibly (Docker / GitHub Actions)
            // headless=false → Chrome opens normally on your Windows PC
            String headless = System.getProperty("headless", "false");
            if ("true".equalsIgnoreCase(headless)) {
                options.addArguments("--headless=new");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--window-size=1920,1080");
            }

            options.addArguments("--disable-extensions");
            options.addArguments("--disable-popup-blocking");

            WebDriver driver = new ChromeDriver(options);
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            driverThread.set(driver);
        }
        return driverThread.get();
    }

    public static void quitDriver() {
        if (driverThread.get() != null) {
            driverThread.get().quit();
            driverThread.remove();
        }
    }
}