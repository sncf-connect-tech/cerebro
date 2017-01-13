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

package com.vsct.supervision.notification.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.vsct.supervision.notification.TestUtils;
import com.vsct.supervision.notification.model.Stat;
import com.vsct.supervision.notification.repository.SeyrenRepository;
import com.vsct.supervision.seyren.api.Alert;
import com.vsct.supervision.seyren.api.Alert.Alerts;

@RunWith(MockitoJUnitRunner.class)
public class AlertServiceTest {
    @InjectMocks
    private final AlertService service = new AlertService();

    @Mock
    private SeyrenRepository seyrenRepository;

    @Before
    public void setUp() throws Exception {
        final Alert.Alerts alerts = new Alerts();
        final List<Alert> listAlerts = new ArrayList<>();
        listAlerts.add(TestUtils.getDefaultAlert());
        listAlerts.add(TestUtils.getDefaultAlertNoChanged());

        alerts.setValues(listAlerts);

        when(seyrenRepository.getAlerts(0, 20)).thenReturn(alerts);
        when(seyrenRepository.getAlerts(50, 50)).thenReturn(alerts);
        when(seyrenRepository.getAlerts(anyInt(), anyInt())).thenReturn(alerts);
        when(seyrenRepository.getAlarmAlerts(TestUtils.DEFAULT_ALARM_ID, 0, 20)).thenReturn(alerts);
    }

    @Test
    public void getAllAlertsTest() throws Exception {
        final Alert.Alerts allAlerts = service.getAlerts();
        assertNotNull(allAlerts);
        assertFalse(allAlerts.getValues().isEmpty());
        assertEquals(allAlerts.getValues().size(), 2);
    }

    @Test
    public void getAllAlertsAlarmTest() throws Exception {
        final Alert.Alerts allAlerts = service.getAlarmAlerts(TestUtils.DEFAULT_ALARM_ID);
        assertNotNull(allAlerts);
        assertFalse(allAlerts.getValues().isEmpty());
        assertEquals(allAlerts.getValues().size(), 2);
    }

    @Test
    public void getStatNoTypeChangeTest() throws Exception {

        final Alert.Alerts alerts = new Alerts();
        final List<Alert> listAlerts = new ArrayList<>();

        Alert alert = TestUtils.getDefaultAlert();
        alert.setTimestamp(Instant.now().minus(30, ChronoUnit.DAYS));
        listAlerts.add(alert);

        alert = TestUtils.getDefaultAlert();
        alert.setFromType(alert.getToType());
        listAlerts.add(alert);

        alert = TestUtils.getDefaultAlertNoChanged();
        alert.setTimestamp(Instant.now().minus(30, ChronoUnit.DAYS));
        listAlerts.add(alert);

        alert = TestUtils.getDefaultAlertNoChanged();
        alert.setFromType(alert.getToType());
        listAlerts.add(alert);

        alerts.setValues(listAlerts);

        when(seyrenRepository.getAlerts(0, 50)).thenReturn(alerts);
        final List<Stat> statsM = service.getStatNoTypeChange("2m");
        final List<Stat> statsD = service.getStatNoTypeChange("2d");
        final List<Stat> statsH = service.getStatNoTypeChange("2h");
        assertNotNull(statsM);
        assertNotNull(statsD);
        assertNotNull(statsH);
        assertEquals(statsM.size(), 0);
        assertEquals(statsD.size(), 0);
        assertEquals(statsH.size(), 0);
    }
}
