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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.vsct.supervision.notification.TestUtils;
import com.vsct.supervision.seyren.api.Subscription;
import com.vsct.supervision.seyren.api.SubscriptionType;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionMapperTest {
    @InjectMocks
    private SubscriptionMapper subscriptionMapper = new SubscriptionMapper();

    private Subscription defaultSubscription;

    @Before
    public void setUp() throws Exception {
        this.defaultSubscription = TestUtils.getDefaultSubscription();
    }

    @Test
    public void testMapNewSubscriptionTypeIsMailIdIsNull() throws Exception {
        Subscription subscription = this.defaultSubscription;

        assertNotNull(subscription.getId());
        subscription.setType(SubscriptionType.HIPCHAT);

        Subscription alarm = subscriptionMapper.mapNewSubscriptionFormToSeyren(subscription);
        assertNull(alarm.getId());
        assertEquals(SubscriptionType.EMAIL, subscription.getType());
    }

    @Test
    public void testMapNewSetNullFromToTimeToDefaultValues() throws Exception {
        Subscription s = this.defaultSubscription;
        s.setFromTime(null);
        s.setToTime(null);

        s = subscriptionMapper.mapNewSubscriptionFormToSeyren(s);
        assertEquals(SubscriptionMapper.DEFAULT_FROM_TIME, s.getFromTime());
        assertEquals(SubscriptionMapper.DEFAULT_TO_TIME, s.getToTime());
    }

    @Test
    public void testMapNewSetEmptyFromToTimeToDefaultValues() throws Exception {
        Subscription s = this.defaultSubscription;
        s.setFromTime("");
        s.setToTime("");

        s = subscriptionMapper.mapNewSubscriptionFormToSeyren(s);
        assertEquals(SubscriptionMapper.DEFAULT_FROM_TIME, s.getFromTime());
        assertEquals(SubscriptionMapper.DEFAULT_TO_TIME, s.getToTime());
    }

    @Test
    public void testMapUpdateSetNullFromToTimeToDefaultValues() throws Exception {
        Subscription s = this.defaultSubscription;
        s.setFromTime(null);
        s.setToTime(null);

        s = subscriptionMapper.mapUpdateSubscriptionFormToSeyren(s);
        assertEquals(SubscriptionMapper.DEFAULT_FROM_TIME, s.getFromTime());
        assertEquals(SubscriptionMapper.DEFAULT_TO_TIME, s.getToTime());
    }

    @Test
    public void testMapUpdateSetEmptyFromToTimeToDefaultValues() throws Exception {
        Subscription s = this.defaultSubscription;
        s.setFromTime("");
        s.setToTime("");

        s = subscriptionMapper.mapUpdateSubscriptionFormToSeyren(s);
        assertEquals(SubscriptionMapper.DEFAULT_FROM_TIME, s.getFromTime());
        assertEquals(SubscriptionMapper.DEFAULT_TO_TIME, s.getToTime());
    }
}
