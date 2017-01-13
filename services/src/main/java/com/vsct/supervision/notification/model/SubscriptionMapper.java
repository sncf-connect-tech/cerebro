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

import org.springframework.stereotype.Component;

import com.vsct.supervision.notification.exception.CerebroException;
import com.vsct.supervision.seyren.api.Subscription;
import com.vsct.supervision.seyren.api.SubscriptionType;

@Component
public class SubscriptionMapper {

    public static final String DEFAULT_FROM_TIME = "0800";
    public static final String DEFAULT_TO_TIME = "2000";

    /**
     * Validate and/or force field values in a new subscription form.
     *
     * @param subscription subscription to validate/update
     */
    public Subscription mapNewSubscriptionFormToSeyren(Subscription subscription) throws CerebroException {

        // Reset unwanted fields or enforced default values
        subscription.setId(null);
        subscription.setType(SubscriptionType.EMAIL);

        setDefaultFromToTimeIfEmptyOrNull(subscription);

        return subscription;
    }

    /**
     * Validate and/or force field values in a updated  subscription form.
     *
     * @param subscription subscription to validate/update
     */
    public Subscription mapUpdateSubscriptionFormToSeyren(Subscription subscription) throws CerebroException {
        setDefaultFromToTimeIfEmptyOrNull(subscription);

        return subscription;
    }

    private Subscription setDefaultFromToTimeIfEmptyOrNull(Subscription subscription) {
        if (subscription.getFromTime() == null || subscription.getFromTime().isEmpty() || !subscription.getFromTime().matches("\\d{4}")) {
            subscription.setFromTime(DEFAULT_FROM_TIME);
        }

        if (subscription.getToTime() == null || subscription.getToTime().isEmpty() || !subscription.getToTime().matches("\\d{4}")) {
            subscription.setToTime(DEFAULT_TO_TIME);
        }

        return subscription;
    }
}