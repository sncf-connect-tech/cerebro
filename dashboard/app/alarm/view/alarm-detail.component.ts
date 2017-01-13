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

import {Component, OnInit, ViewChild} from "@angular/core";
import {ActivatedRoute, Router, Params} from "@angular/router";
import {ModalComponent} from "ng2-bs3-modal/ng2-bs3-modal";
import {Alarm} from "../alarm";
import {AlarmDetail} from "../alarm-detail";
import {Alert} from "../../alert/alert";
import {Subscription} from "../../subscription/subscription";
import {AlarmService} from "../alarm.service";
import {AlertService} from "../../alert/alert.service";
import {ErrorMappingService} from "../../common/error/error-mapping.service";
import {CerebroException} from "../../common/error/cerebroException";

@Component({
    selector: 'alarm-detail',
    inputs: ['alarm'],
    templateUrl: 'app/alarm/view/alarm-detail.component.html',
    styleUrls: ['app/alarm/view/alarm-detail.component.css', 'app/alert/alert.css']
})
export class AlarmDetailComponent implements OnInit {
    @ViewChild('subDeletionModal')
    subDeletionModal: ModalComponent;

    private _alerts: Alert[] = [];
    errorMessage: string;
    alarm: Alarm;
    lastCheckDate: Date;
    showAlertNoChangeStatus: boolean = false;
    targetGraphiteKeys: string[];
    subscriptionForDeletion: Subscription;
    isSubscriptionsLayoutInline: boolean = false;
    isAlertsLoaded: boolean = false;

    constructor(
        private alarmService: AlarmService,
        private alertService: AlertService,
        private route: ActivatedRoute,
        private router: Router,
        private errorMappingService : ErrorMappingService) {
    }

    ngOnInit() {
      this.route.params.forEach((params: Params) => {
          let id = params['id'];

          this.alarmService.getAlarm(id).subscribe(
              response => {
                  let alarmDetail: AlarmDetail = response;

                  this.alarm = alarmDetail.alarm;

                  let uniqueTargetGraphiteKeys = alarmDetail.targetGraphiteKeys || [];
                  uniqueTargetGraphiteKeys = uniqueTargetGraphiteKeys.filter((str1, str2, list) => list.indexOf(str1) == str2);
                  this.targetGraphiteKeys = uniqueTargetGraphiteKeys;
                  this.lastCheckDate = new Date(1000 * Math.floor(this.alarm.lastCheck));
              },
              error =>  this.errorMessage = this.errorMappingService.getMessage(error as CerebroException));

          this.alertService.getAlarmAlerts(id).subscribe(
              response => {
                  this._alerts = response;
                  this.isAlertsLoaded = true;
                },
              error =>  this.errorMessage = this.errorMappingService.getMessage(error as CerebroException));
        });
    }

    get alerts() {
        return this._alerts.filter((alert: Alert) => this.showAlertNoChangeStatus || alert.fromType != alert.toType);
    }

    confirmSubscriptionDeletion(subscription: Subscription) {
        this.subscriptionForDeletion = subscription;
        this.subDeletionModal.open();
    }


    deleteSubscription() {
        this.alarmService.deleteSubscription(this.alarm, this.subscriptionForDeletion).subscribe(
            response => {
                if ('ALARM_DELETED' == response) {
                    this.router.navigate(['alarms'])
                } else {
                    this.ngOnInit()
                }
            },
            error =>  this.errorMessage = this.errorMappingService.getMessage(error as CerebroException));
        this.subscriptionForDeletion = null;
    }

    toggleSubscription(subscription: Subscription) {
        subscription.enabled = !subscription.enabled;
        this.alarmService.updateSubscription(subscription, this.alarm.id).subscribe( response => {
          this.ngOnInit();
        });
    }

    updateEnabled(anAlarm: Alarm) {
        anAlarm.enabled = !anAlarm.enabled;
        this.updateCheck(anAlarm);
    }

    private updateCheck(anAlarm: Alarm) {
        this.alarmService.updateAlarm(anAlarm).subscribe(
            alarmId => this.router.navigate(['alarms', alarmId ]),
            error => this.errorMessage = this.errorMappingService.getMessage(error as CerebroException));
    }
    
    isSupportedSubscription (subscription : Subscription) : boolean {
      return subscription.type == 'EMAIL';
    }

    isSubscriptionsInline() : boolean {
        return this.isSubscriptionsLayoutInline;
    }

    switchSubscriptionsInline(){
        this.isSubscriptionsLayoutInline = !this.isSubscriptionsLayoutInline;
    }
  }
