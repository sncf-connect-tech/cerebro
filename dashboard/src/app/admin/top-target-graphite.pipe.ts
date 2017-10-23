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

import { Pipe, PipeTransform } from '@angular/core';

import { Alarm } from '../alarm/alarm';

/**
 * A pipe to get a top of an array of Alarm sorted by the target length.
 */
@Pipe({
  name: 'topTargetGraphite'
})
export class TopTargetGraphitePipe implements PipeTransform {
  STAT_MAXSIZE = 10;

  transform(alarms: Alarm[])  {
    if (alarms === undefined) {
      // console.error("Alarm array is undefined");
      return alarms;
    }

    return alarms.sort(function (a, b) {
      if (a.target.length < b.target.length) {
        return 1;
      }
      if (a.target.length > b.target.length) {
        return -1;
      }
      return 0;
    }).slice(0, this.STAT_MAXSIZE);
  };
}
