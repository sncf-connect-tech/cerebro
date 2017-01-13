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

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestScenario {

    static TestNavigation navigation;

    private static final Logger LOGGER = LoggerFactory.getLogger(TestNavigation.class);
    public static boolean useKeycloak = false;

    public static void test(WebDriver driver, String baseurl){

        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.get(baseurl);

        navigation = new TestNavigation(driver);


        testLoginLogout(driver);

        testNavigation();

        testCreation(driver);

        testSubscription(driver);

    }

    public static void testNavigation(){
        navigation.tabs();
    }

    public static void testCreation(WebDriver driver){
        TestCreation createTest = new TestCreation(driver);
        createTest.createAlarm();
        createTest.testDetailPage();
        createTest.deleteAlarm();
    }

    public static void testLoginLogout(WebDriver driver){

        if(TestScenario.useKeycloak){
            TestLogin loginTest = new TestLogin(driver);
            loginTest.login();
            loginTest.logoutThenLogin();
        }
        else{
            LOGGER.info("testLoginLogout is disabled!");
        }
    }

    public static void testSubscription(WebDriver driver){
        TestSubscription subscriptionTest = new TestSubscription(driver);
        TestCreation createTest = new TestCreation(driver);

        navigation.clickOnAddAlarm();
        createTest.createAlarm();
        subscriptionTest.unsubscribe();
        subscriptionTest.subscribe();
        createTest.deleteAlarm();
    }
}