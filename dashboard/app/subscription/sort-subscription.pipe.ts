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

import { Subscription } from './subscription';

/**
 * A pipe to sort subscription by type.
 * Subscription with type 'EMAIL' are returned first.
 */
@Pipe({
  name : 'sortSubscriptionPipe'
})
export class SortSubscriptionPipe implements PipeTransform {

  transform(subscriptions: Subscription[]) {
    return subscriptions.sort(function (a, b){
      if (a.type === 'EMAIL' && b.type !== 'EMAIL') {
        return -1;
      }
      if (b.type === 'EMAIL' && a.type !== 'EMAIL') {
        return 1;
      }
      return 0;
      });
  }
}
