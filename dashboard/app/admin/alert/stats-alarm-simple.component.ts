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

import {Component, Input} from "@angular/core";
import {Router} from "@angular/router";
import {Observable} from "rxjs/Observable";

import {Stat} from "../stat";

@Component({
  selector: 'modal-stats-alarm-simple',
  templateUrl: 'app/admin/alert/stats-alarm-simple.component.html',
  styleUrls: ['app/admin/admin.css']
})

export class StatsAlarmSimple {
  @Input() title: string;
  @Input() stats: Stat[];
  @Input() alarmNames: string[];

  constructor(private router: Router) { }

  gotoDetail(alarmId: String) {
    this.router.navigate(['alarms', alarmId]);
  }
}
