package com.sf.BaseClass;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class BaseClass {

    protected  static Properties prop;
    protected WebDriver driver;
    @BeforeSuite
    public void loadConfig() throws IOException {
        prop = new Properties();
        FileInputStream fileInputStream = new FileInputStream("src/main/resources/application.properties");
        prop.load(fileInputStream);
    }
    @BeforeMethod
    public void setup() throws IOException, IllegalAccessException {
        launchBrowser();
        configureBrowser();
        staticWait(2);
    }
    private void launchBrowser() throws IllegalAccessException {
        String browser = prop.getProperty("browser");
        switch (browser.toLowerCase()) {
            case "chrome":
                driver = new ChromeDriver();
                break;
            case "firefox":
                driver = new FirefoxDriver();
                break;
            case "edge":
                driver = new EdgeDriver();
                break;
            default:
                throw new IllegalAccessException("Browser not Supported: " + browser);
        }
    }

    private void configureBrowser() {
        int implicitWait = Integer.parseInt(prop.getProperty("implicitWait"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        driver.manage().window().maximize();
        try {
            driver.get(prop.getProperty("url"));
        } catch (Exception e) {
            System.out.println("FAILED TO GET URL: " + e.getMessage());
        }
    }



    @AfterMethod
    public void tearDown() {
        try {
            if (driver != null) {
                driver.quit();
            }
        }
        catch (Exception e) {
            System.out.println("FAILED TO TEAR DOWN: " + e.getMessage());
        }
    }
    public void staticWait(int seconds) {
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }


}
