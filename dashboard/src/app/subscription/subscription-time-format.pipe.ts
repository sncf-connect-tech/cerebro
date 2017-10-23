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

/**
 * Pipe to transform four digit to an readible time, e.g. 0830 ==> 08h30.
 * Used to convert subscription time.
 */
@Pipe({
    name: 'subscriptionTimeFormat'
})
export class SubscriptionTimeFormatPipe implements PipeTransform {

    transform(subscriptionTime: string): string {
        let result = subscriptionTime;
        if (result && result.length === 4) {
             result = result.substring(0, 2) + 'h' +  result.substring(2, 4);
        }
        return result;
    };
}
