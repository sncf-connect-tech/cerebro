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

import {Component, OnInit} from "@angular/core";

import {Alarm} from "../../alarm/alarm";
import {Alert} from "../../alert/alert";
import {Stat} from "../stat";
import {AlertService} from "../../alert/alert.service";
import {AlarmService} from "../../alarm/alarm.service";
import {ErrorMappingService} from "../../common/error/error-mapping.service";
import {CerebroException} from "../../common/error/cerebroException";

@Component({
  selector: 'admin-alerts',
  templateUrl: './admin-alerts.component.html',
  styleUrls: ['../admin.css']
})
export class AdminAlertsComponent implements OnInit {
  errorMessage: string;

  alertWithChangeSince2Hour: Stat[];
  alertWithoutChangeSince3Day: Stat[];
  alertWithFiveChangeSince2Hour: Stat[];
  alarmNames: string[] = [];

  constructor(
    private errorMappingService: ErrorMappingService,
    private alarmService: AlarmService,
    private alertService: AlertService) { }

  ngOnInit() {
    this.countAlertWithoutChangeSince3Day();
    this.countAlertWithChangeSince2Hour();
  }

  countAlertWithoutChangeSince3Day(): void {
    this.alertService.getStatAlertWithNoChange("3d").subscribe(
      stats => {
        this.alertWithoutChangeSince3Day = stats;

        for (let stat of stats) {
          this.loadAlarmsName(stat);
        }
      }, error => this.errorMessage = this.errorMappingService.getMessage(error as CerebroException)
    );
  }

  countAlertWithChangeSince2Hour(): void {
    this.alertService.getStatAlertWithChange("2h").subscribe(
      stats => {
        this.alertWithFiveChangeSince2Hour = [];
        this.alertWithChangeSince2Hour = stats;

        for (let stat of stats) {
          if (stat.count > 5) {
            this.alertWithFiveChangeSince2Hour.push(stat);
          }

          this.loadAlarmsName(stat);
        }
      }, error => this.errorMessage = this.errorMappingService.getMessage(error as CerebroException)
    );
  }

  loadAlarmsName(stat: Stat): void {
    this.alarmService.getAlarm(stat.alarmId).subscribe(
      alarm => this.alarmNames[stat.alarmId] = alarm.alarm.name,
      error => this.errorMessage = this.errorMappingService.getMessage(error as CerebroException)
    );
  }
}
