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
    selector: 'edit-alarm-3-compare',
    templateUrl: 'app/alarm/edit/edit-alarm-3-compare.component.html'
})
export class EditAlarm3CompareComponent implements OnInit {
  @Input() alarm : Alarm; //Needed for thresehold level, see HTML
  @Input() targetCurrentFunction: string;
  @Input() observeMode: string;
  @Input() hidden: boolean = false;

  @Output() onSubmit = new EventEmitter<string>();
  @Output() preview = new EventEmitter<string[]>();
  @Output() back = new EventEmitter<number>();

  targetAnalyzedFunction: string;
  targetThresholdFunction: string;

  thresholdModes: SelectOption[];
  thresholdMode: string;
  windowNullAsZero:boolean;
  aberrationDelta: number;
  analysisModes: SelectOption[];
  analysisMode: string;
  timeshiftUnits: SelectOption[];
  timeshiftUnit: string;
  timeshiftNumber: number;
  timeshiftAggregations: SelectOption[];
  timeshiftAggregation: string;
  summarizeUnits: SelectOption[];
  summarizeUnit: string;
  summarizeModes: SelectOption[];
  summarizeMode: string;
  summarizeInterval: number;
  compareMethods: SelectOption[];
  compareMethod: string;
  allowNoData: boolean;
  introTexts:string[];

  constructor() { }

  ngOnInit() {
    this.initDefaultValues();
    this.initIntroTexts();
  }

  initDefaultValues() {
    this.thresholdModes = [];
    this.thresholdModes.push(new SelectOption("static", "Fixed thresholds"));
    this.thresholdModes.push(new SelectOption("history", "History comparison (timeShift)"));
	this.thresholdModes.push(new SelectOption("holtwinters", "Holt-Winters forecast deviation (holtWintersAberration)"));
    this.thresholdMode = this.thresholdModes[0].id;
    this.windowNullAsZero = false;
	this.aberrationDelta = 3;

    this.analysisModes = [];
    this.analysisModes.push(new SelectOption("static", "A single value in the past"));
    this.analysisModes.push(new SelectOption("summarize", "A time range in the past (summarize)"));
    this.analysisMode = this.analysisModes[0].id;
    this.timeshiftUnits = [];
    this.timeshiftUnits.push(new SelectOption("min", "one minute in the past"));
    this.timeshiftUnits.push(new SelectOption("10min", "10 minutes in the past"));
    this.timeshiftUnits.push(new SelectOption("20min", "20 minutes in the past"));
    this.timeshiftUnits.push(new SelectOption("30min", "30 minutes in the past"));
    this.timeshiftUnits.push(new SelectOption("40min", "40 minutes in the past"));
    this.timeshiftUnits.push(new SelectOption("50min", "50 minutes in the past"));
    this.timeshiftUnits.push(new SelectOption("h", "1 hour in the past"));
    this.timeshiftUnits.push(new SelectOption("d", "1 day in the past"));
    this.timeshiftUnits.push(new SelectOption("w", "1 week in the past"));
    this.timeshiftUnits.push(new SelectOption("mon", "1 month in the past"));
    this.timeshiftUnit = this.timeshiftUnits[0].id;
    this.timeshiftNumber = 1;
    this.timeshiftAggregations = [];
    this.timeshiftAggregations.push(new SelectOption("maxSeries", "the maximum"));
    this.timeshiftAggregations.push(new SelectOption("minSeries", "the minimum"));
    this.timeshiftAggregations.push(new SelectOption("averageSeries", "the average"));
    this.timeshiftAggregation = this.timeshiftAggregations[0].id;

    this.summarizeModes = [];
    this.summarizeModes.push(new SelectOption("sum", "Sum over..."));
    this.summarizeModes.push(new SelectOption("avg", "Average over..."));
    this.summarizeModes.push(new SelectOption("max", "Maximum over..."));
    this.summarizeModes.push(new SelectOption("min", "Minimum over..."));
    this.summarizeMode = this.summarizeModes[0].id;
    this.summarizeUnits = [];
    this.summarizeUnits.push(new SelectOption("h", "hour"));
    this.summarizeUnits.push(new SelectOption("d", "day"));
    this.summarizeUnits.push(new SelectOption("w", "week"));
    this.summarizeUnits.push(new SelectOption("mon", "month"));
    this.summarizeUnit = this.summarizeUnits[0].id;
    this.summarizeInterval = 1;

    this.compareMethods = [];
    this.compareMethods.push(new SelectOption("diffSeries", "Volumes"));
    this.compareMethods.push(new SelectOption("asPercent", "Percentages"));
    this.compareMethod = this.compareMethods[0].id;
    this.allowNoData = this.alarm.allowNoData;
  }

  isPercent() {
      return this.thresholdMode === 'history' && this.compareMethod === 'asPercent';
  }

  buildTargetThresholdFunction(): string {
    if (this.thresholdMode === "static") {
      this.targetThresholdFunction = this.targetCurrentFunction;
    } else if (this.thresholdMode === "history") {
      // TODO : clean these 'append' code please
      if (this.analysisMode === "summarize") {
        this.targetAnalyzedFunction = "summarize(" + this.alarm.target + ",'" + this.summarizeInterval + this.summarizeUnit + "','" + this.summarizeMode + "', true)";
      } else {
        this.targetAnalyzedFunction = this.alarm.target;
      }

      this.targetThresholdFunction = this.compareMethod + "(" + this.targetCurrentFunction + "," + (this.timeshiftNumber > 1 ? this.timeshiftAggregation + "(" : "");

      for (var i = 1; i <= this.timeshiftNumber; i++){
        this.targetThresholdFunction = this.targetThresholdFunction + this.buildTimeshift(this.targetAnalyzedFunction, i, this.timeshiftUnit);
        if (i !== this.timeshiftNumber) {
          this.targetThresholdFunction = this.targetThresholdFunction + ",";
        }
      }

      this.targetThresholdFunction = this.targetThresholdFunction + ")";
      this.targetThresholdFunction = this.targetThresholdFunction + (this.timeshiftNumber > 1 ? ")" : "");
    } else if (this.thresholdMode === "holtwinters") {
        this.targetThresholdFunction = "absolute(holtWintersAberration(" + this.targetCurrentFunction + "," + this.aberrationDelta + "))";
    }

    if (this.windowNullAsZero) {
      this.targetThresholdFunction = "transformNull(" + this.targetThresholdFunction + ", 0)";
    }
    return this.targetThresholdFunction;
  }

  buildTimeshift(theFunction: string, theNumber: number, theUnit: string) {
    if (theUnit.indexOf("0min") === 1) {
      return "timeShift(" + theFunction + ",'" + (theNumber * Number(theUnit.charAt(0))) + "0min')";
    } else {
      return "timeShift(" + theFunction + ",'" + theNumber + theUnit + "')";
    }
  }

  submit() {
    this.buildTargetThresholdFunction();
    this.alarm.allowNoData = this.allowNoData;
    this.onSubmit.emit(this.targetThresholdFunction);
  }

  goBack() {
    this.back.emit(3);
  }

  openPreview() {
    let targets = [this.targetCurrentFunction, this.buildTargetThresholdFunction()];
    this.preview.emit(targets);
  }

  getIllustrationSrc(): string {
    if (this.thresholdMode === 'static') {
      return 'app/alarm/edit/edit-alarm-3-compare.images/observe-' + this.observeMode + '-compare-static.png';
    } else if (this.thresholdMode === 'history') {
        return 'app/alarm/edit/edit-alarm-3-compare.images/observe-' + this.observeMode + '-compare-' + this.thresholdMode + '-'
          + this.analysisMode + '-' + (this.timeshiftNumber > 1 ? 'multi' : '1') + '.png';
    } else if (this.thresholdMode === 'holtwinters') {
        return 'app/alarm/edit/edit-alarm-3-compare.images/observe-' + this.observeMode + '-compare-holtwinters.png';
    }
  }

  initIntroTexts() {
    this.introTexts = [
        "Compare recent value(s) to a fixed threshold, or to history (dynamic trend), or to Holt-Winters (algorithm) predictions",
        "Compare recent value(s) to a single value in the past, or a time range in the past",
        "You can use a single value or a time range as history, but also compute the average, the sum, etc. over several iterations (values or time ranges) in the past, shifting each time of X hours ou days or weeks...",
        "You can set thresholds as volumes (current value - history) or percentages (current value / history)",
        "If the warning threshold is lower than the error threshold, then the alarm detects rises (traffic, response time, etc.), i.e. when values exceed the threshold",
        "Vice versa, if the warning threshold is higher than the error threshold, then the alarm detects falls, i.e. when values get lower than the thresholds",
        "You can choose weither no data in the backend is considered a zero",
        "Go back to previous steps if you want. Your inputs will be kept until you come back here",
        "Click here anytime to check the alarm previews",
        "Go to next steps when ready. You can come back here, as long as the alarm is not finished creating"
    ]
  }
}
