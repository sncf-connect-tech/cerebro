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

package com.vsct.supervision.notification;

public enum ErrorCode {
    CEREBRO_UNKNOWN_ERROR,
    ALARM_UNKNOWN,
    ALARM_DUPLICATE_NAME,
    ALARM_DUPLICATE_DATAS,
    ALARM_INVALID,
    ALARM_TARGET_INVALID,
    SUBSCRIPTION_UNKNOWN,
    SUBSCRIPTION_DUPLICATE,
    SUBSCRIPTION_INVALID,
    SUBSCRIPTION_UPDATE_INVALID,
    SUBSCRIPTION_DELETE_ERROR,
    SEYREN_ERROR
}
