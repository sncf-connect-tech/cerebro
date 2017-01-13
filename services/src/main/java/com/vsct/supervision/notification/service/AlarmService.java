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

import java.math.BigDecimal;
import java.net.URI;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vsct.supervision.notification.ErrorCode;
import com.vsct.supervision.notification.email.NotificationHandler;
import com.vsct.supervision.notification.exception.CerebroException;
import com.vsct.supervision.notification.exception.DuplicateSubscriptionException;
import com.vsct.supervision.notification.repository.SeyrenRepository;
import com.vsct.supervision.seyren.api.Alarm;
import com.vsct.supervision.seyren.api.Subscription;

@Service
public class AlarmService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmService.class);

    @Autowired
    private SeyrenRepository seyrenRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private NotificationHandler notificationHandler;


    /**
     * Get a alarm by this ID.
     *
     * <i>This method do nothing more than calling {@link SeyrenRepository#getAlarm(String)}</i>
     *
     * @param id Alarm ID to recover
     * @return Alarm recovered
     * @see SeyrenRepository#getAlarm(String)
     */
    public Alarm getAlarm(String id) {
        return seyrenRepository.getAlarm(id);
    }

    /**
     * Get all alarms from Seyren.
     *
     * <i>This method do nothing more than calling {@link SeyrenRepository#getAllAlarms()}</i>
     *
     * @return All alarm in Seyren
     */
    public Collection<Alarm> getAllAlarms() {
        return seyrenRepository.getAllAlarms().getValues();
    }

    /**
     * Add a subscription to a alarm.
     *
     * If alarm not exist, create it. Otherwise, add subscription to existing alarm.
     * If subscription already exist, do nothing.
     *
     * @param alarm Alarm to create or add subscription
     * @return The alarm created/updated
     * @throws CerebroException
     */
    public Alarm subscribeToAAlarm(final Alarm alarm) {
        LOGGER.debug("Search identical alarm to: " + alarm);
        Alarm identicalAlarm = this.searchAlarm(alarm);

        if (identicalAlarm != null) {
            // Alarm already exist, enable it if needed and manage subscription
            LOGGER.debug("Identical alarm found ({}).", identicalAlarm.getId());

            // Enable alarm if it's not
            activateAlarm(identicalAlarm);

            Subscription subscriptionToAdd = alarm.getSubscriptions().iterator().next();

            // Adding subscription
            try {
                subscriptionService.addSubscription(subscriptionToAdd, identicalAlarm.getId());
                return identicalAlarm;
            } catch (DuplicateSubscriptionException exception) {
                LOGGER.debug("Adding subscription not possible, identical subscription found for alarm {}.", identicalAlarm.getId());
                return identicalAlarm;
            }
        } else {
            // Error if alarm name already exists
            Alarm alarmByName = this.searchAlarmByName(alarm.getName());

            if (alarmByName != null) {
                throw new CerebroException(ErrorCode.ALARM_DUPLICATE_NAME, "A alarm with name '" + alarm.getName() + "' already exist");
            }

            seyrenRepository.addAlarm(alarm);
            return this.searchAlarmByName(alarm.getName());
        }
    }

    /**
     * If a alarm is disable, enable it.
     *
     * @param alarm The alarm to enable
     */
    private void activateAlarm(final Alarm alarm) {
        if (alarm != null && !alarm.isEnabled()) {
            LOGGER.debug("Activating alarm {}", alarm.getId());
            alarm.setEnabled(true);
            seyrenRepository.updateAlarm(alarm);
        }
    }

    /**
     * Search a alarm by is name.
     *
     * <i>Seyren can't store multiple alarms with the same name.</i>
     *
     * @param name Name of the alarm to find
     * @return The alarm found
     */
    private Alarm searchAlarmByName(final String name) {
        Alarm.Alarms alarms = seyrenRepository.getAllAlarms();

        return alarms.getValues().stream().filter(c -> c.getName().equals(name)).findAny().orElse(null);
    }

    private Collection<Alarm> searchAlarmsBySourceTargetAndThresholds(final URI source, final String target, final BigDecimal warn,
        final BigDecimal error) {

        return getAllAlarms().stream()
            .filter(alarm -> Objects.equals(alarm.getGraphiteBaseUrl(), source) && Objects.equals(alarm.getTarget(), target)
                && Objects.equals(alarm.getWarn(), warn) && Objects.equals(alarm.getError(), error))
            .collect(Collectors.toList());
    }

    /**
     * Search a alarm with same Graphite source, Graphite target, warn and error threshold.
     *
     * Actually, take each alarms with same properties, and return the first.
     *
     * @param alarm The alarm to search
     * @return Alarm found
     */
    public Alarm searchAlarm(final Alarm alarm) {

        Collection<Alarm> identicalAlarms =
            searchAlarmsBySourceTargetAndThresholds(alarm.getGraphiteBaseUrl(), alarm.getTarget(), alarm.getWarn(), alarm.getError());

        if (!identicalAlarms.isEmpty()) {
            return identicalAlarms.iterator().next();
        }

        return null;
    }

    /**
     * Search all the alarms who have least once subscription with needed target
     * 
     * @param subTarget
     * @return
     */
    public Collection<Alarm> searchAlarmsBySubscriptionTarget(String subTarget) {
        return getAllAlarms().stream().filter(alarm -> alarm.getSubscriptions().stream().anyMatch(sub -> subTarget.equals(sub.getTarget())))
            .collect(Collectors.toList());
    }

    /**
     * Update a alarm.
     *
     * If alarm not exist, throw a CerebroException.
     *
     * @param alarm Alarm to update
     * @return The alarm updated
     */
    public Alarm updateAlarm(final Alarm alarm) {
        LOGGER.debug("Update alarm to: " + alarm);

        validateUpdatable(alarm);

        try {
            seyrenRepository.updateAlarm(alarm);

            if(alarm.isEnabled()) {
                notificationHandler.sendAlarmHasBeenModified(alarm);
            } else{
                notificationHandler.sendAlarmHasBeenDeactivated(alarm);
            }

            return this.searchAlarmByName(alarm.getName());
        } catch (RuntimeException exception) {
            LOGGER.error("Error updating alarm", exception);
            throw new CerebroException(ErrorCode.CEREBRO_UNKNOWN_ERROR, "Error updating alarm", exception);
        }
    }

    private void validateUpdatable(Alarm alarm) {

        Alarm identicalAlarm = getAlarm(alarm.getId());

        // Error if alarm name already exists
        Alarm alarmByName = this.searchAlarmByName(alarm.getName());

        if (alarmByName != null && identicalAlarm != null && !alarmByName.getId().equals(alarm.getId())) {
            throw new CerebroException(ErrorCode.ALARM_DUPLICATE_NAME, "A alarm with name '" + alarm.getName() + "' already exist");
        }

        Collection<Alarm> identicalAlarms =
            searchAlarmsBySourceTargetAndThresholds(alarm.getGraphiteBaseUrl(), alarm.getTarget(), alarm.getWarn(), alarm.getError());

        for (Alarm alarm2 : identicalAlarms) {
            if (!alarm.getId().equals(alarm2.getId())) {
                throw new CerebroException(ErrorCode.ALARM_DUPLICATE_DATAS, "A alarm with same data already exist: " + alarm2.getId());
            }
        }
    }
}
