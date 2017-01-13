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

package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Utils {

    public final static int WINDOW_WIDTH = 1280;
    public final static int WINDOW_HEIGHT = 1024;
    public final static int DEFAULT_WAITING_TIME = 5;

    public static void clickWhenReady(WebDriver driver, By element){
        clickWhenReady(driver,driver.findElement(element));
    }

    public static void clickWhenReady(WebDriver driver, WebElement webElement){
        new WebDriverWait(driver,DEFAULT_WAITING_TIME).until(ExpectedConditions.visibilityOf(webElement));
        Actions actions = new Actions(driver);
        actions.moveToElement(webElement);
        new WebDriverWait(driver,DEFAULT_WAITING_TIME).until(ExpectedConditions.elementToBeClickable(webElement));
        webElement.click();
    }
}
