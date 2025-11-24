package com.sf.DummyTests;

import com.sf.BaseClass.BaseClass;
import com.sf.Pages.HomePage;
import com.sf.Pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginPageTest extends BaseClass {

    private LoginPage loginPage;
    private HomePage homePage;

    @BeforeMethod
    public void setUpPages()
    {
        loginPage =new LoginPage(getDriver()) ;
        homePage =new HomePage(getDriver()) ;
    }

    @Test
    public  void loginTest()
    {
        loginPage.Login("admin","admin123");
        Assert.assertTrue(homePage.isAdminTabVisible(),"Admin Tab is not visible");
        homePage.logout();
        staticWait(2);
    }

    @Test
    public  void invalidLogin()
    {
        loginPage.Login("admin","admin13");
        String expectedErrorMessage="Invalid credentials";
        Assert.assertTrue(loginPage.verifyErrorMessage(expectedErrorMessage),"Test Failed");
    }
}
