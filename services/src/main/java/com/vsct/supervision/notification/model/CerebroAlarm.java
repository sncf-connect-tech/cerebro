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

import java.util.Collection;

import com.vsct.supervision.notification.util.GraphiteKeyUtil;
import com.vsct.supervision.seyren.api.Alarm;

public class CerebroAlarm {
    private Alarm alarm;
    private Collection<String> targetGraphiteKeys;

    public CerebroAlarm(Alarm alarm) {
        this.alarm = alarm;
        this.targetGraphiteKeys = GraphiteKeyUtil.extractGraphiteKeys(alarm.getTarget());
    }

    public Alarm getAlarm() {
        return alarm;
    }

    public Collection<String> getTargetGraphiteKeys() {
        return targetGraphiteKeys;
    }
}
