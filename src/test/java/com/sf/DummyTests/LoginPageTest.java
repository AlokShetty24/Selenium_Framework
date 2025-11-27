package com.sf.DummyTests;

import com.sf.BaseClass.BaseClass;
import com.sf.Pages.HomePage;
import com.sf.Pages.LoginPage;
import com.sf.Utilities.ExtentManager;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginPageTest extends BaseClass {

    private LoginPage loginPage;
    private HomePage homePage;

    @BeforeMethod
    public void setUpPages() {
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
    }

    @Test
    public void loginTest() {
        ExtentManager.logStep("Starting Valid Login Test");

        loginPage.Login("admin", "admin123");
        ExtentManager.logStep("Entered valid username & password");

        boolean isAdminVisible = homePage.isAdminTabVisible();
        ExtentManager.logStep("Checking if Admin Tab is visible");

        Assert.assertTrue(isAdminVisible, "Admin Tab is not visible");
        ExtentManager.logStep("Admin tab is visible → Login successful");

        homePage.logout();
        ExtentManager.logStep("Logged out successfully");

        staticWait(2);
    }

    @Test
    public void invalidLogin() {
        ExtentManager.logStep("Starting Invalid Login Test");

        loginPage.Login("admin", "admin13");
        ExtentManager.logStep("Entered invalid password");

        String expectedErrorMessage = "Invalid credentials";

        boolean isErrorShown = loginPage.verifyErrorMessage(expectedErrorMessage);
        ExtentManager.logStep("Verifying error message");

        Assert.assertTrue(isErrorShown, "Test Failed: Error message mismatch");
        ExtentManager.logStep("Error message displayed correctly");
    }
}
