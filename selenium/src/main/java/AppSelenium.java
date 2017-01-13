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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.chrome.ChromeTest;
import test.firefox.FirefoxTest;

public class AppSelenium {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppSelenium.class);

    private static final String BASE_URL = "http://localhost:51010";

    public static void main(String[] args) {
        AppSelenium app = new AppSelenium();
        app.testOnChrome();
        app.testOnFireFox();
    }

    @Test
    public void testOnChrome() {
        LOGGER.info("Starting tests on Chrome...");
        ChromeTest.chrome(BASE_URL, false);
        LOGGER.info("Chrome tests have finished.");
    }

    @Test
    public void testOnFireFox() {
        LOGGER.info("Starting tests on Firefox...");
        FirefoxTest.firefox(BASE_URL, false);
        LOGGER.info("Firefox tests have finished.");
    }
}