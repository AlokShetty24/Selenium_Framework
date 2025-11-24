package com.sf.ActionDriver;

import com.sf.BaseClass.BaseClass;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.security.PublicKey;
import java.time.Duration;

public class ActionDriver {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public ActionDriver(WebDriver driver) {
        this.driver = driver;
        int explictWait= Integer.parseInt(BaseClass.getProp().getProperty("explicitWait"));
        this.wait = new WebDriverWait(driver,Duration.ofSeconds( explictWait));
    }

    public void waitForElementToBeClickable(By by) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(by));
        } catch (Exception e) {
            System.out.println("Element is NOT clickable" + e.getMessage());
        }
    }

    public void waitForElementToBeVisible(By by) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (Exception e) {
            System.out.println("Element is NOT Visisible" + e.getMessage());
        }
    }

    public void click(By by) {
        try {
            waitForElementToBeClickable(by);
            driver.findElement(by).click();
        } catch (Exception e) {
            System.out.println("Unable to click element" + e.getMessage());
        }
    }

    public void enterText(By by, String text) {
        try {
            waitForElementToBeVisible(by);
            driver.findElement(by).clear();
            driver.findElement(by).sendKeys(text);
        } catch (Exception e) {
            System.out.println("Unable to enter text" + e.getMessage());
        }
    }

    public String getText(By by) {
        try {
            waitForElementToBeVisible(by);
            return driver.findElement(by).getText();
        } catch (Exception e) {
            System.out.println("Unable to get text" + e.getMessage());
            return "";
        }
    }

    public boolean compareText(By by, String text) {
        try {
            waitForElementToBeVisible(by);
            driver.findElement(by).clear();
            String actualText = driver.findElement(by).getText();
            if (text.equals(actualText)) {
                System.out.println("The text is equal to the actual text" + actualText);
                return true;
            } else {
                System.out.println("The text is not equal to the actual text" + actualText);
                return false;
            }
        } catch (Exception e) {
            System.out.println("Unable to compare text" + e.getMessage());
            return false;
        }
    }

    public boolean isDisplayed(By by) {
        try {
            waitForElementToBeVisible(by);
            boolean isDisplayed = driver.findElement(by).isDisplayed();
            if (isDisplayed) {
                System.out.println("The element is Displayed" + by);
                return isDisplayed;
            }
            else  {
                System.out.println("The element is not Displayed" + by);
                return false;
            }
        } catch (Exception e) {
            System.out.println("Unable to display element" + e.getMessage());
            return false;
        }
    }

    public void ScrollToElement(By by) {
        try {
            JavascriptExecutor js=(JavascriptExecutor)driver;
            WebElement element=driver.findElement(by);
            js.executeScript("arguments[0].scrollIntoView(true);", element);

        }
        catch (Exception e) {
            System.out.println("Unable to scroll element" + e.getMessage());
        }
    }

    public void waitForPageLoad(int timeOutSec)
    {
        try {
            wait.withTimeout(Duration.ofSeconds(timeOutSec)).until(WebDriver->((JavascriptExecutor)WebDriver).
                    executeScript("return document.readystate").equals("complete"));
            System.out.println("Page Load Complete");
        } catch (Exception e) {
            System.out.println("Unable to load page" + e.getMessage());
        }
    }

}

