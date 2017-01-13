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

import { Stat } from './stat';

/**
 * A pipe to get a top of an array of Stat sorted by the count value.
 */
@Pipe({
  name: 'sortstat'
})
export class SortStatPipe implements PipeTransform {
  STAT_MAXSIZE = 10;

  transform(stats: Stat[])  {
    if (stats === undefined) {
      // console.error("Alarm array is undefined");
      return stats;
    }

    return stats.sort(function (a, b){
      if (a.count < b.count) {
        return 1;
      }
      if (a.count > b.count) {
        return -1;
      }
      return 0;
    }).slice(0, this.STAT_MAXSIZE);
  };
}
