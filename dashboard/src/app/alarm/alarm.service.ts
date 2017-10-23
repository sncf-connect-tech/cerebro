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

import { Injectable }                                               from '@angular/core';
import { Http, Response, Headers, RequestOptions, URLSearchParams } from '@angular/http';
import { Observable }                                               from 'rxjs/Observable';

import { AppConfig } from '../app.config';

import { Alarm }            from './alarm';
import { AlarmDetail }      from './alarm-detail';
import { Subscription }     from '../subscription/subscription';
import { CerebroException } from '../common/error/cerebroException';

@Injectable()
export class AlarmService {

    private remoteRootUrl = this.config.get("services_url");    
    private alarmAddUrl = this.remoteRootUrl + "/alarms";
    private alarmSearchUrl = this.remoteRootUrl + "/alarms/search";
    private alarmUpdateUrl = this.remoteRootUrl + "/alarms";

    constructor(
      private http: Http,
      private config: AppConfig
    ) { }

    getAlarms(): Observable<Alarm[]> {
        return this.http.get(this.remoteRootUrl + "/alarms")
            .map(response => <Alarm[]> response.json())
            .catch(this.handleError);
            ;
    }

    getAlarmsBySubscriptionTarget(target: string) {
        let params = new URLSearchParams();
        params.set('subscriptionTarget', target);

        return this.http.get(this.remoteRootUrl + "/alarms?" + params)
            .map(response => <Alarm[]> response.json())
            .catch(this.handleError)
            ;
    }

    getAlarm(id: string): Observable<AlarmDetail> {
        return this.http.get(this.remoteRootUrl + "/alarms/" + id)
            .map(response => <AlarmDetail> response.json())
            .catch(this.handleError)
            ;
    }

    addAlarm(newAlarm: Alarm) {
        let body = JSON.stringify(newAlarm);
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        return this.http.post(this.alarmAddUrl, body, options)
            .map(response => <String> response.text())
            .catch(this.handleError)
            ;
    }

   updateAlarm(alarm: Alarm) {
        let body = JSON.stringify(alarm);
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        return this.http.put(this.alarmUpdateUrl, body, options)
            .map(response => <String> response.text())
            .catch(this.handleError)
            ;
    }

    addSubscription(subscription: Subscription, alarmId: string) {
      let body = JSON.stringify(subscription);
      let headers = new Headers({ 'Content-Type': 'application/json' });
      let options = new RequestOptions({ headers: headers });

      return this.http.post(this.remoteRootUrl + "/alarms/" + alarmId + "/subscriptions", body, options)
        .map(response => <String> response.text())
        .catch(this.handleError)
        ;
    }

    deleteSubscription(alarm: Alarm, subscription: Subscription) {
        return this.http.delete(this.remoteRootUrl + "/alarms/" + alarm.id + "/subscriptions/" + subscription.id)
            .map(response => response.text())
            .catch(this.handleError)
            ;
    }

    searchAlarm(alarm: Alarm): Observable<Alarm> {
        let body = JSON.stringify(alarm);
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        return this.http.post(this.alarmSearchUrl, body, options)
            .map(response => <Alarm> response.json())
            .catch(this.handleError)
            ;
    }

    searchSubscription(subscription: Subscription, alarmId: string) {
        let body = JSON.stringify(subscription);
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        return this.http.post(this.remoteRootUrl + "/alarms/" + alarmId + "/subscriptions/search", body, options)
            .map(response => <Subscription> response.json())
            .catch(this.handleError)
            ;
    }

    updateSubscription(subscription: Subscription, alarmId: string) {
      let body = JSON.stringify(subscription);
      let headers = new Headers({ 'Content-Type': 'application/json' });
      let options = new RequestOptions({ headers: headers });

      return this.http.put(this.remoteRootUrl + "/alarms/" + alarmId + "/subscriptions/" + subscription.id, body, options)
          .catch(this.handleError)
          ;
    }

    private handleError(errorResponse: any) {
        // in a real world app, we may send the server to some remote logging infrastructure
        // instead of just logging it to the console
        if (errorResponse instanceof Response) {
          return Observable.throw(errorResponse.json() as CerebroException || 'Server error');
        }

        return Observable.throw(errorResponse || 'Server error');
    }
}
