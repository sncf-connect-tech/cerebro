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

package com.vsct.supervision.notification.controller;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.vsct.supervision.notification.exception.CerebroException;
import com.vsct.supervision.notification.log.Loggable;
import com.vsct.supervision.notification.model.AlarmMapper;
import com.vsct.supervision.notification.model.CerebroAlarm;
import com.vsct.supervision.notification.model.GraphiteSources;
import com.vsct.supervision.notification.model.Stat;
import com.vsct.supervision.notification.model.SubscriptionMapper;
import com.vsct.supervision.notification.service.AlarmService;
import com.vsct.supervision.notification.service.AlertService;
import com.vsct.supervision.notification.service.SubscriptionService;
import com.vsct.supervision.seyren.api.Alarm;
import com.vsct.supervision.seyren.api.Alert;
import com.vsct.supervision.seyren.api.Subscription;

import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT })
@RestController
@Loggable(service = "cerebro")
public class CerebroController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CerebroController.class);

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private AlertService alertService;

    @Autowired
    private GraphiteSources graphiteSources;

    @Autowired
    private AlarmMapper alarmMapper;

    @Autowired
    private SubscriptionMapper subscriptionMapper;

    //SOURCES

    @RequestMapping(value="/datasources/locations", method = RequestMethod.GET)
    @ApiResponses(value={@ApiResponse(code=200, message = "Returns the datasource backend location(s)")})
    public Collection<URI> sources() {
        return graphiteSources.getIpportsByUrl().keySet();
    }

    //ALARMS

    @RequestMapping(value="/alarms", method = RequestMethod.GET)
    @ApiResponses(value={@ApiResponse(code=200, message = "Returns the subscriber's alarms, or all alarms if no email is provided")})
    public Collection<Alarm> all(@ApiParam(name="subscriptionTarget", value="a subscriber's email") @RequestParam(name = "subscriptionTarget", required = false) final String subTarget) {
        final Collection<Alarm> alarms = alarmMapper.mapToPresentation(subTarget != null ? alarmService.searchAlarmsBySubscriptionTarget(subTarget) : alarmService.getAllAlarms());
        return alarms;
    }

   @RequestMapping(value="/alarms/{id}", method = RequestMethod.GET)
    public CerebroAlarm getAlarm(@ApiParam(name="id", value="an alarm Id", required = true) @PathVariable("id") final String id) {
        return new CerebroAlarm(alarmMapper.mapToPresentation(alarmService.getAlarm(id)));
    }

    @RequestMapping(value = "/alarms", method = RequestMethod.POST)
    @ApiResponses(value={@ApiResponse(code=200, message = "Creates a new alarm and returns its Id")})
    public String addAlarm(@ApiParam(name="alarm", value="an alarm to add", required = true) @RequestBody final Alarm alarm) {
        return alarmService.subscribeToAAlarm(alarmMapper.mapNewAlarmFormToSeyren(alarm)).getId();
    }

    @RequestMapping(value = "/alarms/{alarmId}/subscriptions/{subscriptionId}", method = RequestMethod.GET)
    @ApiResponses(value={@ApiResponse(code=200, message = "Returns the subscription with the given alarm and subscription Ids")})
    public Subscription getSubscription(@ApiParam(name="alarmId", value="an alarm Id") @PathVariable("alarmId") final String alarmId,
        @ApiParam(name = "subscriptionId", value = "a subscription Id") @PathVariable("subscriptionId") final String subscriptionId) {
        return subscriptionService.getSubscription(alarmId, subscriptionId);
    }

    @RequestMapping(value = "/alarms/search", method = RequestMethod.POST)
    @ApiResponses(value={@ApiResponse(code=200, message = "search for an alarm")})
    public Alarm searchAlarm(@ApiParam(name="alarm", value="the alarm to search") @RequestBody final Alarm alarm) {
        return alarmMapper.mapToPresentation(alarmService.searchAlarm(alarmMapper.mapNewAlarmFormToSeyren(alarm)));
    }

    @RequestMapping(value = "/alarms", method = RequestMethod.PUT)
    @ApiResponses(value={@ApiResponse(code=200, message = "Updates the alarm and returns its Id")})
    public String updateAlarm(@ApiParam(name="alarm", value="an updated alarm", required = true) @RequestBody final Alarm alarm) {
        return alarmService.updateAlarm(alarmMapper.mapUpdateAlarmFormToSeyren(alarm)).getId();
    }

    //SUBSCRIPTIONS

    @RequestMapping(value = "/alarms/{alarmId}/subscriptions", method = RequestMethod.POST)
    @ApiResponses(value={@ApiResponse(code=200, message = "Adds a subscription to an alarm")})
    public void addSubscription(@ApiParam(name="subscription", value="a subscription to add") @RequestBody final Subscription subscription,
                                @ApiParam(name="alarmId", value="the Id of the alarm") @PathVariable final String alarmId) {
        subscriptionService.addSubscription(subscriptionMapper.mapNewSubscriptionFormToSeyren(subscription), alarmId);
    }

    @RequestMapping(value = "/alarms/{alarmId}/subscriptions/{subscriptionId}", method = RequestMethod.DELETE)
    @ApiResponses(value={@ApiResponse(code=200, message = "Deletes the subscription with the given alarm and subscription Ids")})
    public String deleteSubscription(@ApiParam(name="alarmId", value="an alarm Id") @PathVariable("alarmId") final String alarmId,
        @ApiParam(name = "subscriptionId", value = "a subscription Id") @PathVariable("subscriptionId") final String subscriptionId) {
        return subscriptionService.deleteSubscription(alarmId, subscriptionId).name();
    }

    @RequestMapping(value = "/alarms/{alarmId}/subscriptions/search", method = RequestMethod.POST)
    @ApiResponses(value={@ApiResponse(code=200, message = "Search for a subscription")})
    public Subscription searchSubscription(@ApiParam(name="subscription", value="the subscription to search for") @RequestBody final Subscription subscription,
                                           @ApiParam(name="alarmId") @PathVariable final String alarmId) {
        return subscriptionService.searchSubscription(subscription, alarmId);
    }

    @RequestMapping(value = "/alarms/{alarmId}/subscriptions/{subscriptionId}", method = RequestMethod.PUT)
    @ApiResponses(value={@ApiResponse(code=200, message = "Updates a subscription")})
    public void updateSubscription(@ApiParam(name="subscription", value="the updated subscription") @RequestBody final Subscription subscription,
                                   @ApiParam(name="alarmId", value="the Id of the alarm to which the subscription is linked") @PathVariable("alarmId") final String alarmId) {
        subscriptionService.updateSubscription(subscriptionMapper.mapUpdateSubscriptionFormToSeyren(subscription), alarmId);
    }

    //ALERTS

    @RequestMapping(value="/alerts", method = RequestMethod.GET)
    @ApiResponses(value={@ApiResponse(code=200, message = "Returns the latest alarm alerts")})
    public Collection<Alert> getAlerts(@ApiParam(name="nbItem", value="number of alerts to return") @RequestParam(value = "items", required = false, defaultValue = "20") final int nbItem,
        							   @ApiParam(name="start", value="starting date") @RequestParam(value = "start", required = false, defaultValue = "0") final int start,
        							   @ApiParam(name="from") @RequestParam(value = "from", required = false) final String from) {
        
        Collection<Alert> alerts;
        if (from != null) {
            alerts = alertService.getAlerts(from);
        } else {
            alerts = alertService.getAlerts(nbItem, start).getValues();
        }

        return alerts;
    }

    @RequestMapping(value="/alerts/stats/nochanges", method = RequestMethod.GET)
    @ApiResponses(value={@ApiResponse(code=200, message = "Returns stats (number of occurrence,current status) about all alerts without status change from \"from\" to now")})
    public List<Stat> getAlertStatNoTypeChange(@ApiParam(name="from", value="beginning date") @RequestParam("from") final String from) {
        final List<Stat> stat = alertService.getStatNoTypeChange(from);
        return stat;
    }

    @RequestMapping("/alerts/stats/changes")
    @ApiResponses(value={@ApiResponse(code=200, message = "Returns stats (number of occurrence, current status) about all alerts with status change from \"from\" to now")})
    public List<Stat> getAlertStatTypeChange(@RequestParam("from") final String from) {
        final List<Stat> stat = alertService.getStatTypeChange(from);
        return stat;
    }

    @RequestMapping(value="/alarms/{id}/alerts", method = RequestMethod.GET)
    @ApiResponses(value={@ApiResponse(code=200, message = "Returns all alerts for a given alarm")})
    public Collection<Alert> getAlarmAlerts(@ApiParam(name="id", value="an alarm Id") @PathVariable("id") final String id) {
        final Collection<Alert> alerts = alertService.getAlarmAlerts(id).getValues();
        return alerts;
    }

    //ERRORS

    @ExceptionHandler(CerebroException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public String getErrorCode(final CerebroException exception) throws IOException {
        LOGGER.error("Service error", exception);
        ObjectMapper objectMapper = new ObjectMapper();
        FilterProvider filterProvider = new SimpleFilterProvider().addFilter("responseFilter",
            SimpleBeanPropertyFilter.filterOutAllExcept("errorCode", "errorMessage"));
        objectMapper.setFilterProvider(filterProvider);
        return objectMapper.writeValueAsString(exception);
    }
}
