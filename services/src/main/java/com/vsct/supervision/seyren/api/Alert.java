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
import java.time.Instant;
import java.util.Collection;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * An instance of this class represents an occurrence of a check that is found to be out of the normal range.
 * 
 * It stores some of the state of the check at the time it occurred.
 * 
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Alert {

    private String id;
    @JsonProperty("checkId")
    private String alarmId;
    private BigDecimal value;
    private String target;
    private BigDecimal warn;
    private BigDecimal error;
    private AlertType fromType;
    private AlertType toType;
    private Instant timestamp;
    private String targetHash;

    public static class Alerts {
        private Collection<Alert> values;

        private int total;

        public Collection<Alert> getValues() {
            return values;
        }

        public int getTotal() {
            return total;
        }

        public void setValues(final Collection<Alert> values) {
            this.values = values;
        }

        public void setTotal(final int total){
            this.total = total;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @JsonDeserialize(using = InstantOfEpochMilliDeserializer.class)
    public void setTimestamp(final Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(final String alarmId) {
        this.alarmId = alarmId;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(final BigDecimal value) {
        this.value = value;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(final String target) {
        this.target = target;
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

    public AlertType getFromType() {
        return fromType;
    }

    public void setFromType(final AlertType fromType) {
        this.fromType = fromType;
    }

    public AlertType getToType() {
        return toType;
    }

    public void setToType(final AlertType toType) {
        this.toType = toType;
    }

    public String getTargetHash() {
        return targetHash;
    }

    public void setTargetHash(final String targetHash) {
        this.targetHash = targetHash;
    }

}
