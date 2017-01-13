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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import conf.Conf;
import utils.Utils;

public class TestLogin {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestLogin.class);

    private WebDriver driver;

    public TestLogin(final WebDriver driver) {
        this.driver = driver;
    }

    public void login() {
        LOGGER.info("trying to log in");
        //Enter username
        WebElement usernameField = driver.findElement(By.id("username"));
        usernameField.sendKeys(Conf.USERNAME);
        LOGGER.info("enter username");

        //Enter password
        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys(Conf.PASSWORD);
        LOGGER.info("enter password");

        //Log in
        Utils.clickWhenReady(driver,By.id("kc-login"));
        LOGGER.info("logged in");
    }

    public void logoutThenLogin(){
        LOGGER.info("trying to log out");
        Utils.clickWhenReady(driver,By.id("logout"));
        LOGGER.info("logged out");
        login();
    }
}