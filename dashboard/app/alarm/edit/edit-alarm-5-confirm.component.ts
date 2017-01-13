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

import {Component, Input, Output, EventEmitter, OnInit} from "@angular/core";
import {ToastsManager} from "ng2-toastr/ng2-toastr";
import {Alarm} from "../alarm";
import {AlarmService} from "../alarm.service";
import {CerebroException} from "../../common/error/cerebroException";
import {ErrorMappingService} from "../../common/error/error-mapping.service";

@Component({
    selector: 'edit-alarm-5-confirm',
    templateUrl: 'app/alarm/edit/edit-alarm-5-confirm.component.html'
})
export class EditAlarm5ConfirmComponent implements OnInit {
  @Input() alarm: Alarm;
  @Input() targetThresholdFunction: string;
  @Input() hidden: boolean = false;

  @Output() onSubmit = new EventEmitter<Alarm>();
  @Output() preview = new EventEmitter<boolean>();
  @Output() back = new EventEmitter<number>();

  existedAlarm: boolean;
  existedSubscription: boolean;
  introTexts: string[];
  errorMessage: string;

  finalAlarm: Alarm; // Alarm to save
  identicalAlarm: Alarm; // Alarm from Seyren

  constructor(
    private alarmService: AlarmService,
    private errorMappingService: ErrorMappingService,
    private toastr: ToastsManager
  ) { }

  ngOnInit() {
    this.existedAlarm = false;
    this.existedSubscription = false;
    this.initIntroTexts();

    // Search if a same alarm already exist

    this.finalAlarm = Object.assign({}, this.alarm);
    this.finalAlarm.target = this.targetThresholdFunction;

    this.alarmService.searchAlarm(this.finalAlarm).subscribe(
      response => {
        if (response != null) {
          this.existedAlarm = true;
          this.identicalAlarm = response;
          this.toastr.warning("This alarm (nom : \"" + response.name + "\") already exists with the same configuration. Your alarm will be attach to it", "Warning");

          // Search if a same subscription on the alarm already exist
          this.alarmService.searchSubscription(this.finalAlarm.subscriptions[0], response.id).subscribe(
            response => {
              if (response != null) {
                this.existedSubscription = true;
                this.toastr.error("There is already a subscription similar to this notification (email adress, selected days, schedule).\n You can not submit this subscription.", "Warning");
              }
            }
          );
        }
      }, error =>  this.errorMessage = this.errorMappingService.getMessage(error as CerebroException)
    );
  }

  isIgnoreStatus(subscriptionProperty: string) {
    return this.alarm.subscriptions[0][subscriptionProperty];
  }

  submit() {
    this.onSubmit.emit(this.finalAlarm);
  }

  goBack() {
    this.back.emit(5);
  }

  openPreview() {
    this.preview.emit(true);
  }

  private initIntroTexts() {
    this.introTexts = [
      "Check one last time all your parameters before validating the alarm creation",
      "You can check if the generated Graphite function is correct according to the parameters you indicated",
      "Go back to the previous step if you want. Your input will be kept until you come back here",
      "Click here anytime to see the alarm previews",
      "If you are confident in the parameters, you can validate the alarm creation. Bravo ! :-)"
    ]
  }
}
