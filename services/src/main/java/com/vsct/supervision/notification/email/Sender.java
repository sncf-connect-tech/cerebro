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

package com.vsct.supervision.notification.email;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Sender {
    abstract void send(String title, String text, List<String> recipients);

    private static final Logger LOGGER = LoggerFactory.getLogger(Sender.class);

    public Properties getProperties(){
        Properties props = new Properties();
        try {
            props.load(MailSenderImpl.class.getResourceAsStream("/config/email.properties"));
        } catch (IOException e) {
            LOGGER.error("Invalid mail properties file",e);
        }
        return props;
    }
}
