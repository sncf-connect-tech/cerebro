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

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.vsct.supervision.config.CerebroConfiguration;
import com.vsct.supervision.notification.TestUtils;
import com.vsct.supervision.seyren.api.Alarm;
import com.vsct.supervision.seyren.api.Subscription;
import com.vsct.supervision.seyren.api.SubscriptionType;

@RunWith(MockitoJUnitRunner.class)
public class NotificationHandlerTest {

    @Mock
    private CerebroConfiguration mockConfiguration;

    private NotificationHandler notificationHandler;

    @Mock
    private MailSenderImpl senderMock;

    private String dashboardTestBaseUrl = "http://cerebro.test.fr/cerebro";

    @Before
    public void setUp() throws Exception {
        when(mockConfiguration.getDashboardBaseUrl()).thenReturn(dashboardTestBaseUrl);
        when(mockConfiguration.isUpdateNotificationsEnable()).thenReturn(true);
        doNothing().when(senderMock).send(anyString(),anyString(),anyListOf(String.class));
        when(senderMock.getProperties()).thenReturn(getProperties());
        notificationHandler = new NotificationHandler(senderMock, mockConfiguration);
    }

    @Test
    public void sendAlarmHasBeenModified_shouldSendModificationMail() {
        Alarm alarm = TestUtils.getDefaultAlarm();

        Map<String, String> model = new HashMap<>();
        model.put("status", "changed");
        model.put("alert", alarm.getName());
        model.put("link", dashboardTestBaseUrl + "/notifications/" + alarm.getId());

        List<String> recipients = alarm.getSubscriptions().stream().map(Subscription::getTarget).collect(Collectors.toList());
        notificationHandler.sendAlarmHasBeenModified(alarm);

        verify(senderMock).send("One of your alerts has been changed",notificationHandler.processTemplate("checkModified.vm", model),recipients);
    }

    @Test
    public void sendAlarmHasBeenDeactivated_shouldSendDeactivationMail() {
        Alarm alarm = TestUtils.getDefaultAlarm();

        Map<String, String> model = new HashMap<>();
        model.put("status", "disabled");
        model.put("alert", alarm.getName());
        model.put("link", dashboardTestBaseUrl + "/notifications/" + alarm.getId());

        List<String> recipients = alarm.getSubscriptions().stream().map(Subscription::getTarget).collect(Collectors.toList());

        notificationHandler.sendAlarmHasBeenDeactivated(alarm);

        verify(senderMock).send("One of your alerts has been disabled",notificationHandler.processTemplate("checkModified.vm", model),recipients);
    }

    @Test
    public void sendAlarmOnlyOnMailSubscriptionTarget() {
        Alarm alarm = TestUtils.getDefaultAlarm();
        Subscription s1 = TestUtils.getDefaultSubscription();
        Subscription s2 = TestUtils.getDefaultSubscription();
        s2.setTarget("/dev/null");
        s2.setType(SubscriptionType.SHELL);

        alarm.setSubscriptions(Arrays.asList(s1, s2));

        Map<String, String> model = new HashMap<>();
        model.put("status", "disabled");
        model.put("alert", alarm.getName());
        model.put("link", dashboardTestBaseUrl + "/notifications/" + alarm.getId());

        List<String> recipients = Collections.singletonList(s1.getTarget());

        notificationHandler.sendAlarmHasBeenDeactivated(alarm);

        verify(senderMock).send("One of your alerts has been disabled",notificationHandler.processTemplate("checkModified.vm", model),recipients);
    }

    @Test
    public void sendAlarmOnlyOnEnabledSubscription() {
        Alarm alarm = TestUtils.getDefaultAlarm();
        Subscription s1 = TestUtils.getDefaultSubscription();
        Subscription s2 = TestUtils.getDefaultSubscription();
        s2.setTarget("disabled@sub.com");
        s2.setEnabled(false);

        alarm.setSubscriptions(Arrays.asList(s1, s2));

        Map<String, String> model = new HashMap<>();
        model.put("status", "disabled");
        model.put("alert", alarm.getName());
        model.put("link", dashboardTestBaseUrl + "/notifications/" + alarm.getId());

        List<String> recipients = Collections.singletonList(s1.getTarget());

        notificationHandler.sendAlarmHasBeenDeactivated(alarm);

        verify(senderMock).send("One of your alerts has been disabled",notificationHandler.processTemplate("checkModified.vm", model),recipients);
    }

    private Properties getProperties() throws IOException{
        Properties props = new Properties();
        props.load(MailSenderImpl.class.getResourceAsStream("/config/email.properties"));
        return props;
    }
}
