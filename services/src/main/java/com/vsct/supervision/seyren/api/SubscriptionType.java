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

package com.vsct.supervision.seyren.api;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SubscriptionType {

    EMAIL,
    PAGERDUTY,
    HIPCHAT,
    HUBOT,
    FLOWDOCK,
    HTTP,
    IRCCAT,
    PUSHOVER,
    LOGGER,
    SNMP,
    SLACK,
    TWILIO,
    VICTOROPS,
    OPSGENIE,
    SHELL,
    UNKNOW;

    @JsonCreator
    public static SubscriptionType forValue(String value) {
        for (SubscriptionType type : SubscriptionType.values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        return SubscriptionType.UNKNOW;
    }
}
