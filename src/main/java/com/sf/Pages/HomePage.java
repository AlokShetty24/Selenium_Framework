package com.sf.Pages;

import com.sf.ActionDriver.ActionDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage {
    private ActionDriver actionDriver;

    private By adminTab=By.xpath("//span[text()='Admin']");
    private By userIDButton=By.className("oxd-userdropdown-name");
    private By logoutButton=By.xpath("//a[text()='Logout']");
    private By OrangeHrmLogo=By.xpath("//div[@class='oxd-brand-banner']//img");

    public HomePage(WebDriver driver) {
        this.actionDriver = new ActionDriver(driver);
    }

    public boolean isAdminTabVisible(){
        return actionDriver.isDisplayed(adminTab);
    }

    public boolean verifyOrangeHrmLogo(){
        return actionDriver.isDisplayed(OrangeHrmLogo);
    }

    public void logout(){
        actionDriver.click(userIDButton);
        actionDriver.click(logoutButton);
    }
}
