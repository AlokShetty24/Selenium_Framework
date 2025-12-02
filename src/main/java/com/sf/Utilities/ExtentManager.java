package com.sf.Utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class ExtentManager {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();


    // ------------------------------------------------------
    // INIT EXTENT REPORT
    // ------------------------------------------------------
    public synchronized static ExtentReports getReporter() {

        if (extent == null) {

            String reportPath = System.getProperty("user.dir")
                    + "/ExtentReport/ExtentReport.html";

            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
            spark.config().setDocumentTitle("Automation Test Report");
            spark.config().setReportName("Execution Report");
            spark.config().setTheme(Theme.DARK);

            extent = new ExtentReports();
            extent.attachReporter(spark);

            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        }

        return extent;
    }


    // ------------------------------------------------------
    // START TEST NODE
    // ------------------------------------------------------
    public synchronized static ExtentTest startTest(String testName) {
        ExtentTest extentTest = getReporter().createTest(testName);
        test.set(extentTest);
        return extentTest;
    }


    // ------------------------------------------------------
    // UNLOAD THREADLOCAL
    // ------------------------------------------------------
    public synchronized static void unload() {
        test.remove();
    }


    // ------------------------------------------------------
    // GET CURRENT TEST
    // ------------------------------------------------------
    public synchronized static ExtentTest getTest() {
        return test.get();
    }

    private static boolean isReady() {
        return test.get() != null;
    }


    // ------------------------------------------------------
    // BASIC LOGGING
    // ------------------------------------------------------
    public synchronized static void logStep(String message) {
        if (isReady()) test.get().info(message);
    }


    // ------------------------------------------------------
    // PASS LOG WITH SCREENSHOT
    // ------------------------------------------------------
    public synchronized static void logPass(WebDriver driver, String message, String stepName) throws IOException {

        if (!isReady()) return;

        String base64 = captureBase64(driver);

        test.get().pass(
                message,
                MediaEntityBuilder.createScreenCaptureFromBase64String(base64, stepName).build()
        );
    }


    // ------------------------------------------------------
    // FAILURE LOG WITH SCREENSHOT
    // ------------------------------------------------------
    public synchronized static void logFailure(WebDriver driver, String message, String stepName) throws IOException {

        if (!isReady()) return;

        String base64 = captureBase64(driver);

        test.get().fail(
                message,
                MediaEntityBuilder.createScreenCaptureFromBase64String(base64, stepName).build()
        );
    }


    // ------------------------------------------------------
    // INFO + SCREENSHOT
    // ------------------------------------------------------
    public synchronized static void logStepWithScreenshot(WebDriver driver,
                                                          String log,
                                                          String screenshotText) throws IOException {

        if (!isReady()) return;

        test.get().info(log);
        attachScreenshot(driver, screenshotText);
    }


    // ------------------------------------------------------
    // ATTACH ONLY SCREENSHOT
    // ------------------------------------------------------
    public synchronized static void attachScreenshot(WebDriver driver, String message) throws IOException {

        if (!isReady()) return;

        String base64 = captureBase64(driver);

        test.get().info(
                message,
                MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build()
        );
    }


    // ------------------------------------------------------
    // SCREENSHOT BASE64
    // ------------------------------------------------------
    public synchronized static String captureBase64(WebDriver driver) throws IOException {

        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        byte[] fileContent = FileUtils.readFileToByteArray(src);

        return Base64.getEncoder().encodeToString(fileContent);
    }
}
