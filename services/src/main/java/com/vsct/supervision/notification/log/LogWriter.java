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

package com.vsct.supervision.notification.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.boot.logging.LogLevel;

public class LogWriter {

    public static void write(Class clazz, LogLevel logLevel, String message) {
        Logger logger = LoggerFactory.getLogger(clazz);

        switch (logLevel) {
        case TRACE:
            logger.trace(message);
            break;
        case DEBUG:
            logger.debug(message);
            break;
        case INFO:
            logger.info(message);
            break;
        case WARN:
            logger.warn(message);
            break;
        case ERROR:
            logger.error(message);
            break;
        case FATAL:
            Marker marker = MarkerFactory.getMarker("FATAL");
            logger.error(marker, message);
            break;
        default:
            logger.warn("No suitable log level found");
            break;
        }
    }
}
