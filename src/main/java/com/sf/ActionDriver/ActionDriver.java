package com.sf.ActionDriver;

import com.sf.BaseClass.BaseClass;
import com.sf.Utilities.ExtentManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
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
            captureFailure("Element not clickable: " + by);
        }
    }

    public void waitForElementToBeVisible(By by) {
        try {
            logger.debug("Waiting for element to be visible: {}", by);
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (Exception e) {
            logger.error("Element is NOT visible: {} | Exception: {}", by, e.getMessage());
            captureFailure("Element not visible: " + by);
        }
    }

    // -------------------- ACTION METHODS --------------------

    public void click(By by) {
        try {
            String elementDescription = getElementDescription(by);
            logger.info("Clicking element: {}", elementDescription);

            waitForElementToBeClickable(by);
            driver.findElement(by).click();

            ExtentManager.logStep("Clicked element: " + elementDescription);
            logger.debug("Click action completed for: {}", by);

        } catch (Exception e) {
            logger.error("Unable to click element: {} | Exception: {}", by, e.getMessage());
            captureFailure("Failed to click element: " + by);
        }
    }

    public void enterText(By by, String text) {
        try {
            logger.info("Entering text '{}' into element: {}", text, by);

            waitForElementToBeVisible(by);

            WebElement element = driver.findElement(by);
            element.clear();
            element.sendKeys(text);

            ExtentManager.logStepWithScreenshot(driver,
                    "Entered text: " + text,
                    "After entering text");

            logger.debug("Text entered successfully into: {}", by);

        } catch (Exception e) {
            logger.error("Unable to enter text '{}' into element: {} | Exception: {}", text, by, e.getMessage());
            captureFailure("Failed to enter text in element: " + by);
        }
    }

    public String getText(By by) {
        try {
            logger.info("Fetching text from element: {}", by);
            waitForElementToBeVisible(by);
            String text = driver.findElement(by).getText();
            logger.debug("Extracted text: {}", text);
            return text;

        } catch (Exception e) {
            logger.error("Unable to get text from element: {} | Exception: {}", by, e.getMessage());
            captureFailure("Failed to get text from: " + by);
            return "";
        }
    }

    public boolean compareText(By by, String expectedText) {
        try {
            logger.info("Comparing text for element: {}", by);

            waitForElementToBeVisible(by);
            String actualText = driver.findElement(by).getText();

            if (expectedText.equals(actualText)) {
                ExtentManager.logStep("Text matched: " + actualText);
                logger.info("Text matches.");
                return true;
            } else {
                logger.warn("Text mismatch! Expected: '{}', Actual: '{}'", expectedText, actualText);
                captureFailure("Text mismatch for: " + by);
                return false;
            }

        } catch (Exception e) {
            logger.error("Unable to compare text for element {} | Exception: {}", by, e.getMessage());
            captureFailure("Failed to compare text for: " + by);
            return false;
        }
    }

    public boolean isDisplayed(By by) {
        try {
            logger.info("Checking visibility of element: {}", by);
            waitForElementToBeVisible(by);

            boolean displayed = driver.findElement(by).isDisplayed();
            if (displayed) {
                ExtentManager.logStep("Element is displayed: " + by);
            } else {
                captureFailure("Element is NOT displayed: " + by);
            }
            return displayed;

        } catch (Exception e) {
            logger.error("Display check failed for {} | Exception: {}", by, e.getMessage());
            captureFailure("Failed to verify display: " + by);
            return false;
        }
    }

    public void ScrollToElement(By by) {
        try {
            logger.info("Scrolling to element: {}", by);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebElement element = driver.findElement(by);

            js.executeScript("arguments[0].scrollIntoView(true);", element);

            ExtentManager.logStep("Scrolled to element: " + by);
            logger.debug("Scroll complete.");

        } catch (Exception e) {
            logger.error("Unable to scroll to element {} | Exception: {}", by, e.getMessage());
            captureFailure("Failed to scroll to: " + by);
        }
    }

    // -------------------- PAGE LOAD WAIT --------------------

    public void waitForPageLoad(int timeOutSec) {
        logger.info("Waiting for page to load (timeout {} seconds)", timeOutSec);

        try {
            wait.withTimeout(Duration.ofSeconds(timeOutSec)).until(
                    webDriver -> ((JavascriptExecutor) webDriver)
                            .executeScript("return document.readyState")
                            .equals("complete")
            );

            ExtentManager.logStep("Page loaded successfully");

        } catch (Exception e) {
            logger.error("Page load failed within {} seconds | Exception: {}", timeOutSec, e.getMessage());
            captureFailure("Page load timeout");
        }
    }

    // -------------------- DESCRIPTION METHOD --------------------

    public String getElementDescription(By locator) {
        try {
            WebElement element = driver.findElement(locator);

            String name = element.getDomAttribute("name");
            String id = element.getDomAttribute("id");
            String classname = element.getDomAttribute("class");
            String text = element.getText();
            String placeholder = element.getAttribute("placeholder");
            String value = element.getAttribute("value");

            if (isNotEmpty(name)) return "name=" + name;
            if (isNotEmpty(id)) return "id=" + id;
            if (isNotEmpty(classname)) return "class=" + classname;
            if (isNotEmpty(text)) return "text=" + truncates(text, 50);
            if (isNotEmpty(value)) return "value=" + value;
            if (isNotEmpty(placeholder)) return "placeholder=" + placeholder;

        } catch (Exception e) {
            logger.error("Failed to extract element description: {}", e.getMessage());
        }
        return "Unknown Element";
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }

    private String truncates(String text, int length) {
        return text.length() <= length ? text : text.substring(0, length) + "...";
    }

    // -------------------- COMMON FAILURE HANDLER --------------------

    private void captureFailure(String message) {
        try {
            ExtentManager.logStepWithScreenshot(driver, message, "Failure Screenshot");
        } catch (IOException e) {
            logger.error("Unable to capture screenshot for failure: {}", e.getMessage());
        }
    }
}
