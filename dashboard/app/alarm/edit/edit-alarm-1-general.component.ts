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

import {Component, OnInit, Input, Output, EventEmitter} from "@angular/core";
import {Alarm} from "../alarm";
import {AlarmService} from "../alarm.service";
import {DatasourceService} from "../../common/datasource.service";

@Component({
    selector: 'edit-alarm-1-general',
    templateUrl: 'app/alarm/edit/edit-alarm-1-general.component.html'
})
export class EditAlarm1GeneralComponent implements OnInit {
  @Input() hidden: boolean = false;
  @Input() alarm: Alarm;

  @Output() onSubmit = new EventEmitter<boolean>();
  @Output() preview = new EventEmitter<string[]>();

  sources: string[];
  unitsWithCoefs = new Map<string, number[]>();
  introTexts: string[];

  constructor(
    private datasourceService: DatasourceService) { }

  ngOnInit() {
    this.loadSources();
    this.initIntroTexts();
    // Map with unit and multiplicator (first position) and a margin to add (second position)
    this.unitsWithCoefs.set('day', [60*24, 120]);
    this.unitsWithCoefs.set('hour', [60, 30]);
    this.unitsWithCoefs.set('min', [1, 5]);
  }

  loadSources() {
    this.datasourceService.getLocations().subscribe(
      sources => { this.sources = sources; this.alarm.graphiteBaseUrl = sources[0] }
    );
  }

  /**
   * Take each unit value in a Graphite key, add a margin and return the higher value formated for Seyren (with unit and minus symbol).
  */
  getWindowsValues () : string {
    //get alarm.target and extract time data
    let alarmTarget = this.alarm.target;

    let arrayOfTargetTimeUnits = alarmTarget.match(/\.\d{1,2}(day|hour|min)\./g);

    // Key without unit
    if (arrayOfTargetTimeUnits == null) {
      return "-25min";
    }

    let timeValues = [];

    //transform time data into minute data
    for (let item of arrayOfTargetTimeUnits) {
      item = item.replace(/\./g,'');

      // For each know unit
      for (let unit of Array.from(this.unitsWithCoefs)) {
        timeValues.push(this.retrieveWindowValue(item, unit[0]));
      }
    }

    return "-" + timeValues.sort().reverse()[0] + "min";
  }

  /**
  * Take graphite unit values (e.g. 10min, 1d, etc.) to calculate a window value.
  * For example : 1d ==> (60*24) min + a margin.
  * If value does'nt contains unit, return 0.
  */
  retrieveWindowValue(value: string, unit: string) : number {
    if (value.indexOf(unit) == -1) {
      return 0;
    }

    let extractedValue : number = +value.replace(unit,''); // "+" convert string to number... - take unit text from map
    extractedValue = extractedValue * this.unitsWithCoefs.get(unit)[0]; // Convert to minutes if it hour, day, etc...
    let stringFrom = extractedValue + this.unitsWithCoefs.get(unit)[1]; // Add a margin

    return stringFrom;
  }

  submit() {
    this.alarm.from = this.getWindowsValues();
    this.onSubmit.emit(true);
  }

  openPreview() {
    this.preview.emit(null);
  }

  onWizardSubmit(target: string) {
    this.alarm.target = target;
  }

  initIntroTexts(){
    this.introTexts = [
        'The alarm name is useful to search among the alarms and sort the received notifications',
        'A good description helps to maintain the alarms and to better understand the received alerts',
        'Enter your Graphite key to be watched',
        'Lost in Graphite functions ? Click here to access Graphite documentation',
        'Where is the data ? If nothing seems to happen in the previews, it may be the source of the problem',
        'Click here anytime to see alarm previews',
        'Got to the next step when ready. You can come back here while the alarm is not created'
    ]
  }
}
