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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.Utils;

public class TestNavigation {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestNavigation.class);

    private WebDriver driver;

    public TestNavigation(final WebDriver driver) {
        this.driver = driver;
    }

    public void tabs(){
        clickOnMyAlarms();
        clickOnAddAlarm();
        clickOnAllAlarms();
    }

    public void clickOnAllAlarms(){
        Utils.clickWhenReady(driver,By.id("all-alarms-tab"));
        LOGGER.info("on 'all alarms' tab");
    }

    public void clickOnAddAlarm(){
        Utils.clickWhenReady(driver,By.id("add-alarm-tab"));
        LOGGER.info("on 'add alarm' tab");
    }

    public void clickOnMyAlarms(){

        if(TestScenario.useKeycloak) {
            Utils.clickWhenReady(driver,By.id("my-alarms-tab"));
            LOGGER.info("on 'my alarms' tab");
        }
        else {
            LOGGER.info("test my alarms is disabled!");
            searchAlarm();
        }

    }

    public void searchAlarm(){
        TestScenario.navigation.clickOnAllAlarms();
        WebElement searchBar = driver.findElement(By.id("filterBarText"));
        new WebDriverWait(driver,Utils.DEFAULT_WAITING_TIME).until(ExpectedConditions.visibilityOf(searchBar));
        Utils.clickWhenReady(driver,By.id("showDisableCheck"));
        LOGGER.info("show disabled alarms");
        Utils.clickWhenReady(driver,searchBar);
        searchBar.sendKeys(TestCreation.NAME);
        LOGGER.info("Search for '" + TestCreation.NAME + "'");
    }
}
