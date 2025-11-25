package com.sf.Pages;

import com.sf.ActionDriver.ActionDriver;
import com.sf.BaseClass.BaseClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {

    private ActionDriver actionDriver;

    public LoginPage(WebDriver driver) {
        this.actionDriver = BaseClass.getActionDriver();
    }
    private By userNameFeild= By.name("username");
    private By passwordFeild=By.cssSelector("input[type='password']");
    private By loginButton= By.xpath("//button[text()=' Login ']");
    private By errorMessage= By.xpath("//p[text()='Invalid credentials']");

    public void Login(String username,String password) {
        actionDriver.enterText(userNameFeild,username);
        actionDriver.enterText(passwordFeild,password);
        actionDriver.click(loginButton);
    }

    public boolean isErrorMessageDisplayed() {
        return actionDriver.isDisplayed(errorMessage);
    }

    public String getErrorMessage() {
        return actionDriver.getText(errorMessage);
    }

    public boolean verifyErrorMessage(String errorMessage) {
        return actionDriver.compareText(this.errorMessage,errorMessage);
    }

}
