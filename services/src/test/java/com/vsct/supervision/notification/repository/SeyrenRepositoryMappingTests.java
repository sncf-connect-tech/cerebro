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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.vsct.supervision.notification.ErrorCode;
import com.vsct.supervision.notification.TestUtils;
import com.vsct.supervision.notification.exception.CerebroException;
import com.vsct.supervision.notification.exception.SeyrenResponseErrorHandler;
import com.vsct.supervision.seyren.api.Alarm;
import com.vsct.supervision.seyren.api.SubscriptionType;

/**
 * Inspiration : https://github.com/jeffsheets/MockRestServiceServerExample
 */
public class SeyrenRepositoryMappingTests {
    private final String seyrenUrl = "fakeSeyren";
    private SeyrenRepository seyrenRepository = new SeyrenRepository();
    private RestTemplate restTemplate = new RestTemplate();
    private MockRestServiceServer mockServer;

    public static final String CHECK_GOOD_FILE_NAME = "/checkGood.json";
    public static final String CHECK_BAD_SUBSCRIPTION_TYPE_FILE_NAME = "/checkBadSubscriptionType.json";
    public static final String CHECK_BAD_URI_FILE_NAME = "/checkBadURI.json";
    public static final String CHECK_INVALID_FILE_NAME = "/checkInvalid.json";

    @Before
    public void setUp() {
        restTemplate.setErrorHandler(new SeyrenResponseErrorHandler());
        mockServer = MockRestServiceServer.createServer(restTemplate);
        seyrenRepository.setRestTemplate(restTemplate);
        seyrenRepository.setSeyrenUrl(seyrenUrl);
    }

    @Test
    public void testGetAlarm() throws IOException, URISyntaxException {
        mockServer.expect(requestTo(seyrenUrl + SeyrenRepository.API_ALARMS + "/" + TestUtils.DEFAULT_ALARM_ID))
            .andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getFile(CHECK_GOOD_FILE_NAME), MediaType.APPLICATION_JSON));

        Alarm c = seyrenRepository.getAlarm(TestUtils.DEFAULT_ALARM_ID);
        assertEquals(SubscriptionType.EMAIL, c.getSubscriptions().get(0).getType());

        mockServer.verify();
    }

    @Test
    public void testGetBadAlarm() throws IOException, URISyntaxException {
        mockServer.expect(requestTo(seyrenUrl + SeyrenRepository.API_ALARMS + "/" + TestUtils.DEFAULT_ALARM_ID))
            .andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getFile(CHECK_BAD_SUBSCRIPTION_TYPE_FILE_NAME), MediaType.APPLICATION_JSON));

        Alarm c = seyrenRepository.getAlarm(TestUtils.DEFAULT_ALARM_ID);
        assertEquals(SubscriptionType.UNKNOW, c.getSubscriptions().get(0).getType());

        mockServer.verify();
    }

    @Test
    public void testGetAlarmWithGoodURI() throws Exception {
        mockServer.expect(requestTo(seyrenUrl + SeyrenRepository.API_ALARMS + "/" + TestUtils.DEFAULT_ALARM_ID))
            .andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getFile(CHECK_GOOD_FILE_NAME), MediaType.APPLICATION_JSON));

        Alarm c = seyrenRepository.getAlarm(TestUtils.DEFAULT_ALARM_ID);


        mockServer.verify();
    }

    @Test
    public void testGetAlarmWithBadURI() throws Exception {
        mockServer.expect(requestTo(seyrenUrl + SeyrenRepository.API_ALARMS + "/" + TestUtils.DEFAULT_ALARM_ID))
            .andExpect(method(HttpMethod.GET)).andRespond(withSuccess(getFile(CHECK_BAD_URI_FILE_NAME), MediaType.APPLICATION_JSON));

        Alarm c = seyrenRepository.getAlarm(TestUtils.DEFAULT_ALARM_ID);


        mockServer.verify();
    }

    @Test
    public void testResponseErrorHandler_404() throws Exception {
        mockServer.expect(requestTo(seyrenUrl + SeyrenRepository.API_ALARMS + "/badAlarm"))
            .andExpect(method(HttpMethod.GET)).andRespond(withServerError());
        
        try {
            seyrenRepository.getAlarm("badAlarm");
            fail();
        } catch (CerebroException e) {
            assertEquals(ErrorCode.SEYREN_ERROR, e.getErrorCode());
        }
    }

    @Test
    public void testResponseErrorHandler_500() throws Exception {
        mockServer.expect(requestTo(seyrenUrl + SeyrenRepository.API_ALARMS + "/addAlarm"))
            .andExpect(method(HttpMethod.PUT)).andRespond(withServerError());
       
        Alarm alarm = new Alarm();
        alarm.setId("addAlarm");

        try {
            seyrenRepository.updateAlarm(alarm);
            fail();
        } catch (CerebroException e) {
            assertEquals(ErrorCode.SEYREN_ERROR, e.getErrorCode());
        }
    }

    private static String getFile(String fileName) throws IOException {
        return IOUtils.toString(SeyrenRepositoryMappingTests.class.getResourceAsStream(fileName), "UTF-8");
    }
}
