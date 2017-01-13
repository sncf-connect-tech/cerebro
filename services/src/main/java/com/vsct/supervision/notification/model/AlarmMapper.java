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

import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vsct.supervision.notification.exception.CerebroException;
import com.vsct.supervision.seyren.api.Alarm;

@Component
public class AlarmMapper {
    public static final String DEFAULT_FROM = "-25min";
    public static final String DEFAULT_UNTIL = "-1min";

    @Autowired
    private GraphiteSources graphiteSources;

    @Autowired
    private SubscriptionMapper subscriptionMapper;

    private AlarmValidator alarmValidator = new AlarmValidator();

    public Alarm.Alarms mapToPresentation(Alarm.Alarms alarms) {
        mapToPresentation(alarms.getValues());
        return alarms;
    }

    public Collection<Alarm> mapToPresentation(final Collection<Alarm> alarms) {
        alarms.forEach(this::mapToPresentation);
        return alarms;
    }

    public Alarm mapToPresentation(Alarm alarm) {
        if (alarm != null) {
            alarm.setGraphiteBaseUrl(graphiteSources.getUrlsByIpport().get(alarm.getGraphiteBaseUrl()));
        }
        return alarm;
    }

    /**
     * Validate and/or force field values in a new alarm form.
     *
     * @param newAlarm alarm to validate
     * @throws CerebroException
     */
    public Alarm mapNewAlarmFormToSeyren(Alarm newAlarm) throws CerebroException {

        // Validate required fields
        alarmValidator.validateAlarm(newAlarm);

        // Reset unwanted fields or enforced default values
        newAlarm.setId(null);
        newAlarm.setLastCheck(null);

        if (newAlarm.getFrom() == null || newAlarm.getFrom().isEmpty()) {
            newAlarm.setFrom(DEFAULT_FROM);
        }

        if (newAlarm.getUntil() == null || newAlarm.getUntil().isEmpty()) {
            newAlarm.setUntil(DEFAULT_UNTIL);
        }

        updateSourcesToBackend(newAlarm);

        initListSubscription(newAlarm);

        return newAlarm;
    }

    /**
     * Validate and/or force field values to update alarm form.
     *
     * @param updatedAlarm to validate
     * @throws CerebroException
     */
    public Alarm mapUpdateAlarmFormToSeyren(final Alarm updatedAlarm) throws CerebroException {
        alarmValidator.validateAlarm(updatedAlarm);
        updateSourcesToBackend(updatedAlarm);

        updatedAlarm.getSubscriptions().forEach(subscription -> subscriptionMapper.mapUpdateSubscriptionFormToSeyren(subscription));

        return updatedAlarm;
    }

    /**
     * For each subscription, set ID to null
     *
     * @param newAlarm alarm to verify subscription
     */
    private void initListSubscription(final Alarm newAlarm) {
        newAlarm.getSubscriptions().forEach(subscription -> subscriptionMapper.mapNewSubscriptionFormToSeyren(subscription));
    }

    private void updateSourcesToBackend(final Alarm alarm) {
        URI graphiteBaseUrl = alarm.getGraphiteBaseUrl();
        if (graphiteBaseUrl == null){
            List<GraphiteSources.GraphiteSource> sources = graphiteSources.getSources();
            if (sources != null && sources.size() > 0) {
                Collection<URI> ipports = graphiteSources.getIpportsByUrl().values();
                // Reset to first IP-Port or URL by default
                alarm.setGraphiteBaseUrl(ipports.size() > 0 ? ipports.iterator().next() : sources.get(0).getUrl());
            }
        } else {
            if (graphiteSources.getIpportsByUrl().containsKey(graphiteBaseUrl)) {
                // Reset to mapped IP-Port
                alarm.setGraphiteBaseUrl(graphiteSources.getIpportsByUrl().get(graphiteBaseUrl));
            }
        }
    }
}
