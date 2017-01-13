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

package test.firefox;


import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;

import io.github.bonigarcia.wdm.FirefoxDriverManager;
import test.TestScenario;
import utils.Utils;

public class FirefoxTest {

    public static void firefox(String baseurl, boolean useKeycloak){
        WebDriver driver = null;
        try {
            ProfilesIni allProfiles = new ProfilesIni();
            FirefoxProfile myProfile = allProfiles.getProfile("default");
            myProfile.setAcceptUntrustedCertificates(true);
            myProfile.setAssumeUntrustedCertificateIssuer(false);
            FirefoxDriverManager.getInstance().setup();
            driver = new FirefoxDriver(myProfile);
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
