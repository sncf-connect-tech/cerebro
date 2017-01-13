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

package com.vsct.supervision.notification.model;

import org.springframework.util.StringUtils;

import com.vsct.supervision.notification.ErrorCode;
import com.vsct.supervision.notification.exception.CerebroException;
import com.vsct.supervision.seyren.api.Alarm;

public class AlarmValidator {


    public void validateAlarm(final Alarm alarm) throws CerebroException {
        // Validate required fields
        if (StringUtils.isEmpty(alarm.getName())) {
            throw new CerebroException(ErrorCode.ALARM_INVALID, "Alarm name is required.");
        }

        if (StringUtils.isEmpty(alarm.getTarget()) || StringUtils.startsWithIgnoreCase(alarm.getTarget(), "*")) {
            throw new CerebroException(ErrorCode.ALARM_INVALID, "Alarm target is required.");
        }

        if (StringUtils.isEmpty(alarm.getWarn())) {
            throw new CerebroException(ErrorCode.ALARM_INVALID, "Alarm warning threshold is required.");
        }

        if (StringUtils.isEmpty(alarm.getError())) {
            throw new CerebroException(ErrorCode.ALARM_INVALID, "Alarm error threshold is required.");
        }
    }


}
