package com.sf.DummyTests;

import com.sf.BaseClass.BaseClass;
import com.sf.Pages.HomePage;
import com.sf.Pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.Assertion;

public class HomePageTest extends BaseClass {

    private LoginPage loginPage;
    private HomePage homePage;

    @BeforeMethod
    public void setUpPages()
    {
        loginPage =new LoginPage(getDriver()) ;
        homePage =new HomePage(getDriver()) ;
    }

    @Test
    public  void VerifyOrangeHRMLogo()
    {
        loginPage.Login("admin","admin123");
        Assert.assertTrue(homePage.verifyOrangeHrmLogo(),"logo not visible");
    }
}
