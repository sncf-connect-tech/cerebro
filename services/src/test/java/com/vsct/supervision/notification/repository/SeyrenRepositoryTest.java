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

package com.vsct.supervision.notification.repository;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.vsct.supervision.notification.ErrorCode;
import com.vsct.supervision.notification.TestUtils;
import com.vsct.supervision.notification.exception.CerebroException;
import com.vsct.supervision.notification.exception.SeyrenException;
import com.vsct.supervision.seyren.api.Alert;
import com.vsct.supervision.seyren.api.Alarm;
import com.vsct.supervision.seyren.api.Subscription;

@RunWith(MockitoJUnitRunner.class)
public class SeyrenRepositoryTest {
    private static final String DEFAULT_SEYREN_URL = "coucou";

    @InjectMocks
    private final SeyrenRepository repository = new SeyrenRepository();

    @Mock
    private RestTemplate restTemplate;

    @Before
    public void init() {
        repository.setSeyrenUrl(DEFAULT_SEYREN_URL);

        when(restTemplate.getForObject(DEFAULT_SEYREN_URL + SeyrenRepository.API_ALARMS + "/" + TestUtils.DEFAULT_ALARM_ID, Alarm.class))
            .thenReturn(TestUtils.getDefaultAlarm());
    }

    @Test
    public void testGetAlarmOK() throws Exception {
        Alarm alarm = repository.getAlarm(TestUtils.DEFAULT_ALARM_ID);

        assertEquals(TestUtils.DEFAULT_ALARM_ID, alarm.getId());
    }

    @Test
    public void testGetAlarmFailIfAlarmNotFound() {
        when(restTemplate.getForObject(anyString(), eq(Alarm.class))).thenThrow(new ResourceAccessException("coucou"));

        try {
            repository.getAlarm("coucou");
            fail();
        } catch (CerebroException e) {
            assertEquals(ErrorCode.ALARM_UNKNOWN, e.getErrorCode());
        }
    }

    @Test
    public void testGetAllAlarms() {

        Alarm.Alarms result = new Alarm.Alarms();
        result.setValues(Arrays.asList(TestUtils.getDefaultAlarm(), TestUtils.getDefaultAlarm()));

        when(restTemplate.getForObject(DEFAULT_SEYREN_URL + SeyrenRepository.API_ALARMS,Alarm.Alarms.class)).thenReturn(result);

        Alarm.Alarms alarms = repository.getAllAlarms();
        assertNotNull(alarms);
        assertTrue(alarms.getValues().size()==result.getValues().size());
        assertArrayEquals(result.getValues().toArray(),alarms.getValues().toArray());
    }

    @Test
    public void testUpdateAlarm() {

        Alarm alarm = TestUtils.getDefaultAlarm();

        doNothing().when(restTemplate).put(anyString(), anyObject(), any(String.class));
        repository.updateAlarm(alarm);
        verify(restTemplate).put(DEFAULT_SEYREN_URL + SeyrenRepository.API_ALARMS+"/"+alarm.getId(), alarm, String.class);
    }

    @Test
    public void testDeleteAlarm() {
        ResponseEntity<Object> response = new ResponseEntity(HttpStatus.OK);
        when(restTemplate.exchange(anyString(), Mockito.<HttpMethod> eq(HttpMethod.DELETE), Mockito.<HttpEntity<?>> any(), Matchers.<Class<Object>>any())).thenReturn(response);
        assertTrue(repository.deleteAlarm("id"));
    }

    @Test
    public void testAddSubscription_ok() {

        ResponseEntity<Object> response = new ResponseEntity(HttpStatus.OK);
        Subscription subscription = new Subscription();

        when(restTemplate.postForEntity(anyString(),anyObject(),Matchers.<Class<Object>>any())).thenReturn(response);

        repository.addSubscription(subscription,"alarmid");
        verify(restTemplate).postForEntity(DEFAULT_SEYREN_URL + SeyrenRepository.API_ALARMS+"/alarmid/subscriptions/", subscription, Object.class);
    }

    @Test
    public void testAddSubscription_ko() {

        ResponseEntity<Object> response = new ResponseEntity(HttpStatus.CONFLICT);
        Subscription subscription = new Subscription();

        when(restTemplate.postForEntity(anyString(),anyObject(),Matchers.<Class<Object>>any())).thenReturn(response);
        try{
            repository.addSubscription(subscription,"alarmid");
            verify(restTemplate).postForEntity(DEFAULT_SEYREN_URL + SeyrenRepository.API_ALARMS+"/alarmid/subscriptions/", subscription, Object.class);
            fail();
        }
        catch(SeyrenException e){
            assertEquals(e.getHttpStatus(),HttpStatus.CONFLICT.value());
            assertEquals(e.getAction(),"addSubscription");
        }

    }

    @Test
    public void testUpdateSubscription() {

        Subscription subscription = new Subscription();
        subscription.setId("subscriptionId");

        doNothing().when(restTemplate).put(anyString(),anyObject(),Matchers.<Class<Object>>any());

        repository.updateSubscription(subscription,"alarmId");
        verify(restTemplate).put(DEFAULT_SEYREN_URL + SeyrenRepository.API_ALARMS+"/alarmId/subscriptions/subscriptionId", subscription, String.class);
    }

    @Test
    public void testDeleteSubscription() {
        ResponseEntity<Object> response = new ResponseEntity(HttpStatus.OK);
        when(restTemplate.exchange(anyString(), Mockito.<HttpMethod> eq(HttpMethod.DELETE), Mockito.<HttpEntity<?>> any(), Matchers.<Class<Object>>any())).thenReturn(response);
        assertTrue(repository.deleteSubscription("alarmId","subscriptionId"));
    }

    @Test
    public void testGetAlerts() {

        Alert.Alerts result = new Alert.Alerts();
        result.setValues(Arrays.asList(TestUtils.getDefaultAlert(), TestUtils.getDefaultAlert()));

        when(restTemplate.getForObject(DEFAULT_SEYREN_URL + SeyrenRepository.API_ALERTS + "?start=0&items=10",Alert.Alerts.class)).thenReturn(result);

        Alert.Alerts alerts = repository.getAlerts(0,10);
        assertNotNull(alerts);
        assertTrue(alerts.getValues().size()==result.getValues().size());
        assertArrayEquals(result.getValues().toArray(),alerts.getValues().toArray());
    }

    @Test
    public void testGetAlarmAlerts() {

        Alert.Alerts result = new Alert.Alerts();
        result.setValues(Arrays.asList(TestUtils.getDefaultAlert(), TestUtils.getDefaultAlert()));

        when(restTemplate.getForObject(DEFAULT_SEYREN_URL + SeyrenRepository.API_ALARMS + "/alarmId/alerts?start=0&items=10",Alert.Alerts.class)).thenReturn(result);

        Alert.Alerts alerts = repository.getAlarmAlerts("alarmId",0,10);
        assertNotNull(alerts);
        assertTrue(alerts.getValues().size()==result.getValues().size());
        assertArrayEquals(result.getValues().toArray(),alerts.getValues().toArray());

    }

}
