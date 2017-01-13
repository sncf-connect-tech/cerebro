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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vsct.supervision.seyren.api.Alarm;
import com.vsct.supervision.seyren.api.AlertType;

/**
 * An object to store stat about alerts of an Alarm.
 *
 * Stat indicate how many time an Alarm have an alert with a type.
 *
 * You can build a list of stat with {@link StatBuilder}: this builder permit to list each alarm with no status change.
 *
 * @see StatBuilder
 */
public class Stat {
    private String alarmId;
    private int count;
    private AlertType type;

    /**
     * Builder to list each alarm with no change in alert status
     */
    public static class StatBuilder {
        private final List<String> ignoredId = new ArrayList<>();
        private Map<String, Stat> alarmIdStat = new HashMap<>();

        /**
         * Increment the count of alerts with no change.
         *
         * If alarm is ignored, do nothing.
         *
         * @param alarmId ID of alarm with an alerts without change
         * @param type The type of the alert.
         */
        public void increment(final String alarmId, final AlertType type) {
            if (!ignoredId.contains(alarmId)) {
                if (alarmIdStat.containsKey(alarmId)) {
                    alarmIdStat.get(alarmId).increment();
                } else {
                    alarmIdStat.put(alarmId, new Stat(alarmId, type));
                }
            }
        }

        /**
         * Remove count for a alarm and ignore it for the future
         *
         * @param alarmId ID of alarm to ignore
         */
        public void removeAndIgnore(final String alarmId) {
            alarmIdStat.remove(alarmId);
            ignoredId.add(alarmId);
        }

        /**
         * Return the list of all stats.
         *
         * @return List of a stat for each Alarm kept
         */
        public List<Stat> build() {
            return new ArrayList<>(alarmIdStat.values());
        }
    }

    private Stat(String alarmId, AlertType type) {
        this.alarmId = alarmId;
        this.type = type;
        this.count = 1;
    }

    private void increment() {
        count++;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public int getCount() {
        return count;
    }

    public AlertType getType() {
        return type;
    }
}
