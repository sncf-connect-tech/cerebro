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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vsct.supervision.notification.model.Stat;
import com.vsct.supervision.notification.repository.SeyrenRepository;
import com.vsct.supervision.seyren.api.Alert;
import com.vsct.supervision.seyren.api.Alert.Alerts;

@Service
public class AlertService {
    public static final int DEFAULT_NB_ITEMS_ALERTS = 20;
    private static final int NB_ITEMS_GET_FOR_ALERTS = 50;

    @Autowired
    private SeyrenRepository seyrenRepository;

    /**
     * Get 20 first alerts from Seyren.
     *
     * @return Alert in Seyren
     * @see SeyrenRepository#getAlerts(int, int)
     */
    public Alert.Alerts getAlerts() {
        return getAlerts(DEFAULT_NB_ITEMS_ALERTS, 0);
    }

    /**
     * Get some alerts from Seyren.
     *
     * @param nbItem number of alert to retrieve
     * @return All alarm in Seyren
     * @see SeyrenRepository#getAlarmAlerts(String, int, int)
     */
    public Alert.Alerts getAlerts(final int nbItem) {
        return getAlerts(nbItem, 0);
    }

    /**
     * Get some alerts from Seyren.
     *
     * <i>This method do nothing more than calling
     * {@link SeyrenRepository#getAlerts(int, int)}</i>
     *
     * @param nbItem number of alert to retrieve
     * @param start index of the fist traceResult
     * @return Alert in Seyren
     */
    public Alert.Alerts getAlerts(final int nbItem, final int start) {
        return seyrenRepository.getAlerts(start, nbItem);
    }

    /**
     * List alerts with no change.
     *
     * @param from time window to check alerts without change
     * @return A stat object with each alarm ID and number of "no status change" in the desired time windows
     */
    public List<Stat> getStatNoTypeChange(final String from) {
        final Collection<Alert> alerts = this.getAlerts(from);

        final Stat.StatBuilder stat = new Stat.StatBuilder();

        for (final Alert a : alerts) {
            if (a.getFromType() == a.getToType()) {
                stat.increment(a.getAlarmId(), a.getToType());
            } else {
                stat.removeAndIgnore(a.getAlarmId());
            }
        }

        return stat.build();
    }

    /**
     * List alerts with change.
     *
     * @param from time window to check alerts with change
     * @return A stat object with each alarm ID and number of status change in the desired time windows
     */
    public List<Stat> getStatTypeChange(final String from) {
        final Collection<Alert> alerts = this.getAlerts(from);

        final Stat.StatBuilder stat = new Stat.StatBuilder();

        for (final Alert a : alerts) {
            if (a.getFromType() != a.getToType()) {
                stat.increment(a.getAlarmId(), a.getToType());
            }
        }

        return stat.build();
    }

    /**
     * Get a precise history of alerts from Seyren.
     *
     * Use a "from" parameter e.g. "1d," "1h", etc.
     *
     * @param from from parameter to retrieve an history of a certain duration
     * @return Alert of last hour, day, etc.
     * @see AlertService#calcFromInstant(String)
     */
    public Collection<Alert> getAlerts(final String from) {
        final Instant limit = calcFromInstant(from);
        boolean limitReached = false;
        final Collection<Alert> result = new ArrayList<>();

        int i = 0;

        while (!limitReached && limit!=null) {
            final Alert.Alerts alerts = getAlerts(NB_ITEMS_GET_FOR_ALERTS, i++ * NB_ITEMS_GET_FOR_ALERTS);
            limitReached = olderThan(result, limit, alerts);
        }

        return result;
    }

    /**
     * Get all alerts from alarms in seyren.
     *
     * <i>This method do nothing more than calling
     * {@link SeyrenRepository#getAlarmAlerts(String, int, int)}</i>
     *
     * @param id
     * Alarm ID to recover
     * @return All alerts for this alarm with the key ID in Seyren
     * @see SeyrenRepository#getAlarmAlerts(String, int, int)
     */
    public Alert.Alerts getAlarmAlerts(final String id) {
        return seyrenRepository.getAlarmAlerts(id, 0, DEFAULT_NB_ITEMS_ALERTS);
    }

    /**
     * Add in collection all alerts older than a limit and return is the limit is reached.
     *
     * @param result [<b>this collection is edited in the method</b>] collection to store alerts older than limit
     * @param limit the limit to reach
     * @param alerts bucket of alerts to alarm
     * @return true if one alert in bucket if older than limit, false otherwise
     */
    private boolean olderThan(final Collection<Alert> result, final Instant limit, final Alerts alerts) {
        for (final Alert a : alerts.getValues()) {
            if (a.getTimestamp().isBefore(limit)) {
                return true;
            }
            result.add(a);
        }
        return false;
    }

    /**
     * Method to calc a from Instant with a e.g. "1d", "5h", etc.
     *
     * Can compute unit:
     * - m
     * - h
     * - d
     *
     * @param from from parameter with unit
     * @return An Instant, e.g. now minus 2 day with a from parameter = "2d"
     */
    private Instant calcFromInstant(final String from) {
        Instant result = null;

        if (from.contains("m")) {
            final int nb = Integer.valueOf(from.replace("m", ""));
            result = Instant.now().minus(nb, ChronoUnit.MINUTES);
        }

        if (from.contains("d")) {
            final int nb = Integer.valueOf(from.replace("d", ""));
            result = Instant.now().minus(nb, ChronoUnit.DAYS);
        }

        if (from.contains("h")) {
            final int nb = Integer.valueOf(from.replace("h", ""));
            result = Instant.now().minus(nb, ChronoUnit.HOURS);
        }

        return result;
    }
}
