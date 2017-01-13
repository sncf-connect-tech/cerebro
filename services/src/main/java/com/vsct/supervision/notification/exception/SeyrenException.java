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

package com.vsct.supervision.notification.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import com.vsct.supervision.notification.ErrorCode;

@ResponseStatus
public class SeyrenException extends CerebroException {
    private static final long serialVersionUID = 1L;

    private final String action;
    private final int httpStatus;

    public SeyrenException(final String action, final int httpStatus) {
        super(ErrorCode.SEYREN_ERROR, "Method: " + action);
        this.action = action;
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getAction() {
        return action;
    }
}
