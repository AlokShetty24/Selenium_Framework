package com.sf.ActionDriver;

import com.sf.BaseClass.BaseClass;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ActionDriver {

    private final WebDriver driver;
    private final WebDriverWait wait;
    public static final Logger logger = BaseClass.logger;

    public ActionDriver(WebDriver driver) {
        this.driver = driver;
        int explicitWait = Integer.parseInt(BaseClass.getProp().getProperty("explicitWait"));
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));
        logger.info("ActionDriver initialized with explicit wait: {} seconds", explicitWait);
    }

    // -------------------- WAIT METHODS --------------------

    public void waitForElementToBeClickable(By by) {
        try {
            logger.debug("Waiting for element to be clickable: {}", by);
            wait.until(ExpectedConditions.elementToBeClickable(by));
        } catch (Exception e) {
            logger.error("Element is NOT clickable: {} | Exception: {}", by, e.getMessage());
        }
    }

    public void waitForElementToBeVisible(By by) {
        try {
            logger.debug("Waiting for element to be visible: {}", by);
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (Exception e) {
            logger.error("Element is NOT visible: {} | Exception: {}", by, e.getMessage());
        }
    }

    // -------------------- ACTION METHODS --------------------

    public void click(By by) {
        try {
            String elementDescription = getElementDescription(by);
            logger.info("Clicking element: {}", elementDescription);
            waitForElementToBeClickable(by);
            driver.findElement(by).click();
            logger.debug("Click action completed for: {}", by);
        } catch (Exception e) {
            logger.error("Unable to click element: {} | Exception: {}", by, e.getMessage());
        }
    }

    public void enterText(By by, String text) {
        try {
            logger.info("Entering text '{}' into element: {}", text, by);
            waitForElementToBeVisible(by);
            WebElement element = driver.findElement(by);
            element.clear();
            element.sendKeys(text);
            logger.debug("Text entered successfully into: {}", by);
        } catch (Exception e) {
            logger.error("Unable to enter text '{}' into element: {} | Exception: {}", text, by, e.getMessage());
        }
    }

    public String getText(By by) {
        try {
            logger.info("Fetching text from element: {}", by);
            waitForElementToBeVisible(by);
            String text = driver.findElement(by).getText();
            logger.debug("Extracted text from {}: {}", by, text);
            return text;
        } catch (Exception e) {
            logger.error("Unable to get text from element: {} | Exception: {}", by, e.getMessage());
            return "";
        }
    }

    public boolean compareText(By by, String expectedText) {
        try {
            logger.info("Comparing text for element: {}", by);
            waitForElementToBeVisible(by);

            String actualText = driver.findElement(by).getText();

            if (expectedText.equals(actualText)) {
                logger.info("Expected text matches actual text: '{}'", actualText);
                return true;
            } else {
                logger.warn("Text mismatch! Expected: '{}', Actual: '{}'", expectedText, actualText);
                return false;
            }
        } catch (Exception e) {
            logger.error("Unable to compare text for element {} | Exception: {}", by, e.getMessage());
            return false;
        }
    }

    public boolean isDisplayed(By by) {
        try {
            logger.info("Checking visibility of element: {}", by);
            waitForElementToBeVisible(by);

            boolean displayed = driver.findElement(by).isDisplayed();

            if (displayed) {
                logger.info("Element is displayed: {}", by);
            } else {
                logger.warn("Element is NOT displayed: {}", by);
            }

            return displayed;

        } catch (Exception e) {
            logger.error("Unable to verify display status for element {} | Exception: {}", by, e.getMessage());
            return false;
        }
    }

    public void ScrollToElement(By by) {
        try {
            logger.info("Scrolling to element: {}", by);

            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebElement element = driver.findElement(by);

            js.executeScript("arguments[0].scrollIntoView(true);", element);

            logger.debug("Scroll to element completed: {}", by);
        } catch (Exception e) {
            logger.error("Unable to scroll to element {} | Exception: {}", by, e.getMessage());
        }
    }

    // -------------------- PAGE LOAD WAIT --------------------

    public void waitForPageLoad(int timeOutSec) {
        logger.info("Waiting for page to load completely (timeout: {} seconds)", timeOutSec);

        try {
            wait.withTimeout(Duration.ofSeconds(timeOutSec)).until(
                    webDriver -> ((JavascriptExecutor) webDriver)
                            .executeScript("return document.readyState")
                            .equals("complete")
            );
            logger.info("Page load completed successfully.");
        } catch (Exception e) {
            logger.error("Page did not load successfully within {} seconds | Exception: {}", timeOutSec, e.getMessage());
        }
    }

// Method to get element Description

    public String getElementDescription(By locator) {
        if(driver==null) {
            logger.error("Driver is null");
            return "Driver is null";
        }
        if(locator==null) {
            logger.error("Locator is null");
            return "Locator is null";
        }
        WebElement element = driver.findElement(locator);
        String name=element.getDomAttribute("name");
        String id=element.getDomAttribute("id");
        String classname=element.getDomAttribute("class");
        String text=element.getText();
        String placeholder=element.getAttribute("placeholder");
        String value=element.getAttribute("value");

        if(isNotEmpty(name)) {
            return "Element name: " + name;
        }
        else if(isNotEmpty(id)) {
            return "Element id: " + id;
        }
        else if(isNotEmpty(classname)) {
            return "Element classname: " + classname;
        }
        else if(isNotEmpty(text)) {
            return "Element text: " + truncates(text,50);
        }
        else if(isNotEmpty(value)) {
            return "Element value: " + value;
        }
        else if(isNotEmpty(placeholder)) {
            return "Element placeholder: " + placeholder;
        }
        return null;
    }

    private boolean isNotEmpty(String value) {
        return value!=null && !value.isEmpty();
        }

    private String truncates(String text, int length) {
        if (text == null || text.length() <= length) {
            return text;
        }
        return text.substring(0, length)+"...";
    }
}
