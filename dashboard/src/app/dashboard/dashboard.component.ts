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
import {Alarm} from "../alarm/alarm";
import {AlarmService} from "../alarm/alarm.service";
import {AuthService} from "../common/auth/basic.auth.service";
import {Profile} from "../common/auth/profile";
import {DefaultProfile} from '../common/auth/default-profile';

@Component({
    selector: 'dashboard',
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.css', '../alert/alert.css']
})
export class DashboardComponent implements OnInit {
    errorMessage: string;
    alarms: Alarm[] = [];

    constructor(
        private router: Router,
        private alarmService: AlarmService,
        private authService: AuthService) {
    }

    ngOnInit() {

        let profile : any = this.authService.getProfile()
        if(profile instanceof DefaultProfile){
            this.router.navigate(['alarms']);
            return;
        }

        this.alarmService.getAlarmsBySubscriptionTarget(profile.email).subscribe(
            response => {
                this.alarms = response.filter(alarm => alarm.subscriptions.some(
                    function(sub){
                        return sub.enabled && sub.target === profile.email})
                );
            }
        );
    }

    gotoDetail(alarmId: String) {
        this.router.navigate(['alarms', alarmId]);
    }
}
