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

import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {Observable} from "rxjs/Observable";
import {AppConfig} from "../app.config";
import {Alert} from "./alert";
import {Stat} from "../admin/stat";

@Injectable()
export class AlertService {
    private remoteRootUrl = this.config.get("services_url");
    private allAlertsUrl = "/alerts";
    private statNoChangeUrl = "/alerts/stats/nochanges";
    private statChangeUrl = "/alerts/stats/changes";

    constructor(
      private http: Http,
      private config: AppConfig
    ) { }

    getAlerts(): Observable<Alert[]> { // get 20 alerts
        return this.http.get(this.remoteRootUrl + this.allAlertsUrl)
            .map(response => <Alert[]> response.json())
            .catch(this.handleError);
    }

    getAlarmAlerts(id: string): Observable<Alert[]> {
        return this.http.get(this.remoteRootUrl + "/alarms/" + id + this.allAlertsUrl)
            .map(response => <Alert[]> response.json())
            // .catch(this.handleError)
            ;
    }

    getStatAlertWithNoChange(from: string): Observable<Stat[]> {
        return this.http.get(this.remoteRootUrl + this.statNoChangeUrl + "?from=" + from)
            .map(response => <Stat[]> response.json())
            .do(data => console.log(data)) // eyeball results in the console
            // .catch(this.handleError)
            ;
    }

    getStatAlertWithChange(from: string): Observable<Stat[]> {
        return this.http.get(this.remoteRootUrl + this.statChangeUrl + "?from=" + from)
            .map(response => <Stat[]> response.json())
            .do(data => console.log(data)) // eyeball results in the console
            // .catch(this.handleError)
            ;
    }

    private handleError(errorResponse: any) {
        // in a real world app, we may send the server to some remote logging
        // infrastructure
        // instead of just logging it to the console
        if (errorResponse instanceof Response) {
            return Observable.throw(errorResponse.json() || 'Server error');
        }

        return Observable.throw(errorResponse || 'Server error');
    }
}
