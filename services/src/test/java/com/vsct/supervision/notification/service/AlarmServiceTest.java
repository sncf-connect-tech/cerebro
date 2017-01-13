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

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.vsct.supervision.notification.ErrorCode;
import com.vsct.supervision.notification.TestUtils;
import com.vsct.supervision.notification.email.NotificationHandler;
import com.vsct.supervision.notification.exception.CerebroException;
import com.vsct.supervision.notification.repository.SeyrenRepository;
import com.vsct.supervision.seyren.api.Alarm;
import com.vsct.supervision.seyren.api.Subscription;

@RunWith(MockitoJUnitRunner.class)
public class AlarmServiceTest {
    @InjectMocks
    private final AlarmService alarmService = new AlarmService();

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private SeyrenRepository seyrenRepository;

    @Mock
    private NotificationHandler notificationHandler;

    @Before
    public void setUp() throws Exception {
        Alarm alarm = TestUtils.getDefaultAlarm();

        when(seyrenRepository.getAlarm(TestUtils.DEFAULT_ALARM_ID)).thenReturn(alarm);

        when(seyrenRepository.getAlarm(TestUtils.NONEXISTING_ALARM_ID))
            .thenThrow(new CerebroException(ErrorCode.ALARM_UNKNOWN, "Exception for tests"));

        Alarm.Alarms alarms = new Alarm.Alarms();
        alarms.setValues(Arrays.asList(alarm));
        when(seyrenRepository.getAllAlarms()).thenReturn(alarms);
    }

    @Test
    public void getAlarm() throws Exception {
        Alarm alarm = alarmService.getAlarm(TestUtils.DEFAULT_ALARM_ID);

        assertNotNull(alarm);
        assertEquals(TestUtils.DEFAULT_ALARM_ID, alarm.getId());
        assertEquals(TestUtils.DEFAULT_ALARM_NAME, alarm.getName());
    }

    @Test
    public void getNonExistentAlarm() {
        Alarm alarm = null;
        try {
            alarm = alarmService.getAlarm(TestUtils.NONEXISTING_ALARM_ID);
            fail("Get a nonexistent alarm should throw an exception");
        } catch (CerebroException e) {
            assertNull(alarm);
            assertEquals(ErrorCode.ALARM_UNKNOWN, e.getErrorCode());
        }
    }

    @Test
    public void getAllAlarms() throws Exception {
        Collection<Alarm> allAlarms = alarmService.getAllAlarms();
        assertEquals(allAlarms.size(), 1);
    }

    @Test
    public void updateAlarm() throws Exception {
        Alarm alarm = TestUtils.getDefaultAlarm();
        alarm.setTarget("totoSayHello");

        Alarm updatedAlarm = alarmService.updateAlarm(alarm);

        assertNotNull(updatedAlarm);

        verify(seyrenRepository).updateAlarm(alarm);
    }

    @Test
    public void updateNonexistentAlarmFail() throws Exception {
        Alarm updatedAlarm = TestUtils.getDefaultAlarm();
        updatedAlarm.setId(TestUtils.NONEXISTING_ALARM_ID);

        try {
            alarmService.updateAlarm(updatedAlarm);
            fail("Update non existing alarm should throw an exception");
        } catch (CerebroException ce) {
            assertEquals(ErrorCode.ALARM_UNKNOWN, ce.getErrorCode());
        }
    }

    @Test
    public void updateAlarmWithMoreThanOneSubscription() throws Exception {
        Alarm alarm = TestUtils.getDefaultAlarm();
        Subscription subs = TestUtils.getDefaultSubscription();
        subs.setFromTime("fromTime");

        List<Subscription> newList = new ArrayList<>(alarm.getSubscriptions());
        newList.add(subs);
        alarm.setSubscriptions(newList);

        when(seyrenRepository.getAlarm(alarm.getId())).thenReturn(alarm);

        alarmService.updateAlarm(alarm);
    }

    @Test
    public void updateAlarmSetAlreadyExistingNameFail() throws Exception {
        Alarm alarm = TestUtils.getDefaultAlarm();
        alarm.setId("coucou");

        when(seyrenRepository.getAlarm(alarm.getId())).thenReturn(alarm);

        try {
            alarmService.updateAlarm(alarm);
            fail("Update a alarm with same name should throw an exception");
        } catch (CerebroException ce) {
            assertEquals(ErrorCode.ALARM_DUPLICATE_NAME, ce.getErrorCode());
        }
    }

    @Test
    public void updateAlarmWithSameSourceTargetAndThresholdsFail() throws Exception {
        Alarm alarm = TestUtils.getDefaultAlarm();
        Alarm otherAlarm = TestUtils.getDefaultAlarm();
        otherAlarm.setId("plop");
        otherAlarm.setName("coucou");

        Alarm.Alarms alarms = new Alarm.Alarms();
        alarms.setValues(asList(alarm, otherAlarm));

        reset(seyrenRepository);
        when(seyrenRepository.getAlarm(alarm.getId())).thenReturn(alarm);
        when(seyrenRepository.getAllAlarms()).thenReturn(alarms);

        Alarm updatedAlarm = TestUtils.getDefaultAlarm();
        try {
            // Try to insert a alarm with same "data"
            alarmService.updateAlarm(updatedAlarm);
            fail("Create a alarm with same data should throw an exception");
        } catch (CerebroException ce) {
            assertEquals(ErrorCode.ALARM_DUPLICATE_DATAS, ce.getErrorCode());
        }
    }

    @Test
    public void subscribeToANewAlarm() throws Exception {
        Alarm alarm = new Alarm();
        alarm.setTarget("zenith.key");
        alarm.setName("a test alarm");

        alarmService.subscribeToAAlarm(alarm);

        // 1. Search identical alarm (target, threshold, etc.)
        // 2. Search alarm by name
        // 3. Search created alarm
        verify(seyrenRepository, times(3)).getAllAlarms();
        verify(seyrenRepository).addAlarm(alarm);
    }

    @Test
    public void subscribeAlarmWithSameName() throws Exception {
        Alarm alarm = new Alarm();
        alarm.setName(TestUtils.DEFAULT_ALARM_NAME);

        try {
            alarmService.subscribeToAAlarm(alarm);
            fail("Adding a alarm with same name");
        } catch (CerebroException ce) {
            assertEquals(ErrorCode.ALARM_DUPLICATE_NAME, ce.getErrorCode());
        }
    }

    @Test
    public void subscribeToAnExistingAlarm() throws Exception {
        Alarm alarm = TestUtils.getDefaultAlarm();
        Subscription subToAdd = TestUtils.getDefaultSubscription();
        subToAdd.setId("subTestId");
        subToAdd.setTarget("test@anotherteam.org");
        subToAdd.setSu(true);

        alarm.setSubscriptions(Arrays.asList(subToAdd));

        alarmService.subscribeToAAlarm(alarm);

        verify(seyrenRepository, never()).addAlarm(any());
        verify(subscriptionService).addSubscription(subToAdd, TestUtils.DEFAULT_ALARM_ID);
    }

    @Test
    public void subscribeToAExistingAlarmAlreadyHaveSubscription() throws Exception {
        Alarm addedAlarm = TestUtils.getDefaultAlarm();
        Alarm returnedAlarm = alarmService.subscribeToAAlarm(addedAlarm);

        verify(seyrenRepository, never()).addAlarm(any());
        verify(seyrenRepository, never()).addSubscription(any(), any());

        assertNotNull(returnedAlarm);
        assertEquals(addedAlarm.getId(), returnedAlarm.getId());
    }

    @Test
    public void subscribeToAExistingAlarmAlreadyHaveSubscriptionTargetCase() throws Exception {
        Alarm addedAlarm = TestUtils.getDefaultAlarm();
        Subscription s1 = TestUtils.getDefaultSubscription();
        s1.setTarget(TestUtils.DEFAULT_EMAIL.toUpperCase());

        addedAlarm.setSubscriptions(Arrays.asList(s1));
        alarmService.subscribeToAAlarm(addedAlarm);
        verify(seyrenRepository, never()).addAlarm(any());
        verify(seyrenRepository, never()).addSubscription(s1, addedAlarm.getId());
    }

    @Test
    public void subscribeToAExistingAlarmActivateIt() throws Exception {
        // Remise a zero des mocks et initialisation de ceux si avec une alarme desactive
        Alarm alarm = TestUtils.getDefaultAlarm();
        alarm.setEnabled(false);
        reset(seyrenRepository);
        when(seyrenRepository.getAlarm(TestUtils.DEFAULT_ALARM_ID)).thenReturn(alarm);
        Alarm.Alarms alarms = new Alarm.Alarms();
        alarms.setValues(Arrays.asList(alarm));
        when(seyrenRepository.getAllAlarms()).thenReturn(alarms);

        // test
        alarm = alarmService.getAlarm(TestUtils.DEFAULT_ALARM_ID);
        assertFalse(alarm.isEnabled());

        // verification
        Alarm returnedAlarm = alarmService.subscribeToAAlarm(alarm);
        assertNotNull(returnedAlarm);
        assertTrue(returnedAlarm.isEnabled());
    }

    @Test
    public void searchAlarm() throws Exception {
        Alarm searchAlarm = new Alarm();
        searchAlarm.setTarget("keepLastValue(perf.MPD.mpdsimulation.allRequests.ok.count)");
        searchAlarm.setWarn(BigDecimal.valueOf(100));
        searchAlarm.setError(BigDecimal.valueOf(200));

        Alarm foundAlarm = alarmService.searchAlarm(searchAlarm);
        assertNotNull(foundAlarm);
        assertEquals(TestUtils.DEFAULT_ALARM_ID, foundAlarm.getId());
    }

    @Test
    public void searchNonExistentAlarm() throws Exception {
        Alarm searchAlarm = new Alarm();
        searchAlarm.setTarget("testing.is.good");
        searchAlarm.setWarn(BigDecimal.valueOf(155));
        searchAlarm.setError(BigDecimal.valueOf(551));

        Alarm foundAlarm = alarmService.searchAlarm(searchAlarm);
        assertNull(foundAlarm);
    }

    @Test
    public void searchAlarmsBySubscriptionTarget() throws Exception {
        Subscription notDefaultTargetSubscription = TestUtils.getDefaultSubscription();
        notDefaultTargetSubscription.setTarget("supercoucou");

        Alarm alarm1 = TestUtils.getDefaultAlarm();
        Alarm alarm2 = TestUtils.getDefaultAlarm();
        Alarm alarmWithoutDefaultTargetSubscription = TestUtils.getDefaultAlarm();
        alarmWithoutDefaultTargetSubscription.setSubscriptions(asList(notDefaultTargetSubscription));

        Alarm.Alarms alarms = new Alarm.Alarms();
        alarms.setValues(asList(alarm1, alarmWithoutDefaultTargetSubscription, alarm2));

        reset(seyrenRepository);
        when(seyrenRepository.getAllAlarms()).thenReturn(alarms);

        Collection<Alarm> resultAlarms = alarmService.searchAlarmsBySubscriptionTarget(TestUtils.DEFAULT_EMAIL);

        assertThat(resultAlarms).containsOnly(alarm1, alarm2);
    }

    @Test
    public void updateAlarmSendModificationEmail(){
        Alarm alarm = TestUtils.getDefaultAlarm();
        alarm.setEnabled(true);
        alarmService.updateAlarm(alarm);
        verify(notificationHandler).sendAlarmHasBeenModified(alarm);
    }

    @Test
    public void disableAlarmSendDeactivationEmail(){
        Alarm alarm = TestUtils.getDefaultAlarm();
        alarmService.updateAlarm(alarm);
        verify(notificationHandler).sendAlarmHasBeenDeactivated(alarm);
    }
}