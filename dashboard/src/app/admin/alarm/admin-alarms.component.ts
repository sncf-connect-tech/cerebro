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
import {Router} from "@angular/router";
import {Alarm} from "../../alarm/alarm";
import {AlarmService} from "../../alarm/alarm.service";
import {ErrorMappingService} from "../../common/error/error-mapping.service";
import {CerebroException} from "../../common/error/cerebroException";

@Component({
  selector: 'admin-alarms',
  templateUrl: './admin-alarms.component.html',
  styleUrls: ['../admin.css']
})
export class AdminAlarmsComponent implements OnInit {

  errorMessage: string;

  alarms: Alarm[];
  nbTotalAlarm: number = 0;
  nbEnabledAlarm: number = 0;
  nbDisabledAlarm: number = 0;
  nbAlarmWithoutSub: number = 0;

  constructor(
    private alarmService: AlarmService,
    private errorMappingService: ErrorMappingService,
    private router: Router) {
  }

  ngOnInit() {
    this.alarmService.getAlarms().subscribe(
      response => {
        this.alarms = response;
        this.nbTotalAlarm = this.alarms.length;

        for (let alarm of this.alarms) {
          if (alarm.enabled) {
            this.nbEnabledAlarm++;

            // Count alarm without subscription
            let subscriptionInactive = false;
            for (let subscription of alarm.subscriptions) {
              if (subscription.enabled)  {
                subscriptionInactive = true;
              }
            }
            if (!subscriptionInactive) {
              this.nbAlarmWithoutSub++;
            }

          } else {
            this.nbDisabledAlarm++;
          }
        }
      }, error => this.errorMessage = this.errorMappingService.getMessage(error as CerebroException)
    );
  }

  gotoDetail(alarmId: String) {
    this.router.navigate(['alarms', alarmId ]);
  }
}
