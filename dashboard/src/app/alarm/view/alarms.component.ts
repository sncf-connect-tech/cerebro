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

import {Component, OnInit, Input} from "@angular/core";
import {Router} from "@angular/router";
import {Observable} from "rxjs/Observable";
import {PaginationInstance} from "ngx-pagination";
import {Alarm} from "../alarm";
import {Alert} from "../../alert/alert";
import {AlarmService} from "../alarm.service";
import {AlertService} from "../../alert/alert.service";

@Component({
    selector: 'alarms',
    templateUrl: './alarms.component.html',
    styleUrls: ['./alarms.component.css', '../../alert/alert.css']
})
export class AlarmsComponent implements OnInit {
    errorMessage: string;
    alarms: Alarm[];
    _alarmsFiltered: Alarm[] = [];
    alerts: Alert[];
    _alertsFiltered: Alert[] = [];
    alarmNames: string[] = []; // Needed for alert table to show alarm name (alert contains alarm's ID)
    showDisabledAlarms: boolean = false;
    showAlertNoChangeStatus: boolean = false;

    public config: PaginationInstance = {
        id: 'custom',
        itemsPerPage: 10,
        currentPage: 1
    };

    constructor(
        private router: Router,
        private alarmService: AlarmService,
        private alertService: AlertService) { }

    ngOnInit() {
        this.loadAlarms();
        this.loadAlerts();
    }

    loadAlarms() {
        this.alarmService.getAlarms().subscribe(
            data => {
                this.alarms = data;
                this._alarmsFiltered = data;

                for (let alarm of data) {
                    this.alarmNames[alarm.id] = alarm.name;
                }
            }
        );
    }

    loadAlerts() {
        this.alertService.getAlerts().subscribe(
            data => {
                this.alerts = data;
                this._alertsFiltered = data;
            }
        );
    }

    get alarmsFiltered() {
        return this._alarmsFiltered.filter(alarm => this.showDisabledAlarms || alarm.enabled);
    }

    get alertsFiltered() {
        return this._alertsFiltered.filter(alert => this.showAlertNoChangeStatus || alert.toType !== alert.fromType);
    }

    gotoDetail(alarmId: String) {
        this.router.navigate(['alarms', alarmId]);
    }

    onFilterChanged(event: any) {
        let filter = event; // contains target value
        filter = filter ? filter.toLocaleLowerCase() : null;
        this.onFilterAlarm(filter);
    }

    private onFilterAlarm(filter: any) {
        this._alarmsFiltered = filter ? this.alarms.filter((alarm: Alarm) =>
            JSON.stringify(alarm).toLocaleLowerCase().indexOf(filter) !== -1) : this.alarms;
    }
}
