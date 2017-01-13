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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.vsct.supervision.notification.ErrorCode;
import com.vsct.supervision.notification.TestUtils;
import com.vsct.supervision.notification.exception.CerebroException;
import com.vsct.supervision.notification.exception.DuplicateSubscriptionException;
import com.vsct.supervision.notification.repository.SeyrenRepository;
import com.vsct.supervision.seyren.api.Alarm;
import com.vsct.supervision.seyren.api.Subscription;
import com.vsct.supervision.seyren.api.SubscriptionType;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionServiceTest {
    @InjectMocks
    private final SubscriptionService subscriptionService = new SubscriptionService();

    @Mock
    private SeyrenRepository seyrenRepository;

    @Before
    public void setUp() throws Exception {
        Alarm alarm = TestUtils.getDefaultAlarm();
        alarm.setEnabled(true);

        when(seyrenRepository.getAlarm(TestUtils.DEFAULT_ALARM_ID)).thenReturn(alarm);
        when(seyrenRepository.getAlarm(TestUtils.NONEXISTING_ALARM_ID))
            .thenThrow(new CerebroException(ErrorCode.ALARM_UNKNOWN, "Exception for tests"));

        Alarm.Alarms alarms = new Alarm.Alarms();
        alarms.setValues(Arrays.asList(alarm));
        when(seyrenRepository.getAllAlarms()).thenReturn(alarms);
    }

    @Test
    public void getSubscription() throws Exception {
        Subscription subscription = subscriptionService.getSubscription(TestUtils.DEFAULT_ALARM_ID, TestUtils.DEFAULT_SUBSCRIPTION_ID);
        assertNotNull(subscription);
        assertEquals(TestUtils.DEFAULT_SUBSCRIPTION_ID, subscription.getId());
        assertEquals(TestUtils.DEFAULT_EMAIL, subscription.getTarget());
    }

    @Test
    public void getNonExistentSubscription() {
        Subscription subscription = null;
        try {
            subscription = subscriptionService.getSubscription(TestUtils.DEFAULT_ALARM_ID, "0000000000000");
            fail("Update non existent subscription should throw an exception");
        } catch (CerebroException e) {
            assertNull(subscription);
            assertEquals(ErrorCode.SUBSCRIPTION_UNKNOWN, e.getErrorCode());
        }
    }

    @Test
    // @Ignore
    public void getSubscriptionOnNonExistentAlarm() {
        try {
            subscriptionService.getSubscription(TestUtils.NONEXISTING_ALARM_ID, TestUtils.DEFAULT_SUBSCRIPTION_ID);
            fail("Subscribe to a non existing alarm should throw a Cerebro exception");
        } catch (CerebroException e) {
            assertEquals(ErrorCode.ALARM_UNKNOWN, e.getErrorCode());
        }
    }

    @Test
    public void addSubscription() throws Exception {
        Subscription subToAdd = TestUtils.getDefaultSubscription();
        String subId = "subTestId";
        subToAdd.setId(subId);
        subToAdd.setTarget("test@anotherteam.org");
        subToAdd.setSu(true);

        // Just verify that doesn't throw an exception
        subscriptionService.addSubscription(subToAdd, TestUtils.DEFAULT_ALARM_ID);
    }

    @Test
    public void addSubscriptionAlreadyExist() throws Exception {
        try {
            subscriptionService.addSubscription(TestUtils.getDefaultSubscription(), TestUtils.DEFAULT_ALARM_ID);
            fail("Adding duplicate exception with success");
        } catch (DuplicateSubscriptionException ce) {
            assertEquals(ErrorCode.SUBSCRIPTION_DUPLICATE, ce.getErrorCode());
        }
    }

    @Test
    public void addSubscriptionFailWithNoTarget() throws Exception {
        Subscription subscription = TestUtils.getDefaultSubscription();
        String subId = "subTestId";
        subscription.setId(subId);
        subscription.setTarget("");

        try {
            subscriptionService.addSubscription(subscription, TestUtils.DEFAULT_ALARM_ID);
            fail("Create a subscription with no target should throw an exception");
        } catch (CerebroException ce) {
            assertEquals(ErrorCode.SUBSCRIPTION_INVALID, ce.getErrorCode());
        }
    }

    @Test
    public void addSubscriptionFailWithNoDayToSendAlert() throws Exception {
        Subscription subscription = TestUtils.getDefaultSubscription();
        String subId = "subTestId";
        subscription.setId(subId);
        subscription.setTarget("test@anotherteam.org");
        subscription.setSu(true);

        subscription.setMo(false);
        subscription.setTu(false);
        subscription.setWe(false);
        subscription.setTh(false);
        subscription.setFr(false);
        subscription.setSa(false);
        subscription.setSu(false);

        try {
            subscriptionService.addSubscription(subscription, TestUtils.DEFAULT_ALARM_ID);
            fail("Create a subscription with no alerting day should throw an exception");
        } catch (CerebroException ce) {
            assertEquals(ErrorCode.SUBSCRIPTION_INVALID, ce.getErrorCode());
        }
    }

    @Test
    public void searchSubscription() throws Exception {
        Subscription s = subscriptionService.searchSubscription(TestUtils.getDefaultSubscription(), TestUtils.DEFAULT_ALARM_ID);
        assertNotNull(s);
        assertEquals(TestUtils.DEFAULT_SUBSCRIPTION_ID, s.getId());
    }

    @Test
    public void searchNonExistentSubscription() {
        Subscription fakeSub = TestUtils.getDefaultSubscription();
        fakeSub.setToTime("2333");
        fakeSub.setFromTime("0123");
        Subscription s = subscriptionService.searchSubscription(fakeSub, TestUtils.DEFAULT_ALARM_ID);
        assertNull(s);
    }

    @Test
    public void deleteSubscription() throws Exception {
        when(seyrenRepository.deleteSubscription(TestUtils.DEFAULT_ALARM_ID, TestUtils.DEFAULT_SUBSCRIPTION_ID)).thenReturn(true);
        assertEquals(SubscriptionService.DeletedSubscriptionStatus.OK,
            subscriptionService.deleteSubscription(TestUtils.DEFAULT_ALARM_ID, TestUtils.DEFAULT_SUBSCRIPTION_ID));
        verify(seyrenRepository).deleteSubscription(TestUtils.DEFAULT_ALARM_ID, TestUtils.DEFAULT_SUBSCRIPTION_ID);
        verify(seyrenRepository).getAlarm(TestUtils.DEFAULT_ALARM_ID);
        verifyNoMoreInteractions(seyrenRepository);
    }

    @Test
    public void deleteSubscriptionFail() throws Exception {
        when(seyrenRepository.deleteSubscription(TestUtils.DEFAULT_ALARM_ID, TestUtils.DEFAULT_SUBSCRIPTION_ID)).thenReturn(false);

        try {
            subscriptionService.deleteSubscription(TestUtils.DEFAULT_ALARM_ID, TestUtils.DEFAULT_SUBSCRIPTION_ID);
        } catch (CerebroException ce) {
            assertEquals(ErrorCode.SUBSCRIPTION_DELETE_ERROR, ce.getErrorCode());
        }

        verify(seyrenRepository).deleteSubscription(TestUtils.DEFAULT_ALARM_ID, TestUtils.DEFAULT_SUBSCRIPTION_ID);
        verifyNoMoreInteractions(seyrenRepository);
    }

    @Test
    public void deleteTheOnlyOneSubscriptionForAAlarm() throws Exception {
        when(seyrenRepository.deleteSubscription(TestUtils.DEFAULT_ALARM_ID, TestUtils.DEFAULT_SUBSCRIPTION_ID)).thenReturn(true);

        // Remove subscription to alarm for mock
        Alarm c = TestUtils.getDefaultAlarm();
        c.setSubscriptions(new ArrayList<>());
        assertTrue(c.getSubscriptions().isEmpty());
        when(seyrenRepository.getAlarm(TestUtils.DEFAULT_ALARM_ID)).thenReturn(c);

        when(seyrenRepository.deleteAlarm(TestUtils.DEFAULT_ALARM_ID)).thenReturn(true);

        assertEquals(SubscriptionService.DeletedSubscriptionStatus.ALARM_DELETED,
            subscriptionService.deleteSubscription(TestUtils.DEFAULT_ALARM_ID, TestUtils.DEFAULT_SUBSCRIPTION_ID));

        verify(seyrenRepository).deleteSubscription(TestUtils.DEFAULT_ALARM_ID, TestUtils.DEFAULT_SUBSCRIPTION_ID);
        verify(seyrenRepository).getAlarm(TestUtils.DEFAULT_ALARM_ID);
        verify(seyrenRepository).deleteAlarm(TestUtils.DEFAULT_ALARM_ID);

        verifyNoMoreInteractions(seyrenRepository);
    }

    @Test
    public void updateSubscriptionOK() throws Exception {
        Subscription subscription = TestUtils.getDefaultSubscription();
        // SUPER-742
        // subscription.setEnabled(!subscription.isEnabled());
        subscription.setMo(!subscription.isMo());

        subscriptionService.updateSubscription(subscription, TestUtils.DEFAULT_ALARM_ID);

        verify(seyrenRepository).updateSubscription(subscription, TestUtils.DEFAULT_ALARM_ID);
    }

    @Test
    public void updateSubscriptionFailIfTargetChange() throws Exception {
        Subscription subscription = TestUtils.getDefaultSubscription();
        subscription.setTarget("coucou");

        try {
            subscriptionService.updateSubscription(subscription, TestUtils.DEFAULT_ALARM_ID);
            fail("Update target of a subscription should throw an exception");
        } catch (CerebroException ce) {
            assertEquals(ErrorCode.SUBSCRIPTION_UPDATE_INVALID, ce.getErrorCode());
        }
    }

    @Test
    public void updateSubscriptionFailIfTypeChange() throws Exception {
        Subscription subscription = TestUtils.getDefaultSubscription();
        subscription.setType(SubscriptionType.HIPCHAT);

        try {
            subscriptionService.updateSubscription(subscription, TestUtils.DEFAULT_ALARM_ID);
            fail("Update type of a subscription should throw an exception");
        } catch (CerebroException ce) {
            assertEquals(ErrorCode.SUBSCRIPTION_UPDATE_INVALID, ce.getErrorCode());
        }
    }

    @Test
    public void updateSubscriptionFailIfNoDayToSendAlert() throws Exception {
        Subscription subscription = TestUtils.getDefaultSubscription();

        subscription.setMo(false);
        subscription.setTu(false);
        subscription.setWe(false);
        subscription.setTh(false);
        subscription.setFr(false);
        subscription.setSa(false);
        subscription.setSu(false);

        try {
            subscriptionService.updateSubscription(subscription, TestUtils.DEFAULT_ALARM_ID);
            fail("Update a subscription set no alerting day should throw an exception");
        } catch (CerebroException ce) {
            assertEquals(ErrorCode.SUBSCRIPTION_INVALID, ce.getErrorCode());
        }
    }

    @Test
    public void updateDisableLastSubscriptionAndDisableAlarmOK() throws Exception {
        Alarm alarm = TestUtils.getDefaultAlarm();
        Subscription subscription = alarm.getSubscriptions().get(0);
        subscription.setEnabled(false);

        subscriptionService.updateSubscription(subscription, TestUtils.DEFAULT_ALARM_ID);

        verify(seyrenRepository).updateSubscription(subscription, TestUtils.DEFAULT_ALARM_ID);
        verify(seyrenRepository).updateAlarm(argThat(new ArgumentMatcher<Alarm>() {
            @Override
            public boolean matches(Object argument) {
                Alarm argAlarm = (Alarm) argument;
                return argAlarm.getId().equals(alarm.getId()) && !argAlarm.isEnabled();
            }
        }));
    }

    @Test
    public void updateSubscriptionEnableAlarm() throws Exception {
        // ****
        // Init, set repo to return a fully disabled alarm
        Alarm disabledAlarm = TestUtils.getDefaultAlarm();
        disabledAlarm.getSubscriptions().get(0).setEnabled(false);
        when(seyrenRepository.getAlarm(TestUtils.DEFAULT_ALARM_ID)).thenReturn(disabledAlarm);
        // ****

        Alarm alarm = TestUtils.getDefaultAlarm();
        Subscription subscription = alarm.getSubscriptions().get(0);
        subscription.setEnabled(true);

        subscriptionService.updateSubscription(subscription, TestUtils.DEFAULT_ALARM_ID);

        verify(seyrenRepository).updateSubscription(subscription, TestUtils.DEFAULT_ALARM_ID);
        verify(seyrenRepository).updateAlarm(argThat(new ArgumentMatcher<Alarm>() {
            @Override
            public boolean matches(Object argument) {
                Alarm argAlarm = (Alarm) argument;
                return argAlarm.getId().equals(alarm.getId()) && argAlarm.isEnabled();
            }
        }));
    }
}
