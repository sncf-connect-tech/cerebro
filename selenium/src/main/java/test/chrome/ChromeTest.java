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

package test.chrome;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import test.TestScenario;
import utils.Utils;

public class ChromeTest {

    public static void chrome(String baseurl, boolean useKeycloak){

        WebDriver driver = null;
        try {
            DesiredCapabilities desiredCapabilities = DesiredCapabilities.chrome();
            desiredCapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
            desiredCapabilities.setCapability(CapabilityType.SUPPORTS_APPLICATION_CACHE, false);

            ChromeDriverManager.getInstance().setup();
            driver = new ChromeDriver(desiredCapabilities);
            driver.manage().window().setSize(new Dimension(Utils.WINDOW_WIDTH,Utils.WINDOW_HEIGHT));
            driver.manage().timeouts().implicitlyWait(Utils.DEFAULT_WAITING_TIME, TimeUnit.SECONDS);

            TestScenario.useKeycloak = useKeycloak;
            TestScenario.test(driver, baseurl);
        }
        finally {
            if(driver!=null) {
                driver.quit();
            }
        }
    }
}
