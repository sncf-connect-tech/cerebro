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

import {Pipe, PipeTransform} from "@angular/core";

@Pipe({
  name: 'timestampToDateHour'
})
export class TimestampToDateHourPipe implements PipeTransform {
  // STAT_MAXSIZE = 10;

    transform(timestamp: number) : string {
        // Timestamp of alarm is like this: 1479918373.252
        let date : Date = new Date(timestamp * 1000);

        let hours = this.convertToStringWithTwoDigit(date.getHours());
        let minutes = this.convertToStringWithTwoDigit(date.getMinutes());

        return hours + "h" + minutes;
    };

    /**
     * Convert a number with 1 digit to a string with 2 digit beginning by '0'
     */
    convertToStringWithTwoDigit(n: number): string {
      let ret : string = '' + n;
      ret = ret.length == 2 ? ret : '0' + ret;
      return ret;
    }
}
