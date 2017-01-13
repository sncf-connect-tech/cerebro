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

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vsct.supervision.notification.ErrorCode;
import com.vsct.supervision.notification.exception.CerebroException;
import com.vsct.supervision.notification.exception.DuplicateSubscriptionException;
import com.vsct.supervision.notification.exception.SeyrenException;
import com.vsct.supervision.notification.repository.SeyrenRepository;
import com.vsct.supervision.seyren.api.Alarm;
import com.vsct.supervision.seyren.api.Subscription;

@Service
public class SubscriptionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionService.class);

    @Autowired
    private SeyrenRepository seyrenRepository;

    /**
     * Get a subscription for a alarm.
     *
     * @param alarmId The ID of the alarm
     * @param subscriptionId The ID of the subscription to retrieve
     * @return The subscription retrieved
     */
    public Subscription getSubscription(String alarmId, String subscriptionId) {
        Alarm c = seyrenRepository.getAlarm(alarmId);

        Subscription ret = c.getSubscriptions().stream().filter(s -> s.getId().equals(subscriptionId)).findAny().orElse(null);

        if (ret == null) {
            LOGGER.debug("Subscription {} for alarm {} does not exist.", subscriptionId, alarmId);
            throw new CerebroException(ErrorCode.SUBSCRIPTION_UNKNOWN,
                "Subscription " + subscriptionId + " for alarm " + alarmId + " does not exist.");
        }

        return ret;
    }

    /**
     * Add a new subscription to a alarm.
     *
     * If subscription already exist, throw an exception.
     *
     * @param subscription the subscription to add
     * @param alarmId the ID of the alarm to add subscription
     * @throws SeyrenException If add of subscription in Seyren fail (status code != 2xx)
     * @throws DuplicateSubscriptionException If subscription to add already exist
     */
    public void addSubscription(Subscription subscription, String alarmId) {
        validateSubscription(subscription);

        if (this.searchSubscription(subscription, alarmId) != null) {
            LOGGER.debug("Identical subscription found (alarm id: {}).", alarmId);
            throw new DuplicateSubscriptionException("Attempting to add a subscription that already exists on alarm " + alarmId);
        }

        seyrenRepository.addSubscription(subscription, alarmId);
    }

    /**
     * Search a subscription in a alarm by these properties (from time, to time, days, etc.).
     *
     * @param subscription The subscription to search
     * @param alarmId ID of alarm look for subscription
     * @return subscription found, null if not found
     */
    public Subscription searchSubscription(Subscription subscription, String alarmId) {
        LOGGER.debug("Searched identical subscription to: {}", subscription);

        Alarm alarm = seyrenRepository.getAlarm(alarmId);

        return alarm.getSubscriptions().stream().filter(s -> s.equals(subscription)).findAny().orElse(null);
    }

    /**
     * Update a subscription in Seyren (by subscription ID).
     *
     * <i>This method do nothing more than calling {@link SeyrenRepository#updateSubscription(Subscription, String)}</i>
     *
     * @param subscription The subscription to update
     * @param alarmId the ID of the alarm to update subscription
     */
    public void updateSubscription(Subscription subscription, String alarmId) {
        Alarm alarm = seyrenRepository.getAlarm(alarmId);
        Optional<Subscription> actualSub = alarm.getSubscriptions().stream().filter(s -> s.getId().equals(subscription.getId())).findAny();
        if (!actualSub.isPresent()) {
            throw new CerebroException(ErrorCode.SUBSCRIPTION_UNKNOWN,
                "The subscription passed as parameter does not exist in the Alarm " + alarmId);
        }

        validateUpdateSubscription(actualSub.get(), subscription);

        if (alarm.isEnabled()) {
            if (isLastSubscriptionActiveToDisable(actualSub.get(), subscription, alarm)) {
                alarm.setEnabled(false);
                seyrenRepository.updateAlarm(alarm);
            }
        } else if (!actualSub.get().isEnabled() && subscription.isEnabled()) {
            alarm.setEnabled(true);
            seyrenRepository.updateAlarm(alarm);
        }

        seyrenRepository.updateSubscription(subscription, alarmId);
    }

    private void validateSubscription(Subscription subscription) {
        if (!subscription.isFr() && !subscription.isMo() && !subscription.isSa() && !subscription.isSu() && !subscription.isTh()
            && !subscription.isTu() && !subscription.isWe()) {
            throw new CerebroException(ErrorCode.SUBSCRIPTION_INVALID, "No day selected to send alerts");
        }

        if (StringUtils.isEmpty(subscription.getTarget())) {
            throw new CerebroException(ErrorCode.SUBSCRIPTION_INVALID, "No target for the subscription");
        }
    }

    private void validateUpdateSubscription(Subscription current, Subscription updated) {
        validateSubscription(updated);

        if (!Objects.equals(current.getTarget(), updated.getTarget()) || !Objects.equals(current.getType(), updated.getType())) {
            throw new CerebroException(ErrorCode.SUBSCRIPTION_UPDATE_INVALID, "Fields 'target' and 'type' can not be updated");
        }

    }

    private boolean isLastSubscriptionActiveToDisable(Subscription current, Subscription updated, Alarm alarm) {
        return current.isEnabled() && !updated.isEnabled() && !alarm.getSubscriptions().stream()
            .filter(s -> (!s.getId().equals(current.getId()) && s.isEnabled())).findAny().isPresent();
    }

    /**
     * Remove a subscription to a alarm and the alarm if it have no more subscription.
     *
     * @param alarmId alarm ID to remove subscription
     * @param subscriptionId subscription ID to remove
     * @return String to indicate status of deletion and if alarm was deleted, see {@link SubscriptionService.DeletedSubscriptionStatus}
     * @see SubscriptionService.DeletedSubscriptionStatus
     */
    public SubscriptionService.DeletedSubscriptionStatus deleteSubscription(String alarmId, String subscriptionId) {
        LOGGER.info("Delete subscription {} for alarm {}", subscriptionId, alarmId);

        if (this.seyrenRepository.deleteSubscription(alarmId, subscriptionId)) {
            Alarm alarm = seyrenRepository.getAlarm(alarmId);
            if (alarm.getSubscriptions().isEmpty()) {
                if (seyrenRepository.deleteAlarm(alarmId)) {
                    return SubscriptionService.DeletedSubscriptionStatus.ALARM_DELETED;
                }
                throw new CerebroException(ErrorCode.SEYREN_ERROR, "Delete alarm '" + alarmId + "'has fail");
            }
            return SubscriptionService.DeletedSubscriptionStatus.OK;
        }

        throw new CerebroException(ErrorCode.SUBSCRIPTION_DELETE_ERROR,
            "Delete subscription '" + subscriptionId + "' on alarm '" + alarmId + "' has fail");
    }

    public enum DeletedSubscriptionStatus {
        OK,
        ALARM_DELETED;
    }
}
