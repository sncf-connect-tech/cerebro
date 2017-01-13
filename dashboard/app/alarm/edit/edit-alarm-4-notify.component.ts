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

@Component({
    selector: 'edit-alarm-4-notify',
    templateUrl: 'app/alarm/edit/edit-alarm-4-notify.component.html'
})
export class EditAlarm4NotifyComponent implements OnInit {
  @Input() hidden: boolean = false;
  @Input() alarm: Alarm;

  @Output() onSubmit = new EventEmitter<boolean>();
  @Output() preview = new EventEmitter<boolean>();
  @Output() back = new EventEmitter<number>();

  defaultSliderConfig: any = {
    start: [ new Date(0,0,0,8,0,0,0).getTime(), new Date(0,0,0,20,0,0,0).getTime() ],
    step: 600000,
    connect: true,
    range: {
      min: new Date(0,0,0,0,0,0,0).getTime(),
      max: new Date(0,0,0,23,59,0,0).getTime()
    }
  };

  private introTexts:string[];

  constructor() { }

  ngOnInit() {
    this.initIntroTexts();
  }

  isActive(day: string){
      return this.alarm.subscriptions[0][day];
  }

  isIgnoreStatus(subscriptionProperty: string){
      return this.alarm.subscriptions[0][subscriptionProperty];
  }

  switchActive(day: string){
      this.alarm.subscriptions[0][day] = !this.alarm.subscriptions[0][day];
  }

  switchIgnoreStatus(subscriptionProperty: string){
      this.alarm.subscriptions[0][subscriptionProperty] = !this.alarm.subscriptions[0][subscriptionProperty];
  }

  onSliderChange($event) {
    let fromDate = new Date(parseInt($event[0]));
    var fromHour = (fromDate.getHours().toString().length < 2 ? "0" + fromDate.getHours() : fromDate.getHours());
    var fromMin = (fromDate.getMinutes().toString().length < 2 ? "0" + fromDate.getMinutes() : fromDate.getMinutes());
    this.alarm.subscriptions[0].fromTime = "" + fromHour + fromMin;

    let toDate = new Date(parseInt($event[1]));
    var toHour = (toDate.getHours().toString().length < 2 ? "0" + toDate.getHours() : toDate.getHours());
    var toMin = (toDate.getMinutes().toString().length < 2 ? "0" + toDate.getMinutes() : toDate.getMinutes());
    this.alarm.subscriptions[0].toTime = "" + toHour + toMin;
  }

  submit() {
    this.onSubmit.emit(true);
  }

  goBack() {
    this.back.emit(4);
  }

  openPreview() {
    this.preview.emit(true);
  }

  initIntroTexts(){
    this.introTexts =[
        "You can create a subscription with your address, someone else's address or a mailing list",
        "Tell if you want to receive only specific status change (for example be notified if the status changes to ERROR, but not if it switches back to OK)",
        "Indicate the days of the week for which you want to receive notifications",
        "Indicate the time range during which you want to receive notifications",
        "Go back to the previous step if you want. Your input will be kept until you come back here",
        "Click here anytime to see the alarm previews",
        "Go to the next step when you are ready. You can come back here while the alarm is not created"
    ]
  }
}
