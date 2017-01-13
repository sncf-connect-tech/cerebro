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

package com.vsct.supervision.notification.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphiteKeyUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphiteKeyUtil.class);

    private static final Pattern GRAPHITE_KEY_PATTERN = Pattern.compile("([\\w,\\*_\\-\\{\\}]+[.])+((\\{[\\w|_,-]*\\}[.]*)*|[\\w*_-]*)[\\w*_-]*");

    private GraphiteKeyUtil() {
    }

    /**
     * Get all graphite key to compose check target without function.
     *
     * This method does not handle regular expression in Graphite target.
     * @param target a complete Graphite target
     * @return {@code Collection<String>} paths
     */
    public static Collection<String> extractGraphiteKeys(String target) {
        Collection<String> collection = new ArrayList<>();

        LOGGER.debug("Extracting keys from target: " + target);
        Matcher matcher = GRAPHITE_KEY_PATTERN.matcher(target);
        while (matcher.find()) {
            collection.add(matcher.group());
        }

        LOGGER.debug("Found " + collection.size() + " key(s): " + collection);
        return collection;
    }
}
