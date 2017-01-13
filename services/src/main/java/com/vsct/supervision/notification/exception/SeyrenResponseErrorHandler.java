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

import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import com.google.common.io.CharStreams;
import com.vsct.supervision.notification.ErrorCode;
import com.vsct.supervision.notification.log.Loggable;

@Loggable(service="SeyrenResponseErrorHandler")
public class SeyrenResponseErrorHandler extends DefaultResponseErrorHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SeyrenResponseErrorHandler.class);

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        String seyrenResponseBody;
        LOGGER.debug("Response : {} {}", response.getStatusCode(), response.getStatusText());
        if (response.getBody() != null) {
            seyrenResponseBody = CharStreams.toString(new InputStreamReader(response.getBody(), "UTF-8"));
        } else {
            seyrenResponseBody = "Response whithout body";
        }
        CerebroException exception = new CerebroException(ErrorCode.SEYREN_ERROR, seyrenResponseBody);
        throw exception;
    }
}
