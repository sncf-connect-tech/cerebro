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
import {Router} from "@angular/router";
import {ModalComponent} from "ng2-bs3-modal/ng2-bs3-modal";
import {Alarm} from "../alarm";
import {AlarmService} from "../alarm.service";
import {Subscription} from "../../subscription/subscription";
import {ErrorMappingService} from "../../common/error/error-mapping.service";
import {AuthService} from "../../common/auth/basic.auth.service";
import {Profile} from "../../common/auth/profile";
import {CerebroException} from "../../common/error/cerebroException";

@Component({
  selector: 'add-alarm',
  templateUrl: 'app/alarm/add/add-alarm.component.html'
})

export class AddAlarmComponent implements OnInit {
  TOT_STEP: number = 5;

  @ViewChild('overviews')
  overviews: ModalComponent;

  errorMessage: string;
  subscriptionTypes = ['EMAIL'];
  alarm: Alarm;
  step: number;

  observeMode: string;
  targetCurrentFunction: string;
  targetThresholdFunction: string;

  overviewsOpen: boolean = false;

  constructor(
    private router: Router,
    private alarmService: AlarmService,
    private errorMappingService: ErrorMappingService,
    private authService: AuthService
  ) { }

  ngOnInit() {
    this.step = 1;
    this.initNewAlarm();
  }

  initNewAlarm() {
    let profile: Profile = this.authService.getProfile();
    let aSubscription = new Subscription(null, profile.email, this.subscriptionTypes[0], false, true, true, true, true, true, false, false, false, false, false, "0800", "2000", true);
    this.alarm = new Alarm(null, null, null, null, "-25min", "-1min", null, null, null, true, false, true, null, null, [aSubscription]);
  }

  saveAlarm(anAlarm: Alarm) {
    this.alarmService.addAlarm(anAlarm).subscribe(
        alarmId => this.router.navigate(['alarms', alarmId]),
      error => {
        this.errorMessage = this.errorMappingService.getMessage(error as CerebroException);
      }
    );
  }

  onSubmitStep1() {
    // step 1 edit directly this.alarm object.
    this.step = 2;
  }

  onSubmitStep2(targetAndMode: string[]) {
    this.targetCurrentFunction = targetAndMode[0];
    this.observeMode = targetAndMode[1];
    this.step = 3;
  }

  onSubmitStep3(target: string) {
    this.targetThresholdFunction = target;
    this.step = 4;
  }

  onSubmitStep4() {
    this.step = 5;
  }

  overviewsOpened() {
    this.overviewsOpen = true;
  }

  overviewsClosed() {
    this.overviewsOpen = false;
  }

  stepRewind(stepNumber: number) {
    this.step = stepNumber - 1;
  }

  openOverviews(targets: string[]) {
    this.targetCurrentFunction = targets[0];
    if (targets[1] != null) {
      this.targetThresholdFunction = targets[1];
    }

    this.overviews.open();
  }
}
