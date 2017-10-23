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

import {Component, EventEmitter, Input, Output, OnInit} from "@angular/core";
import {Router} from "@angular/router";
import { Observable } from 'rxjs/Observable';
import { Alarm }  from '../alarm';
import {Subscription} from "../../subscription/subscription";
import {AlarmService} from "../alarm.service";
import {ErrorMappingService} from "../../common/error/error-mapping.service";
import {AuthService} from "../../common/auth/basic.auth.service";
import {Profile} from "../../common/auth/profile";
import {CerebroException} from "../../common/error/cerebroException";
import {AppConfig} from "../../app.config";
import {DatasourceService} from "../../common/datasource.service";

let options = {
  toastLife: 5000,
  positionClass: "toast-top-full-width",
  autoDismiss: true
};

@Component({
    selector: 'copy-alarm-form',
    templateUrl: './copy-alarm-form.component.html'
})
export class CopyAlarmFormComponent implements OnInit {

    private _alarm: Alarm;
    private allowNoData: boolean;

    defaultSliderConfig: any = {
        start: [ new Date(0,0,0,8,0,0,0).getTime(), new Date(0,0,0,20,0,0,0).getTime() ],
        step: 600000,
        connect: true,
        range: {
            min: new Date(0,0,0,0,0,0,0).getTime(),
            max: new Date(0,0,0,23,59,0,0).getTime()
        }
    };

    @Input()
    set alarm(anAlarm: Alarm) {
        let profile: Profile = this.authService.getProfile();

        this._alarm = Object.assign({}, anAlarm);
        this._alarm.subscriptions = null;
        this._alarm.lastCheck = 0;
        this._alarm.subscriptions = new Array<Subscription>();
        this._alarm.subscriptions.push(new Subscription(null, profile.email, this.subscriptionTypes[0], false, true, true, true, true, true, false, false, false, false, false, "0800", "2000", true));
    }
    get alarm() {
        return this._alarm;
    }

    @Output()
    onUpdate = new EventEmitter<boolean>();

    keepLastValueRegExp = new RegExp("^keepLastValue\(.*\)$");
    errorMessage: string;
    sources: string[];
    submitted = false;
    step: number;
    subscriptionTypes = ['EMAIL'];

    constructor(
        private router: Router,
        private alarmService: AlarmService,
        private errorMappingService: ErrorMappingService,
        private authService: AuthService,
        private datasourceService: DatasourceService,
        private config: AppConfig
      ){
    }

    initSources() {
        this.datasourceService.getLocations().subscribe(
            response => this.sources = response
        );
    }

    saveAlarm() {
        // Keep original ID to refresh (emit event) if necessary
        let fromAlarmId = this.alarm.id;
        this.alarm.id = null;
        this.alarm.allowNoData = this.allowNoData;

        this.alarmService.addAlarm(this.alarm).subscribe(
            alarmId => {
                // If the modified Alarm is the current one, it is necessary to refresh (Angular router doesn't since URL doesn't change)
                if (fromAlarmId === alarmId){
                    this.onUpdate.emit(true);
                } else {
                    this.router.navigate(['alarms', alarmId]);
                }
            },
            error => this.errorMessage = this.errorMappingService.getMessage(error as CerebroException));
    }

    onDismiss(){
        // Reset modal
        this.submitted=false;
        this.step=1;
        // Show toaster
        if(this.errorMessage != null){
            // Reset message
            this.errorMessage = null;
        }
    }

    onSliderChange($event) {
        let fromDate = new Date(parseInt($event[0]));
        var fromHour = (fromDate.getHours().toString().length < 2 ? "0" + fromDate.getHours() : fromDate.getHours());
        var fromMin = (fromDate.getMinutes().toString().length < 2 ? "0" + fromDate.getMinutes() : fromDate.getMinutes());
        this.alarm.subscriptions[0].fromTime = "" + fromHour + fromMin;

        let toDate = new Date(parseInt($event[1]));
        var toHour = (toDate.getHours().toString().length < 2 ? "0" + toDate.getHours() : toDate.getHours());
        var toMin = (toDate.getMinutes().toString().length < 2 ? "0" + toDate.getMinutes() : toDate.getMinutes());
        this.alarm.subscriptions[0].toTime = "" + toHour + toMin;
    }

    ngOnInit() {
        this.initSources();

        this.allowNoData = this.alarm.allowNoData;

        this.alarm.name = this.alarm.name + " [COPIE]"
        this.step = 1;
    }

    graphMiniUrl(period: String, title: String) {
        return this.alarm.graphiteBaseUrl + (this.alarm.graphiteBaseUrl.endsWith("/") ? "" : "/") + "render?from=-" + period + "&target=alias(" + this.alarm.target
            + ",'Target')&target=alias(color(constantLine(" + this.alarm.warn + "),'orange'),'Warning')&target=alias(color(constantLine("
            + this.alarm.error + "),'red'),'Error')&title=" + title;
    }

    graphMaxiUrl(period: String, title: String) {
        return this.graphMiniUrl(period, title) + "&width=800&height=600";
    }

    isActive(day: string) {
        return this.alarm.subscriptions[0][day];
    }

    switchActive(day: string) {
        this.alarm.subscriptions[0][day] = !this.alarm.subscriptions[0][day];
    }

    onSubmitStep1() {
        this.step = 2;

        // Update keepLastValue in alarm.target if not present
        if (!this.keepLastValueRegExp.test(this.alarm.target)) {
            this.alarm.target = "keepLastValue(" + this.alarm.target + ")";
        }
    }

    onSubmitStep2() {
        this.step = 3;
        this.submitted = true;
    }

    isIgnoreStatus(subscriptionProperty: string) {
        return this.alarm.subscriptions[0][subscriptionProperty];
    }

    switchIgnoreStatus(subscriptionProperty: string) {
        this.alarm.subscriptions[0][subscriptionProperty] = !this.alarm.subscriptions[0][subscriptionProperty];
    }

    retrieveSliderTime() {
        var beginTime = document.getElementById('slider-time-value-min').innerHTML;
        beginTime = beginTime.replace("h", "");
        var endTime = document.getElementById('slider-time-value-max').innerHTML;
        endTime = endTime.replace("h", "");

        this.alarm.subscriptions[0].fromTime = beginTime;
        this.alarm.subscriptions[0].toTime = endTime;
    }
}
