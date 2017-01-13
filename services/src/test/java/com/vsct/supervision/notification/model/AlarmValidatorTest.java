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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.vsct.supervision.notification.ErrorCode;
import com.vsct.supervision.notification.TestUtils;
import com.vsct.supervision.notification.exception.CerebroException;
import com.vsct.supervision.seyren.api.Alarm;

@RunWith(MockitoJUnitRunner.class)
public class AlarmValidatorTest {

    private AlarmValidator validator = new AlarmValidator();

    @Test
    public void validateAlarmWithNoNameFails() throws Exception {
        Alarm alarm = TestUtils.getDefaultAlarm();
        alarm.setName(null);

        try {
            validator.validateAlarm(alarm);
        } catch (CerebroException ce) {
            assertEquals(ErrorCode.ALARM_INVALID, ce.getErrorCode());
        }
    }

    @Test
    public void validateAlarmWithEmptyFails() throws Exception {
        Alarm alarm = TestUtils.getDefaultAlarm();
        alarm.setName("");
        try {
            validator.validateAlarm(alarm);
        } catch (CerebroException ce) {
            assertEquals(ErrorCode.ALARM_INVALID, ce.getErrorCode());
        }
    }

    @Test
    public void validateAlarmWithNoTargetFails() throws Exception {
        Alarm alarm = TestUtils.getDefaultAlarm();
        alarm.setTarget(null);
        try {
            validator.validateAlarm(alarm);
        } catch (CerebroException ce) {
            assertEquals(ErrorCode.ALARM_INVALID, ce.getErrorCode());
        }
    }

    @Test
    public void validateAlarmWithEmptyTargetFails() throws Exception {
        Alarm alarm = TestUtils.getDefaultAlarm();
        alarm.setTarget("");
        try {
            validator.validateAlarm(alarm);
        } catch (CerebroException ce) {
            assertEquals(ErrorCode.ALARM_INVALID, ce.getErrorCode());
        }
    }

    @Test
    public void validateAlarmWithNoWarnThresholdFails() throws Exception {
        Alarm alarm = TestUtils.getDefaultAlarm();
        alarm.setWarn(null);
        try {
            validator.validateAlarm(alarm);
        } catch (CerebroException ce) {
            assertEquals(ErrorCode.ALARM_INVALID, ce.getErrorCode());
        }
    }

    @Test
    public void validateAlarmWithNoErrorThresholdFails() throws Exception {
        Alarm alarm = TestUtils.getDefaultAlarm();
        alarm.setError(null);
        try {
            validator.validateAlarm(alarm);
        } catch (CerebroException ce) {
            assertEquals(ErrorCode.ALARM_INVALID, ce.getErrorCode());
        }
    }

     @Test
    public void validateAlarmWithOkPlateformWildcard() throws Exception {
        Alarm alarm = TestUtils.getDefaultAlarm();
        alarm.setTarget("Zenith.VAS.VSA.VSA.LIL.PRD*.WAS.*.*.any.*.any.io.sla.any.vol.any.10min.count");

        validator.validateAlarm(alarm);

    }
}
