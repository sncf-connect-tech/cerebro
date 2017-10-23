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
  name: 'emailSub'
})
export class EmailSubPipe implements PipeTransform {
    MAX_LENGTH: number = 25;

    transform(value: string, args: string[]): any {
        if (value == undefined) {
          console.error("Alarm array is undefined");
          return value;
        }
        if (value.length < this.MAX_LENGTH) {
            return value
        } else {
            return value.substring(0, this.MAX_LENGTH) + '...';
        }
    };
}
