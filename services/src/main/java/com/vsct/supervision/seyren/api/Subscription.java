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

import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * This class represents something wanting to be notified of an alert
 */
public class Subscription {

    private String id;
    private String target;
    private SubscriptionType type;
    private boolean su, mo, tu, we, th, fr, sa;
    private boolean ignoreWarn, ignoreError, ignoreOk, ignoreUnknown;
    private String fromTime;
    private String toTime;
    private boolean enabled;

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

    public String getTarget() {
        return target;
    }

    public void setTarget(final String target) {
        this.target = target;
    }

    public SubscriptionType getType() {
        return type;
    }

    public void setType(final SubscriptionType type) {
        this.type = type;
    }

    public boolean isSu() {
        return su;
    }

    public void setSu(final boolean su) {
        this.su = su;
    }

    public boolean isMo() {
        return mo;
    }

    public void setMo(final boolean mo) {
        this.mo = mo;
    }

    public boolean isTu() {
        return tu;
    }

    public void setTu(final boolean tu) {
        this.tu = tu;
    }

    public boolean isWe() {
        return we;
    }

    public void setWe(final boolean we) {
        this.we = we;
    }

    public boolean isTh() {
        return th;
    }

    public void setTh(final boolean th) {
        this.th = th;
    }

    public boolean isFr() {
        return fr;
    }

    public void setFr(final boolean fr) {
        this.fr = fr;
    }

    public boolean isSa() {
        return sa;
    }

    public void setSa(final boolean sa) {
        this.sa = sa;
    }

    public boolean isIgnoreWarn() {
        return ignoreWarn;
    }

    public void setIgnoreWarn(final boolean ignoreWarn) {
        this.ignoreWarn = ignoreWarn;
    }

    public boolean isIgnoreError() {
        return ignoreError;
    }

    public void setIgnoreError(final boolean ignoreError) {
        this.ignoreError = ignoreError;
    }

    public boolean isIgnoreOk() {
        return ignoreOk;
    }

    public void setIgnoreOk(final boolean ignoreOk) {
        this.ignoreOk = ignoreOk;
    }

    public boolean isIgnoreUnknown() { return ignoreUnknown; }

    public void setIgnoreUnknown(final boolean ignoreUnknown) { this.ignoreUnknown = ignoreUnknown; }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(final String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(final String toTime) {
        this.toTime = toTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Tests equality, "enabled" and "id" IS NOT compared.
     *
     * @param o subscription to compare to this
     * @return true if o is equal to this
     */
    @Override
    public boolean equals(Object o) {//NOSONAR
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Subscription that = (Subscription) o;
        return su == that.su &&
            mo == that.mo &&
            tu == that.tu &&
            we == that.we &&
            th == that.th &&
            fr == that.fr &&
            sa == that.sa &&
            ignoreWarn == that.ignoreWarn &&
            ignoreError == that.ignoreError &&
            ignoreOk == that.ignoreOk &&
            ignoreUnknown == that.ignoreUnknown &&
            StringUtils.equalsIgnoreCase(target, that.target) &&
            type == that.type &&
            Objects.equals(fromTime, that.fromTime) &&
            Objects.equals(toTime, that.toTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, type, su, mo, tu, we, th, fr, sa, ignoreWarn, ignoreError, ignoreOk, ignoreUnknown, fromTime, toTime);
    }
}
