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
import {SelectOption} from "../../common/select-option";

@Component({
  selector: 'edit-alarm-2-observe',
  templateUrl: 'app/alarm/edit/edit-alarm-2-observe.component.html'
})
export class EditAlarm2ObserveComponent implements OnInit {
  @Input() hidden: boolean = false;

  @Input() alarm : Alarm; //Needed for thresehold level, see HTML

  @Output() onSubmit = new EventEmitter<string[]>();
  @Output() preview = new EventEmitter<string[]>();
  @Output() back = new EventEmitter<number>();

  keepLastValueRegExp = new RegExp("^keepLastValue\(.*\)$");

  // Windows and threshold options
  windowModes: SelectOption[];
  windowMode: string;
  windowUnits: SelectOption[];
  windowUnit: string;
  windowNumber: number;
  windowAggregations: SelectOption[];
  windowAggregation: string;

  targetCurrentFunction: string;

  alarmWindowUnit: string='min';
  defaultWindowFrom : string = '-25'

  summarizeValue : string;
  introTexts: string[];


  constructor() { }

  ngOnInit() {
    this.initDefaultValues();
    this.initIntroTexts();
  }

  findSummarizeUnit() {
    if( this.windowUnit == 'min') {
      this.summarizeValue ='-' + (this.windowNumber + 5 ) + this.alarmWindowUnit;
      this.alarm.from = this.summarizeValue;
    }
    if( this.windowUnit == 'h') {
      this.summarizeValue ='-' + (this.windowNumber * 61 + 30) + this.alarmWindowUnit;
      this.alarm.from = this.summarizeValue;
    }
    if (this.windowUnit == 'd') {
      this.summarizeValue ='-' + (this.windowNumber * 61 * 24 + 120) + this.alarmWindowUnit;
      this.alarm.from = this.summarizeValue;
    }
  }

  findSummarizeAgragation() {
    if(this.windowMode == 'summarize') {
      this.findSummarizeUnit();
    }
    else {
      this.alarm.from = this.defaultWindowFrom + this.alarmWindowUnit;
    }
  }

  initDefaultValues() {

    this.windowModes = [];
    this.windowModes.push(new SelectOption("keepLastValue", "Last value"));
    this.windowModes.push(new SelectOption("summarize", "Time range"));
    this.windowMode = this.windowModes[0].id;
    this.windowUnits = [];
    this.windowUnits.push(new SelectOption("min", "last minutes"));
    this.windowUnits.push(new SelectOption("h", "last hours"));
    this.windowUnits.push(new SelectOption("d", "last days"));
    this.windowUnit = this.windowUnits[1].id;
    this.windowNumber = 1;
    this.windowAggregations = [];
    this.windowAggregations.push(new SelectOption("sum", "Sum over the..."));
    this.windowAggregations.push(new SelectOption("avg", "Average over the..."));
    this.windowAggregation = this.windowAggregations[0].id;

    this.summarizeValue ='-25min';
  }

  buildTargetCurrentFunction() : string {
    if (this.windowMode === "keepLastValue"){
      if (this.keepLastValueRegExp.test(this.alarm.target)) {
        this.targetCurrentFunction = this.alarm.target;
      } else {
        this.targetCurrentFunction = "keepLastValue(" + this.alarm.target + ")";
      }
    } else if (this.windowMode === "summarize"){
      this.targetCurrentFunction = "summarize(" + this.alarm.target + ",'" + this.windowNumber + this.windowUnit + "','" + this.windowAggregation + "', true)";
    }

    return this.targetCurrentFunction;
  }

  submit() {
    this.buildTargetCurrentFunction();
    this.onSubmit.emit([this.targetCurrentFunction,this.windowMode]);

  }

  goBack() {
    this.back.emit(2);
  }

  openPreview() {
    let targets = [this.buildTargetCurrentFunction()];
    this.preview.emit(targets);
  }

  getIllustrationSrc() : string {
    return 'app/alarm/edit/edit-alarm-2-observe.images/observe-' + this.windowMode + '.png';
  }

    initIntroTexts(){
        this.introTexts = [
            "Let the default sampling parameters (recommended), or make fine adjustements if you know what you are doing... ;)",
            "Base your alarm on the last value or on a smoothing function over the X last values...",
            "Go back to the previous step if you want. Your input will be kept until you come back here",
            "Click here anytime to see the alarm previews",
            "Go to the next step when you are ready. You can come back here while the alarm is not created"
        ]
    }
}
