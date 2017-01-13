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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.vsct.supervision.notification.TestUtils;
import com.vsct.supervision.seyren.api.Alarm;
import com.vsct.supervision.seyren.api.Subscription;

@RunWith(MockitoJUnitRunner.class)
public class AlarmMapperTest {
    private static final URI DEFAULT_GRAPHITE_URL;
    private static final URI OTHER_GRAPHITE_URL;
    private static final URI DEFAULT_GRAPHITE_IPPORT;
    private static final URI OTHER_GRAPHITE_IPPORT;

    static {
        try {
            DEFAULT_GRAPHITE_URL = new URI("http:///mygraphiteinstance");
            DEFAULT_GRAPHITE_IPPORT = new URI("http:///0.0.0.0:666");
            OTHER_GRAPHITE_URL = new URI("http:///myothergraphiteinstance");
            OTHER_GRAPHITE_IPPORT = new URI("http:///0.0.0.0:777");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @InjectMocks
    private AlarmMapper alarmMapper = new AlarmMapper();

    @Mock
    private GraphiteSources graphiteSources;

    @Mock
    private AlarmValidator alarmValidator;

    @Mock
    private SubscriptionMapper subscriptionMapper;

    private Alarm defaultAlarm;

    @Before
    public void init() {
        Map<URI, URI> defaultUriMap = new HashMap<>();
        defaultUriMap.put(DEFAULT_GRAPHITE_URL, DEFAULT_GRAPHITE_URL);
        when(graphiteSources.getIpportsByUrl()).thenReturn(defaultUriMap);

        Subscription newSub = TestUtils.getDefaultSubscription();
        newSub.setId(null);
        when(subscriptionMapper.mapNewSubscriptionFormToSeyren(any())).thenReturn(newSub);
        when(subscriptionMapper.mapUpdateSubscriptionFormToSeyren(any())).thenReturn(TestUtils.getDefaultSubscription());

        this.defaultAlarm = TestUtils.getDefaultAlarm();
    }

    @Test
    public void testMapNewAlarmFormToSeyrenOK() throws Exception {
        alarmMapper.mapNewAlarmFormToSeyren(this.defaultAlarm);
        verify(alarmValidator).validateAlarm(this.defaultAlarm);

        this.defaultAlarm.getSubscriptions()
            .forEach(subscription -> verify(subscriptionMapper).mapNewSubscriptionFormToSeyren(subscription));
    }

    @Test
    public void testMapUpdateAlarmFormToSeyrenOK() throws Exception {
        alarmMapper.mapUpdateAlarmFormToSeyren(this.defaultAlarm);
        verify(alarmValidator).validateAlarm(this.defaultAlarm);

        this.defaultAlarm.getSubscriptions()
            .forEach(subscription -> verify(subscriptionMapper).mapUpdateSubscriptionFormToSeyren(subscription));
    }

    @Test
    public void testSetNullFromUntilTimeToDefaultValues() throws Exception {
        Alarm c = this.defaultAlarm;
        c.setFrom(null);
        c.setUntil(null);

        c = alarmMapper.mapNewAlarmFormToSeyren(c);
        assertEquals(alarmMapper.DEFAULT_FROM, c.getFrom());
        assertEquals(alarmMapper.DEFAULT_UNTIL, c.getUntil());
    }

    @Test
    public void testSetEmptyFromUntilTimeToDefaultValues() throws Exception {
        Alarm c = this.defaultAlarm;
        c.setFrom("");
        c.setUntil("");

        c = alarmMapper.mapNewAlarmFormToSeyren(c);
        assertEquals(AlarmMapper.DEFAULT_FROM, c.getFrom());
        assertEquals(AlarmMapper.DEFAULT_UNTIL, c.getUntil());
    }
    
    // TEST GRAPHITE URL/IPPORT CONVERSION

    @Test
    public void testNoFrontendUrlWithNoMapping() throws Exception {

        Map<URI, URI> emptyMap = new HashMap<>();
        when(graphiteSources.getIpportsByUrl()).thenReturn(new HashMap<>());
        when(this.graphiteSources.getSources()).thenReturn(new ArrayList<GraphiteSources.GraphiteSource>());

        Alarm frontendAlarm = TestUtils.getDefaultAlarm();
        frontendAlarm.setGraphiteBaseUrl(null);
        Alarm backendAlarm = alarmMapper.mapNewAlarmFormToSeyren(frontendAlarm);
        assertEquals(null, backendAlarm.getGraphiteBaseUrl());
    }

    @Test
    public void testNoFrontendUrlWithSingleUrl() throws Exception {

        Map<URI, URI> emptyMap = new HashMap<>();
        when(graphiteSources.getIpportsByUrl()).thenReturn(new HashMap<>());
        when(this.graphiteSources.getSources()).thenReturn(Stream.of(new GraphiteSources.GraphiteSource(DEFAULT_GRAPHITE_URL)).collect(Collectors.toList()));

        Alarm frontendAlarm = TestUtils.getDefaultAlarm();
        frontendAlarm.setGraphiteBaseUrl(null);
        Alarm backendAlarm = alarmMapper.mapNewAlarmFormToSeyren(frontendAlarm);
        assertEquals(DEFAULT_GRAPHITE_URL, backendAlarm.getGraphiteBaseUrl());
    }

    @Test
    public void testNoFrontendUrlWithMultipleUrl() throws Exception {

        Map<URI, URI> emptyMap = new HashMap<>();
        when(graphiteSources.getIpportsByUrl()).thenReturn(new HashMap<>());
        when(this.graphiteSources.getSources()).thenReturn(
            Stream.of(new GraphiteSources.GraphiteSource(DEFAULT_GRAPHITE_URL), new GraphiteSources.GraphiteSource(OTHER_GRAPHITE_URL))
                .collect(Collectors.toList()));
        when(this.graphiteSources.getSources()).thenReturn(new ArrayList<GraphiteSources.GraphiteSource>());

        Alarm frontendAlarm = TestUtils.getDefaultAlarm();
        frontendAlarm.setGraphiteBaseUrl(null);
        Alarm backendAlarm = alarmMapper.mapNewAlarmFormToSeyren(frontendAlarm);
        assertEquals(null, backendAlarm.getGraphiteBaseUrl());
    }

    @Test
    public void testNoFrontendUrlWithSingleUrlIpport() throws Exception {

        Map<URI, URI> ipports = new HashMap<>();
        ipports.put(DEFAULT_GRAPHITE_URL, DEFAULT_GRAPHITE_IPPORT);
        when(graphiteSources.getIpportsByUrl()).thenReturn(ipports);
        when(this.graphiteSources.getSources()).thenReturn(
            Stream.of(new GraphiteSources.GraphiteSource(DEFAULT_GRAPHITE_URL, DEFAULT_GRAPHITE_IPPORT)).collect(Collectors.toList()));

        Alarm frontendAlarm = TestUtils.getDefaultAlarm();
        frontendAlarm.setGraphiteBaseUrl(null);
        Alarm backendAlarm = alarmMapper.mapNewAlarmFormToSeyren(frontendAlarm);
        assertEquals(DEFAULT_GRAPHITE_IPPORT, backendAlarm.getGraphiteBaseUrl());
    }

    @Test
    public void testNoFrontendUrlWithMultipleUrlIpport() throws Exception {

        Map<URI, URI> ipports = new HashMap<>();
        ipports.put(DEFAULT_GRAPHITE_URL, DEFAULT_GRAPHITE_IPPORT);
        ipports.put(OTHER_GRAPHITE_URL, OTHER_GRAPHITE_IPPORT);
        when(graphiteSources.getIpportsByUrl()).thenReturn(ipports);
        when(this.graphiteSources.getSources()).thenReturn(Stream
            .of(new GraphiteSources.GraphiteSource(DEFAULT_GRAPHITE_URL, DEFAULT_GRAPHITE_IPPORT),
                new GraphiteSources.GraphiteSource(OTHER_GRAPHITE_URL, OTHER_GRAPHITE_IPPORT)).collect(Collectors.toList()));

        Alarm frontendAlarm = TestUtils.getDefaultAlarm();
        frontendAlarm.setGraphiteBaseUrl(null);
        Alarm backendAlarm = alarmMapper.mapNewAlarmFormToSeyren(frontendAlarm);
        assertEquals(OTHER_GRAPHITE_IPPORT, backendAlarm.getGraphiteBaseUrl());
    }

    @Test
    public void testFrontendUrlWithNoMapping() throws Exception {

        Map<URI, URI> emptyMap = new HashMap<>();
        when(graphiteSources.getIpportsByUrl()).thenReturn(new HashMap<>());
        when(this.graphiteSources.getSources()).thenReturn(new ArrayList<GraphiteSources.GraphiteSource>());

        Alarm frontendAlarm = TestUtils.getDefaultAlarm();
        frontendAlarm.setGraphiteBaseUrl(DEFAULT_GRAPHITE_URL);
        Alarm backendAlarm = alarmMapper.mapNewAlarmFormToSeyren(frontendAlarm);
        assertEquals(DEFAULT_GRAPHITE_URL, backendAlarm.getGraphiteBaseUrl());
    }

    @Test
    public void testFrontendUrlWithSingleUrl() throws Exception {

        Map<URI, URI> emptyMap = new HashMap<>();
        when(graphiteSources.getIpportsByUrl()).thenReturn(new HashMap<>());
        when(this.graphiteSources.getSources()).thenReturn(Stream.of(new GraphiteSources.GraphiteSource(DEFAULT_GRAPHITE_URL)).collect(Collectors.toList()));

        Alarm frontendAlarm = TestUtils.getDefaultAlarm();
        frontendAlarm.setGraphiteBaseUrl(DEFAULT_GRAPHITE_URL);
        Alarm backendAlarm = alarmMapper.mapNewAlarmFormToSeyren(frontendAlarm);
        assertEquals(DEFAULT_GRAPHITE_URL, backendAlarm.getGraphiteBaseUrl());
    }

    @Test
    public void testFrontendUrlWithMultipleUrl() throws Exception {

        Map<URI, URI> emptyMap = new HashMap<>();
        when(graphiteSources.getIpportsByUrl()).thenReturn(new HashMap<>());
        when(this.graphiteSources.getSources()).thenReturn(
            Stream.of(new GraphiteSources.GraphiteSource(DEFAULT_GRAPHITE_URL), new GraphiteSources.GraphiteSource(OTHER_GRAPHITE_URL))
                .collect(Collectors.toList()));
        when(this.graphiteSources.getSources()).thenReturn(new ArrayList<GraphiteSources.GraphiteSource>());

        Alarm frontendAlarm = TestUtils.getDefaultAlarm();
        frontendAlarm.setGraphiteBaseUrl(DEFAULT_GRAPHITE_URL);
        Alarm backendAlarm = alarmMapper.mapNewAlarmFormToSeyren(frontendAlarm);
        assertEquals(DEFAULT_GRAPHITE_URL, backendAlarm.getGraphiteBaseUrl());
    }

    @Test
    public void testFrontendUrlWithSingleUrlIpport() throws Exception {

        Map<URI, URI> ipports = new HashMap<>();
        ipports.put(DEFAULT_GRAPHITE_URL, DEFAULT_GRAPHITE_IPPORT);
        when(graphiteSources.getIpportsByUrl()).thenReturn(ipports);
        when(this.graphiteSources.getSources()).thenReturn(
            Stream.of(new GraphiteSources.GraphiteSource(DEFAULT_GRAPHITE_URL, DEFAULT_GRAPHITE_IPPORT)).collect(Collectors.toList()));

        Alarm frontendAlarm = TestUtils.getDefaultAlarm();
        frontendAlarm.setGraphiteBaseUrl(DEFAULT_GRAPHITE_URL);
        Alarm backendAlarm = alarmMapper.mapNewAlarmFormToSeyren(frontendAlarm);
        assertEquals(DEFAULT_GRAPHITE_IPPORT, backendAlarm.getGraphiteBaseUrl());
    }

    @Test
    public void testFrontendUrlWithMultipleUrlIpport() throws Exception {

        Map<URI, URI> ipports = new HashMap<>();
        ipports.put(DEFAULT_GRAPHITE_URL, DEFAULT_GRAPHITE_IPPORT);
        ipports.put(OTHER_GRAPHITE_URL, OTHER_GRAPHITE_IPPORT);
        when(graphiteSources.getIpportsByUrl()).thenReturn(ipports);
        when(this.graphiteSources.getSources()).thenReturn(Stream
            .of(new GraphiteSources.GraphiteSource(DEFAULT_GRAPHITE_URL, DEFAULT_GRAPHITE_IPPORT),
                new GraphiteSources.GraphiteSource(OTHER_GRAPHITE_URL, OTHER_GRAPHITE_IPPORT)).collect(Collectors.toList()));

        Alarm frontendAlarm = TestUtils.getDefaultAlarm();
        frontendAlarm.setGraphiteBaseUrl(OTHER_GRAPHITE_URL);
        Alarm backendAlarm = alarmMapper.mapNewAlarmFormToSeyren(frontendAlarm);
        assertEquals(OTHER_GRAPHITE_IPPORT, backendAlarm.getGraphiteBaseUrl());
    }

    @Test
    public void testUnknownFrontendUrlWithNoMapping() throws Exception {

        Map<URI, URI> emptyMap = new HashMap<>();
        when(graphiteSources.getIpportsByUrl()).thenReturn(new HashMap<>());
        when(this.graphiteSources.getSources()).thenReturn(new ArrayList<GraphiteSources.GraphiteSource>());

        Alarm frontendAlarm = TestUtils.getDefaultAlarm();
        frontendAlarm.setGraphiteBaseUrl(OTHER_GRAPHITE_URL);
        Alarm backendAlarm = alarmMapper.mapNewAlarmFormToSeyren(frontendAlarm);
        assertEquals(OTHER_GRAPHITE_URL, backendAlarm.getGraphiteBaseUrl());
    }

    @Test
    public void testUnknownFrontendUrlWithSingleUrl() throws Exception {

        Map<URI, URI> emptyMap = new HashMap<>();
        when(graphiteSources.getIpportsByUrl()).thenReturn(new HashMap<>());
        when(this.graphiteSources.getSources()).thenReturn(Stream.of(new GraphiteSources.GraphiteSource(DEFAULT_GRAPHITE_URL)).collect(Collectors.toList()));

        Alarm frontendAlarm = TestUtils.getDefaultAlarm();
        frontendAlarm.setGraphiteBaseUrl(OTHER_GRAPHITE_URL);
        Alarm backendAlarm = alarmMapper.mapNewAlarmFormToSeyren(frontendAlarm);
        assertEquals(OTHER_GRAPHITE_URL, backendAlarm.getGraphiteBaseUrl());
    }

    @Test
    public void testUnknownFrontendUrlWithSingleUrlIpport() throws Exception {

        Map<URI, URI> ipports = new HashMap<>();
        ipports.put(DEFAULT_GRAPHITE_URL, DEFAULT_GRAPHITE_URL);
        when(graphiteSources.getIpportsByUrl()).thenReturn(ipports);
        when(this.graphiteSources.getSources()).thenReturn(
            Stream.of(new GraphiteSources.GraphiteSource(DEFAULT_GRAPHITE_URL, DEFAULT_GRAPHITE_IPPORT)).collect(Collectors.toList()));

        Alarm frontendAlarm = TestUtils.getDefaultAlarm();
        frontendAlarm.setGraphiteBaseUrl(OTHER_GRAPHITE_URL);
        Alarm backendAlarm = alarmMapper.mapNewAlarmFormToSeyren(frontendAlarm);
        assertEquals(OTHER_GRAPHITE_URL, backendAlarm.getGraphiteBaseUrl());
    }

}
