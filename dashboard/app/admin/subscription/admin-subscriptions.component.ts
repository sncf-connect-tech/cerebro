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
  selector: 'admin-subscriptions',
  templateUrl: 'app/admin/subscription/admin-subscriptions.component.html',
  styleUrls: ['app/admin/admin.css']
})
export class AdminSubscriptionsComponent implements OnInit {
  NB_MAX: number= 5;

  errorMessage: string;

  nbActiveSubscriber: number = 0;
  nbInactiveSubscription: number = 0;
  topAlarmsByActiveSubscribers = [];
  topSubscribersByActiveAlarms= [];
  alarmIdName = [];

  constructor(private alarmService: AlarmService,
      private errorMappingService: ErrorMappingService,
      private router: Router) {
    }

    ngOnInit() {
      this.alarmService.getAlarms().subscribe(
        alarms => {
          let mailNbSubscribe = new Map<string, number>();
          let alarmNbSubscribe = new Map<string, number>();

          for (let alarm of alarms) {
            if (alarm.enabled) {
              for (let subscription of alarm.subscriptions) {

                if (subscription.enabled) {
                  let target = subscription.target.toLowerCase();
                  let nbTargetSubscribed = mailNbSubscribe.get(target);
                  if (nbTargetSubscribed !== undefined) {
                    mailNbSubscribe.set(target, nbTargetSubscribed + 1);
                  } else {
                    mailNbSubscribe.set(target, 1);
                  }

                  let nbAlertSubscriber = alarmNbSubscribe.get(alarm.id);
                  if (nbAlertSubscriber !== undefined) {
                    alarmNbSubscribe.set(alarm.id, nbAlertSubscriber + 1);
                  } else {
                    alarmNbSubscribe.set(alarm.id, 1);
                    this.alarmIdName[alarm.id] = alarm.name;
                  }
                } else {
                  this.nbInactiveSubscription++;
                }
              }
            }
          }

          this.nbActiveSubscriber = mailNbSubscribe.size;

          this.topSubscribersByActiveAlarms = Array.from(mailNbSubscribe).sort((n1, n2) => n2[1] - n1[1]).slice(0, this.NB_MAX);
          this.topAlarmsByActiveSubscribers = Array.from(alarmNbSubscribe).sort((n1, n2) => n2[1] - n1[1]).slice(0, this.NB_MAX);

        }, error => this.errorMessage = this.errorMappingService.getMessage(error as CerebroException)
      );
    }

  gotoDetail(alarmId: String) {
    this.router.navigate(['alarms', alarmId]);
  }
}
