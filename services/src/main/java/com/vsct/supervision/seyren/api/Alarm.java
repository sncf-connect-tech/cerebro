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

package com.vsct.supervision.seyren.api;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * This class represents a graphite target that needs to be monitored.
 *
 * It stores current subscriptions
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Alarm {

    private String id;
    private String name;
    private String description;
    private String target;
    private String from;
    private String until;
    private URI graphiteBaseUrl;
    private BigDecimal warn;
    private BigDecimal error;
    private boolean enabled;
    private boolean live;
    private boolean allowNoData;
    private AlertType state;
    private Instant lastCheck;
    private List<Subscription> subscriptions = new ArrayList<Subscription>();

    public static class Alarms {
        private Collection<Alarm> values;

        public Collection<Alarm> getValues() {
            return values;
        }

        public void setValues(final Collection<Alarm> values) {
            this.values = values;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    public Alarm() {
    }

    @JsonCreator
    public Alarm(@JsonProperty("graphiteBaseUrl") String graphiteBaseUrl) {
        if (StringUtils.isNotBlank(graphiteBaseUrl)) {
            String url = graphiteBaseUrl;
            if (!graphiteBaseUrl.startsWith("http://") && !graphiteBaseUrl.startsWith("https://")) {
                url = "http://" + url;
            }

            this.graphiteBaseUrl = URI.create(url);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(final String target) {
        this.target = target;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(final String from) {
        this.from = from;
    }

    public String getUntil() {
        return until;
    }

    public void setUntil(final String until) {
        this.until = until;
    }

    public URI getGraphiteBaseUrl() {
        return graphiteBaseUrl;
    }

    public void setGraphiteBaseUrl(URI graphiteBaseUrl) {
        this.graphiteBaseUrl = graphiteBaseUrl;
    }

    public BigDecimal getWarn() {
        return warn;
    }

    public void setWarn(final BigDecimal warn) {
        this.warn = warn;
    }

    public BigDecimal getError() {
        return error;
    }

    public void setError(final BigDecimal error) {
        this.error = error;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(final boolean live) {
        this.live = live;
    }

    public boolean isAllowNoData() {
        return allowNoData;
    }

    public void setAllowNoData(final boolean allowNoData) {
        this.allowNoData = allowNoData;
    }

    public AlertType getState() {
        return state;
    }

    public void setState(final AlertType state) {
        this.state = state;
    }

    public Instant getLastCheck() {
        return lastCheck;
    }

    @JsonDeserialize(using = InstantOfEpochMilliDeserializer.class)
    public void setLastCheck(final Instant lastCheck) {
        this.lastCheck = lastCheck;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(final List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

}
