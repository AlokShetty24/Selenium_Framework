package com.sf.BaseClass;

import com.sf.ActionDriver.ActionDriver;
import com.sf.Utilities.LoggerManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class BaseClass {

    protected static Properties prop;

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static ThreadLocal<ActionDriver> actionDriver = new ThreadLocal<>();

    public static final Logger logger = LoggerManager.getLogger(BaseClass.class);

    // -------------------- LOAD CONFIG --------------------
    @BeforeSuite
    public void loadConfig() throws IOException {
        logger.info("Loading application.properties...");
        prop = new Properties();

        try (FileInputStream fis = new FileInputStream("src/main/resources/application.properties")) {
            prop.load(fis);
            logger.info("Properties loaded successfully.");
        }
    }

    // -------------------- SETUP BEFORE EVERY TEST --------------------
    @BeforeMethod
    public synchronized void setup(Method method) {
        logger.info("Setting up test: " + method.getName());

        launchBrowser();
        configureBrowser();
        staticWait(2);

        actionDriver.set(new ActionDriver(getDriver()));
        logger.info("ActionDriver initialized for thread: " + Thread.currentThread().getId());
    }

    // -------------------- LAUNCH BROWSER --------------------
    private void launchBrowser() {
        String browser = prop.getProperty("browser");
        logger.info("Launching browser: {}", browser);

        switch (browser.toLowerCase()) {
            case "chrome":
                driver.set(new ChromeDriver());
                break;

            case "firefox":
                driver.set(new FirefoxDriver());
                break;

            case "edge":
                driver.set(new EdgeDriver());
                break;

            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }

    // -------------------- CONFIGURE BROWSER --------------------
    private void configureBrowser() {
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(
                Integer.parseInt(prop.getProperty("implicitWait"))
        ));
        getDriver().manage().window().maximize();
        getDriver().get(prop.getProperty("url"));
    }

    // -------------------- TEARDOWN --------------------
    @AfterMethod
    public void tearDown() {
        try {
            if (getDriver() != null) {
                getDriver().quit();
            }
        } catch (Exception e) {
            logger.error("Error during teardown: {}", e.getMessage());
        }

        driver.remove();
        actionDriver.remove();
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static ActionDriver getActionDriver() {
        return actionDriver.get();
    }

    public static Properties getProp() {
        return prop;
    }

    public void staticWait(int seconds) {
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
    }
}
