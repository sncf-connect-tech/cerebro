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

package com.vsct.supervision.notification.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsct.supervision.notification.model.AlarmMapper;
import com.vsct.supervision.notification.model.GraphiteSources;
import com.vsct.supervision.notification.model.SubscriptionMapper;
import com.vsct.supervision.notification.service.AlarmService;
import com.vsct.supervision.notification.service.AlertService;
import com.vsct.supervision.notification.service.SubscriptionService;
import com.vsct.supervision.seyren.api.Alarm;
import com.vsct.supervision.seyren.api.Alert;
import com.vsct.supervision.seyren.api.AlertType;
import com.vsct.supervision.seyren.api.Subscription;

public class CerebroControllerTest extends AbstractControllerTest{

    private MockMvc mockMvc;

    @Mock
    private AlarmService alarmService;

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private AlertService alertService;

    @Mock
    private GraphiteSources graphiteSources;

    @Mock
    private AlarmMapper alarmMapper;

    @Mock
    private SubscriptionMapper subscriptionMapper;

    @InjectMocks
    private CerebroController cerebroController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
            .standaloneSetup(cerebroController)
            .build();
    }

    @Test
    public void test_sources() throws Exception{

        Map<URI,URI> ipportsByUrl = new HashMap<>();
        ipportsByUrl.put(new URI("key1"),new URI("value1"));
        ipportsByUrl.put(new URI("key2"),new URI("value2"));

        when(graphiteSources.getIpportsByUrl()).thenReturn(ipportsByUrl);

        mockMvc.perform(get("/datasources/locations")).andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]",is("key1")))
        .andExpect(jsonPath("$[1]",is("key2")));
    }

    @Test
    public void test_all_withoutSubTarget() throws Exception{

        Collection<Alarm> alarms = new ArrayList<>();
        for(int i=0; i < 2; i++){
            alarms.add(getAlarm("id",i));
        }

        when(alarmMapper.mapToPresentation(anyCollection())).thenReturn(alarms);

        mockMvc.perform(get("/alarms")).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id",is("id0")))
            .andExpect(jsonPath("$[0].name",is("name0")))
            .andExpect(jsonPath("$[0].target",is("target0")))
            .andExpect(jsonPath("$[1].id",is("id1")))
            .andExpect(jsonPath("$[1].name",is("name1")))
            .andExpect(jsonPath("$[1].target",is("target1")));
    }

    @Test
    public void test_all_with_subscriptionTarget_parameter() throws Exception{

        Collection<Alarm> alarms = new ArrayList<>();
        for(int i=0; i < 2; i++){
            alarms.add(getAlarm("id",i));
        }

        when(alarmMapper.mapToPresentation(anyCollection())).thenReturn(alarms);

        mockMvc.perform(get("/alarms").param("subscriptionTarget","test")).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id",is("id0")))
            .andExpect(jsonPath("$[0].name",is("name0")))
            .andExpect(jsonPath("$[0].target",is("target0")))
            .andExpect(jsonPath("$[1].id",is("id1")))
            .andExpect(jsonPath("$[1].name",is("name1")))
            .andExpect(jsonPath("$[1].target",is("target1")));
    }

    @Test
    public void test_getAlarm() throws Exception{

        when(alarmMapper.mapToPresentation((Alarm)anyObject())).thenReturn(getAlarm("test"));

        mockMvc.perform(get("/alarms/test")).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.alarm.id",is("test")))
            .andExpect(jsonPath("$.alarm.name",is("name")))
            .andExpect(jsonPath("$.alarm.target",is("target")));
    }

    @Test
    public void test_getSubscription() throws Exception{

        when(subscriptionService.getSubscription(anyString(),anyString())).thenReturn(getSubscription("subscriptionId"));

        mockMvc.perform(get("/alarms/alarmId/subscriptions/subscriptionId")).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id",is("subscriptionId")));
    }

    @Test
    public void test_addAlarm() throws Exception{

        Alarm alarm = getAlarm("test");
        alarm.setGraphiteBaseUrl(new URI("graphiteUrl"));
        when(alarmService.subscribeToAAlarm(anyObject())).thenReturn(alarm);

        mockMvc.perform(post("/alarms")
            .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(new ObjectMapper().writeValueAsString(alarm))
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$",is("test")));
    }

    @Test
    public void test_addSubscription() throws Exception{

        Subscription subscription = getSubscription("test");

        doNothing().when(subscriptionService).addSubscription(anyObject(), anyString());
        mockMvc.perform(post("/alarms/test/subscriptions")
            .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(new ObjectMapper().writeValueAsString(subscription))
        ).andExpect(status().isOk());
    }

    @Test
    public void test_deleteSubscription() throws Exception{

        when(subscriptionService.deleteSubscription(anyString(),anyString())).thenReturn(SubscriptionService.DeletedSubscriptionStatus.OK);

        mockMvc.perform(delete("/alarms/alarmId/subscriptions/subscriptionId")
            .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$",is("OK")));
    }

    @Test
    public void test_searchAlarm() throws Exception{

        Alarm alarm = getAlarm("test");
        alarm.setGraphiteBaseUrl(new URI("graphiteUrl"));
        when(alarmMapper.mapToPresentation((Alarm)anyObject())).thenReturn(alarm);

        mockMvc.perform(post("/alarms/search")
            .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(new ObjectMapper().writeValueAsString(alarm))
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id",is("test")))
            .andExpect(jsonPath("$.name",is("name")))
            .andExpect(jsonPath("$.target",is("target")));
    }

    @Test
    public void test_searchSubscription() throws Exception{

        Subscription subscription = getSubscription("subscriptionId");

        when(subscriptionService.searchSubscription(anyObject(),anyString())).thenReturn(subscription);

        mockMvc.perform(post("/alarms/alarmId/subscriptions/search")
            .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(new ObjectMapper().writeValueAsString(subscription))
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id",is("subscriptionId")));

    }

    @Test
    public void test_updateSubscription() throws Exception{

        Subscription subscription = getSubscription("test");

        doNothing().when(subscriptionService).updateSubscription(anyObject(), anyString());

        mockMvc.perform(put("/alarms/alarmId/subscriptions/subscriptionId")
            .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(new ObjectMapper().writeValueAsString(subscription))
        ).andExpect(status().isOk());
    }

    @Test
    public void test_getAlerts_without_params() throws Exception{

        Collection<Alert> values = Arrays.asList(getAlert("alert1"),getAlert("alert2"));
        Alert.Alerts alerts = new Alert.Alerts();
        alerts.setValues(values);
        when(alertService.getAlerts(anyInt(), anyInt())).thenReturn(alerts);

        mockMvc.perform(get("/alerts")).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is("alert1")))
            .andExpect(jsonPath("$[1].id", is("alert2")))
            ;
    }

    @Test
    public void test_getAlerts_with_from_param() throws Exception{

        Collection<Alert> values = Arrays.asList(getAlert("alert1"),getAlert("alert2"));
        when(alertService.getAlerts(anyString())).thenReturn(values);

        mockMvc.perform(get("/alerts").param("from","test"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is("alert1")))
            .andExpect(jsonPath("$[1].id", is("alert2")))
        ;
    }

    @Test
    public void test_getAlerts_with_start_param_and_nbItem_param() throws Exception{

        Collection<Alert> values = Arrays.asList(getAlert("alert1"),getAlert("alert2"));
        Alert.Alerts alerts = new Alert.Alerts();
        alerts.setValues(values);
        when(alertService.getAlerts(anyInt(), anyInt())).thenReturn(alerts);

        mockMvc.perform(get("/alerts")
            .param("start","1")
            .param("nbItem","2")
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is("alert1")))
            .andExpect(jsonPath("$[1].id", is("alert2")))
        ;
    }

    @Test
    public void test_getAlertStatNoTypeChange() throws Exception{

        when(alertService.getStatNoTypeChange(anyString())).thenReturn(getStats(2, AlertType.OK));

        mockMvc.perform(get("/alerts/stats/nochanges")
            .param("from","1")
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].alarmId", is("alarm1")))
            .andExpect(jsonPath("$[0].type", is("OK")))
            .andExpect(jsonPath("$[0].count", is(1)))
            .andExpect(jsonPath("$[1].alarmId", is("alarm0")))
            .andExpect(jsonPath("$[1].type", is("OK")))
            .andExpect(jsonPath("$[1].count", is(1)))
        ;
    }

    @Test
    public void test_getAlarmAlerts() throws Exception{

        Collection<Alert> values = Arrays.asList(getAlert("alert1"),getAlert("alert2"));
        Alert.Alerts alerts = new Alert.Alerts();
        alerts.setValues(values);
        when(alertService.getAlarmAlerts(anyString())).thenReturn(alerts);

        mockMvc.perform(get("/alarms/id/alerts")
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is("alert1")))
            .andExpect(jsonPath("$[1].id", is("alert2")))
        ;

    }

    @Test
    public void test_updateAlarm() throws Exception{

        Alarm alarm = getAlarm("test");
        alarm.setGraphiteBaseUrl(new URI("graphiteUrl"));
        when(alarmService.updateAlarm(anyObject())).thenReturn(alarm);

        mockMvc.perform(put("/alarms")
            .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .content(new ObjectMapper().writeValueAsString(alarm))
        ).andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$",is("test")));
    }
}
