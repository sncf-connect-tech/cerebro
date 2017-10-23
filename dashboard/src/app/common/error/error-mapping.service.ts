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

export class ErrorMappingService {
  private defaultErrorMessage = "An unexpected error has occurred: "
  private errorMessage : { [key:string]:string; } = {};

  constructor() {
    this.errorMessage["ALARM_UNKNOWN"] = "Error : the alarme cannot be found or does not exist.";
    this.errorMessage["ALARM_DUPLICATE_NAME"] = "An alarm with the same name already exists. Impossible to create/edit this alarm.";
    this.errorMessage["ALARM_TARGET_INVALID"] = "Error : the target/Graphite key is not valid.";
    this.errorMessage["ALARM_DELETE_ERROR"] = "Error : the alarm could not be deleted.";
    this.errorMessage["SUBSCRIPTION_DUPLICATE"] = "There is already a similar subscription to this alarm (address, days, hours). You cannot submit this subscription.";
    this.errorMessage["SUBSCRIPTION_INVALID"] = "The subscription is invalid, because of no active days or missing target (email address).";
    this.errorMessage["SUBSCRIPTION_DELETE_ERROR"] = "The subscription could not be deleted.";
  }

  getMessage(error: any) {

    let errorCode = error.errorCode || error;
    let message;

    message = this.errorMessage[errorCode || ''] || '';
    if(error.errorMessage) {
        message = message + '\n'+ error.errorMessage;
    }
    return message === null ? this.defaultErrorMessage + errorCode : message;
  }
}
