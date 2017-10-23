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

import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';

import { Subscription }        from '../subscription';
import { AlarmService }        from '../../alarm/alarm.service';
import { ErrorMappingService } from '../../common/error/error-mapping.service';
import { AuthService }         from '../../common/auth/basic.auth.service';
import { Profile }             from '../../common/auth/profile';
import { CerebroException }    from '../../common/error/cerebroException';

@Component({
    selector: 'modal-subscription-form',
    templateUrl: './edit-subscription-form.component.html'
})
export class EditSubscriptionFormComponent implements OnInit {
    private _subscription: Subscription;

    errorMessage: string;
    isNewSubscription: boolean = false;
    submitted: boolean = false;
    subscriptionTypes = ['EMAIL'];

    @Input()
    alarmId: string;

    @Output()
    onUpdate = new EventEmitter<boolean>();

    defaultSliderConfig: any;

    constructor(private alarmService: AlarmService,
        private errorMappingService: ErrorMappingService,
        private authService: AuthService) {
    }

    ngOnInit() {
      if (this._subscription == null) {
        this.initNewSubscription();
        this.isNewSubscription = true;
      }

      this.defaultSliderConfig = {
        start: [  new Date(0,0,0,Number(this._subscription.fromTime.substring(0,2)),Number(this._subscription.fromTime.substring(2,4)),0,0).getTime(),
          new Date(0,0,0,Number(this._subscription.toTime.substring(0,2)),Number(this._subscription.toTime.substring(2,4)),0,0).getTime() ],
        step: 600000,
        connect: true,
        range: {
          min: new Date(0,0,0,0,0,0,0).getTime(),
          max: new Date(0,0,0,23,59,0,0).getTime()
        }
      };
    }

    initNewSubscription() {
        let profile: Profile = this.authService.getProfile();
        this._subscription = new Subscription(null, profile.email, this.subscriptionTypes[0], false, true, true, true, true, true, false, false, false, false, false, "0800", "2000", true);
    }

    @Input()
    set subscription(subscription: Subscription) {
        this._subscription = Object.assign({}, subscription);
    }

    get subscription() {
        return this._subscription;
    }

    submit() {
        this.submitted = true;

        if (this.isNewSubscription) {
          this.alarmService.addSubscription(this.subscription, this.alarmId).subscribe(
            response =>
              {
                this.onUpdate.emit(true);
                this.initNewSubscription();
              },
              error => this.errorMessage = this.errorMappingService.getMessage(error as CerebroException));
        } else {
          this.alarmService.updateSubscription(this.subscription, this.alarmId).subscribe(
            response => this.onUpdate.emit(true),
            error => this.errorMessage = this.errorMappingService.getMessage(error as CerebroException)
          );
        }
    }

    getModalId(): string {
        return this.subscription.id + this.alarmId;
    }

    getTitle(): string {
        if (this.isNewSubscription) {
            return "Subscribe to an alarm";
        } else {
            return "Subscription changes for " + this.subscription.target;
        }
    }

	isActive(day: string){
        return this._subscription[day];
    }

    switchActive(day: string){
        this._subscription[day] = !this._subscription[day];
    }

    isIgnoreStatus(subscriptionProperty: string){
        return this._subscription[subscriptionProperty];
    }

    switchIgnoreStatus(subscriptionProperty: string){
        this._subscription[subscriptionProperty] = !this._subscription[subscriptionProperty];
    }

    onSliderChange($event) {
      let fromDate = new Date(parseInt($event[0]));
      let fromHour = (fromDate.getHours().toString().length < 2 ? "0" + fromDate.getHours() : fromDate.getHours());
      let fromMin = (fromDate.getMinutes().toString().length < 2 ? "0" + fromDate.getMinutes() : fromDate.getMinutes());
      this.subscription.fromTime = "" + fromHour + fromMin;

      let toDate = new Date(parseInt($event[1]));
      let toHour = (toDate.getHours().toString().length < 2 ? "0" + toDate.getHours() : toDate.getHours());
      let toMin = (toDate.getMinutes().toString().length < 2 ? "0" + toDate.getMinutes() : toDate.getMinutes());
      this.subscription.toTime = "" + toHour + toMin;
    }

    actionOnOpen(){
        this.submitted = false;
    }
}
