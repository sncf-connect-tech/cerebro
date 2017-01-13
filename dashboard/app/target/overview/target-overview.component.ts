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

import { Component, Input } from '@angular/core';

import { Alarm } from '../../alarm/alarm';
import {AppConfig} from "../../app.config";

@Component({
    selector: 'target-overview',
    templateUrl: 'app/target/overview/target-overview.component.html',
    styleUrls: ['app/target/overview/target-overview-component.css']
})
export class TargetOverviewComponent {
    _target: string;

    @Input()
    alarm: Alarm;

    @Input()
    showThresholds: boolean = true;
    @Input()
    showTargetLabel: boolean = false;
    @Input()
    aliasTarget: boolean = true;
    @Input()
    targetLabel: string;

    constructor(private config: AppConfig){}

    graphMiniUrl(period: string, title: string) {
        if (this.target != null) {
            let url = this.alarm.graphiteBaseUrl + (this.alarm.graphiteBaseUrl.endsWith("/") ? "" : "/") + "render?from=-" + period
                + (this.aliasTarget ? ("&target=alias(" + this.target + ",'Target')") : ("&target=" + this.target)) + "&title=" + title;
            if (this.showThresholds && this.alarm.warn != null && this.alarm.error != null) {
                url = url + "&target=alias(color(constantLine(" + this.alarm.warn + "),'orange'),'Warning')&target=alias(color(constantLine("
                    + this.alarm.error + "),'red'),'Error')";
            }
            return url;
        }
        else {
            return "";
        }
    }

    graphMaxiUrl(period: string, title: string) {
        return this.graphMiniUrl(period, title) + "&width=800&height=600";
    }

    get target() {
        if (this._target != null) {
            return this._target;
        } else {
            return this.alarm.target;
        }
    }

    @Input()
    set target(target: string) {
        this._target = target;
    }
}
