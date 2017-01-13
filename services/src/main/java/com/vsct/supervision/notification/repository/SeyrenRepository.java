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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.vsct.supervision.notification.ErrorCode;
import com.vsct.supervision.notification.exception.CerebroException;
import com.vsct.supervision.notification.exception.SeyrenException;
import com.vsct.supervision.seyren.api.Alert;
import com.vsct.supervision.seyren.api.Alarm;
import com.vsct.supervision.seyren.api.Subscription;

/**
 * A class to do REST Call to Seyren to retrieve objects.
 */
@Repository
public class SeyrenRepository {
    public static final String API_ALARMS = "/api/checks";
    public static final String API_ALERTS = "/api/alerts";
    private static final Logger LOGGER = LoggerFactory.getLogger(SeyrenRepository.class);
    @Autowired
    protected RestTemplate restTemplate;

    @Value("${seyren.host}")
    protected String seyrenUrl;

    /**
     * CONFIGURATION METHODS
     **/
    public void setRestTemplate(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setSeyrenUrl(final String seyrenUrl) {
        this.seyrenUrl = seyrenUrl;
    }

    /** ALARM PART **/

    /**
     * Get a alarm by ID.
     *
     * @param id Alarm ID to retrieve
     * @return The retrieved alarm
     * @throws CerebroException if alarm was not found
     * @throws SeyrenException if another problem occur when contacting Seyren
     */
    public Alarm getAlarm(final String id) {
        try {
            return restTemplate.getForObject(seyrenUrl + API_ALARMS + "/" + id, Alarm.class);
        } catch (final HttpStatusCodeException exception) {
            throw new SeyrenException("getAlarm", exception.getStatusCode().value());
        } catch (final ResourceAccessException exception) {
            throw new CerebroException(ErrorCode.ALARM_UNKNOWN, "Alarm with id '" + id + "' not found", exception);
        }
    }

    /**
     * Get all alarms from Seyren.
     *
     * @return All alarm in Seyren
     */
    public Alarm.Alarms getAllAlarms() {
        return restTemplate.getForObject(seyrenUrl + API_ALARMS, Alarm.Alarms.class);
    }

    /**
     * Add a new alarm in Seyren.
     *
     * @param alarm the alarm to add
     * @throws SeyrenException If response is not like 2xx (if alarm as the same name)
     */
    public void addAlarm(final Alarm alarm) {
        LOGGER.debug("Adding alarm {}", alarm);
        final ResponseEntity<Object> response = restTemplate.postForEntity(seyrenUrl + API_ALARMS, alarm, Object.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new SeyrenException("addAlarm", response.getStatusCode().value());
        }

        LOGGER.debug("Alarm '{}' created", alarm.getName());
    }

    /**
     * Update a alarm.
     *
     * @param alarm The alarm to update
     */
    public void updateAlarm(final Alarm alarm) {

        // TODO : serialize/deserialize java 8 java.time with Jackson JSON mapper
        // com.fasterxml.jackson.databind.JsonMappingException: Can not instantiate value of type [simple type, class org.joda.time.DateTime] from Floating-point number (1.467041095011E9); // NOSONAR
        // no one-double/Double-arg constructor/factory method at [Source: org.apache.catalina.connector.CoyoteInputStream@22c7305d; line:
        // 14, column: 16] (through reference chain: com.seyren.core.domain.Alarm["lastAlarm"])
        alarm.setLastCheck(null);

        LOGGER.debug("Updating alarm {}", alarm.getId());
        restTemplate.put(seyrenUrl + API_ALARMS + "/" + alarm.getId(), alarm, String.class);
    }

    /**
     * Delete a alarm.
     *
     * @param alarmId ID to alarm to delete
     */
    public boolean deleteAlarm(final String alarmId) {
        final ResponseEntity<String> response =
            restTemplate.exchange(seyrenUrl + API_ALARMS + "/" + alarmId, HttpMethod.DELETE, null, String.class);
        LOGGER.info("Delete alarm {}. Response {}", alarmId, response.getStatusCode());
        return response.getStatusCode().is2xxSuccessful();
    }

    /** SUBSCRIPTION PART **/

    /**
     * Add a new subscription to a alarm.
     *
     * @param subscription the subscription to add
     * @param alarmId the ID of the alarm to add subscription
     * @throws SeyrenException If response is not like 2xx (if alarm as the same name)
     */
    public void addSubscription(final Subscription subscription, final String alarmId) {
        final ResponseEntity<Object> response =
            restTemplate.postForEntity(seyrenUrl + API_ALARMS + "/" + alarmId + "/subscriptions/", subscription, Object.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new SeyrenException("addSubscription", response.getStatusCode().value());
        }

        LOGGER.info("Add subscription for alarm {}. Response {}", alarmId, response.getStatusCode());
    }

    /**
     * Update a subscription in Seyren (by subscription ID).
     *
     * @param subscription The subscription to update
     * @param alarmId the ID of the alarm to update subscription
     */
    public void updateSubscription(final Subscription subscription, final String alarmId) {
        restTemplate.put(seyrenUrl + API_ALARMS + "/" + alarmId + "/subscriptions/" + subscription.getId(), subscription, String.class);
        LOGGER.info("Subscription {} for Alarm {} has been updated.", subscription.getId(), alarmId);
    }

    public boolean deleteSubscription(final String alarmId, final String subscriptionId) {
        final ResponseEntity<String> response = restTemplate
            .exchange(seyrenUrl + API_ALARMS + "/" + alarmId + "/subscriptions/" + subscriptionId, HttpMethod.DELETE, null, String.class);
        LOGGER.info("Delete subscription {} for alarm {}. Response {}", subscriptionId, alarmId, response.getStatusCode());
        return response.getStatusCode().is2xxSuccessful();
    }

    /** ALERT PART **/

    /**
     * Get items alerts from Seyren.
     *
     * @param start index of the first traceResult
     * @param items number of items to return
     *
     * @return Items alerts in Seyren
     */
    public Alert.Alerts getAlerts(final int start, final int items) {
        return restTemplate.getForObject(seyrenUrl + API_ALERTS + "?start=" + start + "&items=" + items, Alert.Alerts.class);
    }

    /**
     * Get all alerts from alarm in seyren.
     *
     * @param start index of the first traceResult
     * @param items number of items to return
     *
     * @return All alerts from alarm in seyren.
     */
    public Alert.Alerts getAlarmAlerts(final String alarmId, final int start, final int items) {
        return restTemplate.getForObject(seyrenUrl + API_ALARMS + "/" + alarmId + "/alerts?start=" + start + "&items=" + items,
            Alert.Alerts.class);
    }
}
