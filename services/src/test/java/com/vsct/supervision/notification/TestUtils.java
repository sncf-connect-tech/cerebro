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

package com.vsct.supervision.notification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;

import com.vsct.supervision.notification.model.AlarmMapper;
import com.vsct.supervision.notification.model.SubscriptionMapper;
import com.vsct.supervision.seyren.api.Alert;
import com.vsct.supervision.seyren.api.AlertType;
import com.vsct.supervision.seyren.api.Alarm;
import com.vsct.supervision.seyren.api.Subscription;
import com.vsct.supervision.seyren.api.SubscriptionType;

public class TestUtils {
    public static final String DEFAULT_ALARM_ID = "123456789";
    public static final String NONEXISTING_ALARM_ID = "000000000";
    public static final String DEFAULT_SUBSCRIPTION_ID = "azerytuoiuy";
    public static final String DEFAULT_ALARM_NAME = "Test Alarm";
    public static final String DEFAULT_EMAIL = "tests@supervision.com";

    public static Alarm getDefaultAlarm() {
        Alarm alarm = new Alarm();
        alarm.setName(DEFAULT_ALARM_NAME);
        alarm.setDescription("Un test de Alarm");
        alarm.setTarget("keepLastValue(perf.MPD.mpdsimulation.allRequests.ok.count)");
        alarm.setWarn(BigDecimal.valueOf(100));
        alarm.setError(BigDecimal.valueOf(200));
        alarm.setEnabled(false);
        alarm.setLive(false);
        alarm.setFrom(AlarmMapper.DEFAULT_FROM);
        alarm.setUntil(AlarmMapper.DEFAULT_UNTIL);
        alarm.setId(DEFAULT_ALARM_ID);
        alarm.setSubscriptions(Arrays.asList(getDefaultSubscription()));
        return alarm;
    }

    public static Subscription getDefaultSubscription() {
        Subscription subscription = new Subscription();
        subscription.setId(DEFAULT_SUBSCRIPTION_ID);
        subscription.setTarget(DEFAULT_EMAIL);
        subscription.setMo(true);
        subscription.setTu(true);
        subscription.setWe(true);
        subscription.setTh(true);
        subscription.setFr(true);
        subscription.setSa(false);
        subscription.setSu(false);
        subscription.setFromTime(SubscriptionMapper.DEFAULT_FROM_TIME);
        subscription.setToTime(SubscriptionMapper.DEFAULT_TO_TIME);
        subscription.setIgnoreOk(false);
        subscription.setIgnoreWarn(false);
        subscription.setIgnoreError(false);
        subscription.setEnabled(true);
        subscription.setType(SubscriptionType.EMAIL);
        return subscription;
    }

    public static Alert getDefaultAlert() {
        Alert alert = new Alert();
        alert.setId("word");
        alert.setAlarmId(DEFAULT_ALARM_ID);
        alert.setValue(new BigDecimal(12));
        alert.setTarget("hello.word");
        alert.setError(new BigDecimal(11));
        alert.setWarn(new BigDecimal(10));
        alert.setFromType(AlertType.ERROR);
        alert.setToType(AlertType.OK);
        alert.setTargetHash("targetHash");
        alert.setTimestamp(Instant.now());
        return alert;
    }

    public static Alert getDefaultAlertNoChanged() {
        Alert alert = new Alert();
        alert.setId("hello");
        alert.setAlarmId(DEFAULT_ALARM_ID);
        alert.setValue(new BigDecimal(10));
        alert.setTarget("hello.word");
        alert.setError(new BigDecimal(11));
        alert.setWarn(new BigDecimal(12));
        alert.setFromType(AlertType.UNKNOWN);
        alert.setToType(AlertType.UNKNOWN);
        alert.setTargetHash("targetHash");
        alert.setTimestamp(Instant.now());
        return alert;
    }

}
