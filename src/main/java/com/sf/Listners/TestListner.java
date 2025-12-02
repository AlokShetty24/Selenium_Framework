package com.sf.Listners;

import com.sf.BaseClass.BaseClass;
import com.sf.Utilities.ExtentManager;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListner implements ITestListener {

    // ================================
    //  Invoked Before Everything
    // ================================
    @Override
    public void onStart(ITestContext context) {
        // Initialize Extent Report (only once)
        System.out.println("==== Test Suite Started ====");
        ExtentManager.getReporter();
    }

    // ================================
    //  Invoked After Everything
    // ================================
    @Override
    public void onFinish(ITestContext context) {
        // Final flush for report
        System.out.println("==== Test Suite Finished ====");
        ExtentManager.getReporter().flush();
    }

    // ================================
    //  Invoked When Test Starts
    // ================================
    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();

        // Create a new test node
        ExtentManager.startTest(testName);

        // Log
        ExtentManager.logStep("🔵 Test Started: " + testName);

        System.out.println("Test Started → " + testName);
    }

    // ================================
    //  On PASS
    // ================================
    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();

        try {
            ExtentManager.logPass(
                    BaseClass.getDriver(),
                    "🟢 Test Passed Successfully",
                    testName
            );
        } catch (Exception ignored) {
        }

        ExtentManager.unload();   // Clean thread local

        System.out.println("Test Passed → " + testName);
    }

    // ================================
    //  On FAIL
    // ================================
    @Override
    public void onTestFailure(ITestResult result) {

        String testName = result.getMethod().getMethodName();
        String error = String.valueOf(result.getThrowable());

        try {
            ExtentManager.logFailure(
                    BaseClass.getDriver(),
                    "🔴 Test Failed: " + error,
                    testName
            );
        } catch (Exception ignored) {
        }

        ExtentManager.unload();  // Clean thread local

        System.out.println("Test Failed → " + testName);
        System.out.println("Reason → " + error);
    }

    // ================================
    //  On SKIP
    // ================================
    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();

        ExtentManager.logStep("🟡 Test Skipped: " + testName);

        ExtentManager.unload();  // Clean thread local

        System.out.println("Test Skipped → " + testName);
    }
}
