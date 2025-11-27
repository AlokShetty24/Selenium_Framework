package com.sf.Utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExtentManager {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static Map<Long, WebDriver> driverMap = new HashMap<>();


    /**
     * Initialize and return Extent Report instance
     */
    public synchronized static ExtentReports getReporter() {
        if (extent == null) {

            // Corrected path with "/" separator
            String reportPath = System.getProperty("user.dir") +
                    "/src/main/resources/ExtentReport/ExtentReport.html";

            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
            spark.config().setReportName("Automation Test Report");
            spark.config().setDocumentTitle("Orange HRM Report");
            spark.config().setTheme(Theme.DARK);

            extent = new ExtentReports();
            extent.attachReporter(spark);

            // System info
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Browser", System.getProperty("browser"));
            extent.setSystemInfo("Device", System.getProperty("device"));
            extent.setSystemInfo("Version", System.getProperty("version"));
            extent.setSystemInfo("OS Type", System.getProperty("os.type"));
            extent.setSystemInfo("User.dir", System.getProperty("user.dir"));
        }
        return extent;
    }


    /**
     * Start a new test node for current thread
     */
    public synchronized static ExtentTest startTest(String testName) {
        ExtentTest extentTest = getReporter().createTest(testName);
        test.set(extentTest);
        return extentTest;
    }


    /**
     * Flush the report
     */
    public synchronized static void stopTest() {
        getReporter().flush();
    }


    /**
     * Get current thread test
     */
    public static synchronized ExtentTest getTest() {
        return test.get();
    }


    /**
     * Returns test name for screenshot naming
     */
    public static synchronized String getTestName() {
        ExtentTest currentTest = getTest();
        if (currentTest != null) {
            return currentTest.getModel().getName();
        }
        return "No_Test_Active";
    }


    /**
     * Log a simple info message
     */
    public synchronized static void logStep(String logMessage) {
        getTest().info(logMessage);
    }


    /**
     * Log step with screenshot attachment
     */
    public synchronized static void logStepWithScreenshot(WebDriver driver,
                                                          String logMessage,
                                                          String screenshotMessage)
            throws IOException {

        getTest().pass(logMessage);
        attachScreenShot(driver, screenshotMessage);
    }


    /**
     * Log failure with screenshot
     */
    public synchronized static void logFailure(WebDriver driver,
                                               String logMessage,
                                               String screenshotMessage)
            throws IOException {

        getTest().fail(logMessage);
        attachScreenShot(driver, screenshotMessage);
    }


    /**
     * Log skipped step
     */
    public synchronized static void logSkip(String logMessage) {
        getTest().skip(logMessage);
    }


    /**
     * Capture screenshot → save file → return Base64 string
     */
    public synchronized static String takeScreenShot(WebDriver driver, String screenShotName)
            throws IOException {

        TakesScreenshot ts = (TakesScreenshot) driver;
        File src = ts.getScreenshotAs(OutputType.FILE);

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

        String destinationPath = System.getProperty("user.dir")
                + "/src/main/resources/ScreenShots/"
                + screenShotName + "_" + timeStamp + ".png";

        File finalPath = new File(destinationPath);
        FileUtils.copyFile(src, finalPath);

        return convertToBase64(src);
    }


    /**
     * Convert screenshot file to Base64 string
     */
    public synchronized static String convertToBase64(File file) throws IOException {
        byte[] fileContent = FileUtils.readFileToByteArray(file);
        return Base64.getEncoder().encodeToString(fileContent);
    }


    /**
     * Attach base64 screenshot to report
     */
    public synchronized static void attachScreenShot(WebDriver driver, String message)
            throws IOException {

        String screenShotBase64 = takeScreenShot(driver, getTestName());
        getTest().info(
                message,
                com.aventstack.extentreports.MediaEntityBuilder
                        .createScreenCaptureFromBase64String(screenShotBase64)
                        .build()
        );
    }


    /**
     * Register driver for current thread
     */
    public synchronized static void registerDriver(WebDriver driver) {
        driverMap.put(Thread.currentThread().getId(), driver);
    }
}
