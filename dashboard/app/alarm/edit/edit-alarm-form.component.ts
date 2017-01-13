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
import {Alarm} from "../alarm";
import {AlarmService} from "../alarm.service";
import {ErrorMappingService} from "../../common/error/error-mapping.service";
import {CerebroException} from "../../common/error/cerebroException";
import {AppConfig} from "../../app.config";
import {DatasourceService} from "../../common/datasource.service";

@Component({
    selector: 'edit-alarm-form',
    templateUrl: 'app/alarm/edit/edit-alarm-form.component.html'
})
export class EditAlarmFormComponent implements OnInit {

    @Input()
    private disabled: boolean;

    private _alarm: Alarm;

    @Input()
    set alarm(anAlarm: Alarm) {
        this._alarm = Object.assign({}, anAlarm);
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
    allowNoData: boolean;

    constructor(
        private alarmService: AlarmService,
        private datasourceService: DatasourceService,
        private errorMappingService: ErrorMappingService,
        private config: AppConfig
      ) {
    }

    initSources() {
        this.datasourceService.getLocations().subscribe(
          response => this.sources = response
        );
    }

    saveAlarm() {
        this.alarmService.updateAlarm(this.alarm).subscribe(
            response => this.onUpdate.emit(true),
            error => this.errorMessage = this.errorMappingService.getMessage(error as CerebroException));
    }

    ngOnInit() {
        this.initSources();

        this.allowNoData = this.alarm.allowNoData;
    }

    onSubmitStep1() {
        this.submitted = true;
        this.alarm.allowNoData = this.allowNoData;
        // Update keepLastValue in alarm.target if not present
        if (!this.keepLastValueRegExp.test(this.alarm.target)) {
            this.alarm.target = "keepLastValue(" + this.alarm.target + ")";
        }
    }

    graphMiniUrl(period: String, title: String) {
        return this.alarm.graphiteBaseUrl + (this.alarm.graphiteBaseUrl.endsWith("/") ? "" : "/") + "render?from=-" + period + "&target=alias(" + this.alarm.target
            + ",'Target')&target=alias(color(constantLine(" + this.alarm.warn + "),'orange'),'Warning')&target=alias(color(constantLine("
            + this.alarm.error + "),'red'),'Error')&title=" + title;
    }

    graphMaxiUrl(period: String, title: String) {
        return this.graphMiniUrl(period, title) + "&width=800&height=600";
    }
}
