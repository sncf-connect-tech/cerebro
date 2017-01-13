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

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "graphite")
public class GraphiteSources {

    private List<GraphiteSource> sources;

    private Map<URI, URI> ipportsByUrl = new HashMap<>();
    private Map<URI, URI> urlsByIpport = new HashMap<>();

    @PostConstruct
    public void initSourceMaps() {
        for (GraphiteSource source : sources) {
            URI uri = source.getUrl();
            URI ipport = source.getIpport();
            ipportsByUrl.put(uri, ipport);
            urlsByIpport.put(ipport, uri);
        }
    }

    public List<GraphiteSource> getSources() {
        return sources;
    }

    public void setSources(List<GraphiteSource> sources) {
        this.sources = sources;
    }

    public Map<URI, URI> getIpportsByUrl() {
        return ipportsByUrl;
    }

    public Map<URI, URI> getUrlsByIpport() {
        return urlsByIpport;
    }

    public static class GraphiteSource {

        private URI url;

        private URI ipport;

        public GraphiteSource() {
        }

        public GraphiteSource(URI url) {
            this.url = url;
        }

        public GraphiteSource(URI url, URI ipport) {
            this.url = url;
            this.ipport = ipport;
        }

        public URI getUrl() {
            return url;
        }

        public void setUrl(URI url) {
            this.url = url;
        }

        public URI getIpport() {
            return ipport;
        }

        public void setIpport(URI ipport) {
            this.ipport = ipport;
        }
    }
}
