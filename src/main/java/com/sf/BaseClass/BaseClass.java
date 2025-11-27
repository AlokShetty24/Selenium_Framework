package com.sf.BaseClass;

import com.sf.ActionDriver.ActionDriver;
import com.sf.Utilities.ExtentManager;
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
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class BaseClass {

    protected static Properties prop;
//    protected WebDriver driver;
//    private static ActionDriver actionDriver;
    private static ThreadLocal<WebDriver> driver= new ThreadLocal<>();
    private static ThreadLocal<ActionDriver> actionDriver= new ThreadLocal<>();
    public static final Logger logger = LoggerManager.getLogger(BaseClass.class);

    // -------------------- LOAD CONFIG --------------------
    @BeforeSuite
    public void loadConfig() throws IOException {
        logger.info("Initializing Properties file loading...");
        prop = new Properties();

        try (FileInputStream fis = new FileInputStream("src/main/resources/application.properties")) {
            prop.load(fis);
            logger.info("Properties file loaded successfully.");
        } catch (IOException e) {
            logger.error("Failed to load properties file: {}", e.getMessage());
            throw e;
        }
        ExtentManager.getReporter();
    }

    // -------------------- SETUP BEFORE EVERY TEST --------------------
    @BeforeMethod
    public synchronized void setup() throws IOException, IllegalAccessException {
        logger.info("Test Setup started...");
        launchBrowser();
        configureBrowser();
        logger.debug("Static wait for 2 seconds before proceeding...");
        staticWait(2);
        logger.info("WebDriver initialized and browser launched successfully.");

//        if (actionDriver == null) {
//            logger.debug("Creating new ActionDriver instance...");
//            actionDriver = new ActionDriver(driver);
//            logger.info("ActionDriver instance created."+Thread.currentThread().getId());


        //initilize action driver for current thread
        actionDriver.set(new ActionDriver(getDriver()));
        logger.info("Action Driver initialized for thread :" + Thread.currentThread().getId());

    }

    // -------------------- LAUNCH BROWSER --------------------
    private void launchBrowser() throws IllegalAccessException {
        String browser = prop.getProperty("browser");
        logger.info("Launching browser: {}", browser);

        switch (browser.toLowerCase()) {
            case "chrome":
                logger.debug("Initializing ChromeDriver...");
//                driver = new ChromeDriver();
                   driver.set(new ChromeDriver());
                   ExtentManager.registerDriver(getDriver());
                break;
            case "firefox":
                logger.debug("Initializing FirefoxDriver...");
//                driver = new FirefoxDriver();
                driver.set(new FirefoxDriver());
                ExtentManager.registerDriver(getDriver());
                break;
            case "edge":
                logger.debug("Initializing EdgeDriver...");
//                driver = new EdgeDriver();
                driver.set(new EdgeDriver());
                ExtentManager.registerDriver(getDriver());
                break;
            default:
                logger.error("Browser not supported: {}", browser);
                throw new IllegalAccessException("Browser not supported: " + browser);
        }

        logger.info("{} browser launched successfully.", browser);
    }

    // -------------------- CONFIGURE BROWSER --------------------
    private void configureBrowser() {
        logger.info("Configuring browser settings...");
        try {
            int implicitWait = Integer.parseInt(prop.getProperty("implicitWait"));
            logger.debug("Applying implicit wait: {} seconds", implicitWait);
            getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));

            logger.debug("Maximizing browser window...");
            getDriver().manage().window().maximize();

            String url = prop.getProperty("url");
            logger.info("Navigating to URL: {}", url);
            getDriver().get(url);

        } catch (Exception e) {
            logger.error("Browser configuration failed: {}", e.getMessage());
        }
    }

    // -------------------- TEARDOWN AFTER TEST --------------------
    @AfterMethod
    public void tearDown() {
        logger.info("Test execution completed. Starting teardown...");
        try {
            if (getDriver() != null) {
                logger.debug("Closing browser...");
                getDriver().quit();
                logger.info("Browser closed successfully.");
            }
        } catch (Exception e) {
            logger.error("Failed during teardown: {}", e.getMessage());
        }
        driver.remove();
        actionDriver.remove();
//        driver = null;
//        actionDriver = null;
        logger.info("Driver and ActionDriver instances reset to null.");
        ExtentManager.stopTest();
    }

    // -------------------- STATIC WAIT --------------------
    public void staticWait(int seconds) {
        logger.debug("Static wait for {} seconds.", seconds);
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
    }

    // -------------------- GETTERS & SETTERS --------------------
    public WebDriver getDriver() {
        if (driver.get() == null) {
            logger.error("Attempted to access driver before initialization!");
            throw new IllegalStateException("Driver is not initialized");
        }
        logger.debug("Returning WebDriver instance.");
        return driver.get();
    }

    public void setDriver(WebDriver driverInstance) {
        logger.debug("Setting WebDriver instance manually.");
        driver.set(driverInstance);
    }

    public static Properties getProp() {
        logger.debug("Fetching Properties object.");
        return prop;
    }

    public static ActionDriver getActionDriver() {
        if (actionDriver.get() == null) {
            logger.error("Attempted to access ActionDriver before initialization!");
            throw new IllegalStateException("ActionDriver is not initialized");
        }
        logger.debug("Returning ActionDriver instance.");
        return actionDriver.get();
    }
}
