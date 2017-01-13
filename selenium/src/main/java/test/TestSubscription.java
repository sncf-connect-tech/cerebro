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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.Utils;

public class TestSubscription {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestSubscription.class);

    WebDriver driver;

    public TestSubscription(WebDriver driver){
        this.driver = driver;
    }

    public void unsubscribe(){
        TestScenario.navigation.clickOnMyAlarms();

        Utils.clickWhenReady(driver, driver.findElements(By.name("alarm-row")).get(0));
        LOGGER.info("Click on available alarm");
        if(TestScenario.useKeycloak) {
            Utils.clickWhenReady(driver, By.id("dashboard-details-alarm"));
        }
        else{
            Utils.clickWhenReady(driver, By.id("alarms-details-alarm"));
        }
        LOGGER.info("Open alarm details");
        new WebDriverWait(driver,Utils.DEFAULT_WAITING_TIME).until(ExpectedConditions.visibilityOf(driver.findElement(By.id("alarm-name-title"))));
        Utils.clickWhenReady(driver, driver.findElements(By.name("disable-subscription")).get(0));
        LOGGER.info("disable alarm subscription");
        new WebDriverWait(driver,Utils.DEFAULT_WAITING_TIME).until(ExpectedConditions.visibilityOf(driver.findElements(By.name("enabled-subscription")).get(0)));
    }

    public void subscribe(){
        TestScenario.navigation.searchAlarm();
        new WebDriverWait(driver,Utils.DEFAULT_WAITING_TIME).until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//div[@name='alarm-row' and text()[contains(.,'Selenium test')]]"))));
        Utils.clickWhenReady(driver, driver.findElements(By.name("alarm-row")).get(0));
        LOGGER.info("click on alarm");
        Utils.clickWhenReady(driver, By.id("alarms-details-alarm"));
        LOGGER.info("Open alarm details");
        Utils.clickWhenReady(driver, driver.findElements(By.name("enabled-subscription")).get(0));
        TestScenario.navigation.clickOnMyAlarms();
        LOGGER.info("go on 'my alarms' tab");
        Utils.clickWhenReady(driver, driver.findElements(By.name("alarm-row")).get(0));
        if(TestScenario.useKeycloak) {
            Utils.clickWhenReady(driver, By.id("dashboard-details-alarm"));
        }
        else{
            Utils.clickWhenReady(driver, By.id("alarms-details-alarm"));
        }
    }
}