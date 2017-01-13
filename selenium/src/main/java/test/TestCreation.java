/*
 * This file is part of the Cerebro distribution.
 * (https://github.com/voyages-sncf-technologies/cerebro)
 * Copyright (C) 2017 VSCT.
 *
 * Cerebro is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, version 3 of the License.
 *
 * Cerebro is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.Utils;

public class TestCreation {
    public static final String NAME = "Selenium test";
    private static final String DESCRIPTION = "Selenium description";
    private static final String GRAPHITE_KEY = "a.selenium.test";
    private static final String WARN_THRESHOLD = "20";
    private static final String ERROR_THRESHOLD = "50";

    private static final Logger LOGGER = LoggerFactory.getLogger(TestCreation.class);

    WebDriver driver;
    TestNavigation navigation;

    public TestCreation(final WebDriver driver) {
        this.driver = driver;
        this.navigation = new TestNavigation(driver);
    }

    @Before
    public void createAlarm() {
        TestScenario.navigation.clickOnAddAlarm();
        //Enter alarm name
        WebElement name = driver.findElement(By.name("name"));
        name.sendKeys(NAME);
        LOGGER.info("Enter alarm name");

        //Enter alarm description
        WebElement description = driver.findElement(By.name("description"));
        description.sendKeys(DESCRIPTION);
        LOGGER.info("Enter alarm description");

        //enter alarm graphite key
        WebElement key = driver.findElement(By.name("graphite-key"));
        key.sendKeys(GRAPHITE_KEY);
        LOGGER.info("Enter alarm graphite key");

        //go to step 2
        Utils.clickWhenReady(driver, By.name("go-to-step-2"));
        LOGGER.info("go to step 2");

        //change windowMode
        Select windowModes = new Select(driver.findElement(By.id("windowMode")));
        List<WebElement> options = windowModes.getOptions();
        for(WebElement option: options){
            option.click();
            String value = option.getAttribute("value");
            if(value.equalsIgnoreCase("summarize")) {
                assertTrue(driver.findElement(By.id("windowAggregation")).isDisplayed());
                assertTrue(driver.findElement(By.id("timeUnitsNumber")).isDisplayed());
                assertTrue(driver.findElement(By.id("windowUnits")).isDisplayed());
            }
        }
        LOGGER.info("try all windowModes");


        //go to step 3
        Utils.clickWhenReady(driver, By.name("go-to-step-3"));
        LOGGER.info("go to step 3");

        //Enter warn Threshold
        WebElement warnThreshold = driver.findElement(By.name("warn-threshold"));
        warnThreshold.sendKeys(WARN_THRESHOLD);
        LOGGER.info("Enter warning threshold");

        //Enter error Threshold
        WebElement errorThreshold = driver.findElement(By.name("error-threshold"));
        errorThreshold.sendKeys(ERROR_THRESHOLD);
        LOGGER.info("Enter error threshold");

        //go to step 4
        Utils.clickWhenReady(driver, By.id("go-to-step-4"));
        LOGGER.info("Go to step 4");

        //go to step 5
        Utils.clickWhenReady(driver, By.id("go-to-step-5"));
        LOGGER.info("Go to step 5");

        //create alarm
        new WebDriverWait(driver,Utils.DEFAULT_WAITING_TIME).until(ExpectedConditions.visibilityOf(driver.findElement(By.id("confirm-alarm-creation"))));
        Utils.clickWhenReady(driver, By.id("confirm-alarm-creation"));

        //wait redirect
        new WebDriverWait(driver,Utils.DEFAULT_WAITING_TIME).until(ExpectedConditions.visibilityOfElementLocated(By.id("alarm-name-title")));
        LOGGER.info("alarm is created");
    }

    @Test
    public void testDetailPage(){
        WebElement pageTitle = driver.findElement(By.id("alarm-name-title"));
        assertEquals(pageTitle.getText(), NAME);
        LOGGER.info("page title is good: " + pageTitle.getText().equals(NAME));
        WebElement name = driver.findElement(By.id("alarm-name"));
        assertEquals(name.getText(), NAME);
        LOGGER.info("alarm name is good: " + name.getText().equals(NAME));

        WebElement description = driver.findElement(By.id("alarm-description"));
        assertEquals(description.getText(), DESCRIPTION);
        LOGGER.info("alarm description is good: " + description.getText().equals(DESCRIPTION));

        WebElement graphiteKey = driver.findElement(By.name("alarm-graphite-key"));
        assertEquals(graphiteKey.getText(), GRAPHITE_KEY);
        LOGGER.info("alarm graphite key is good: " + graphiteKey.getText().equals(GRAPHITE_KEY));

        WebElement warnThreshold = driver.findElement(By.id("alarm-warn-threshold"));
        assertEquals(warnThreshold.getText(), WARN_THRESHOLD);
        LOGGER.info("alarm warning threshold: " + warnThreshold.getText().equals(WARN_THRESHOLD));

        WebElement errorThreshold = driver.findElement(By.id("alarm-error-threshold"));
        assertEquals(errorThreshold.getText(), ERROR_THRESHOLD);
        LOGGER.info("alarm error threshold: " + errorThreshold.getText().equals(ERROR_THRESHOLD));
    }

    @After
    public void deleteAlarm(){
        //click on delete
        Utils.clickWhenReady(driver, driver.findElements(By.name("remove-subscription")).get(0));
        LOGGER.info("open alarm deletion modal");
        //wait and click on delete
        new WebDriverWait(driver,Utils.DEFAULT_WAITING_TIME).until(ExpectedConditions.visibilityOfElementLocated(By.id("modal-delete")));
        Utils.clickWhenReady(driver, By.id("modal-delete-subscription-ok"));
        LOGGER.info("alarm is removed");
        //wait list of alarms
        new WebDriverWait(driver,Utils.DEFAULT_WAITING_TIME).until(ExpectedConditions.visibilityOfElementLocated(By.id("filterBarText")));
    }
}