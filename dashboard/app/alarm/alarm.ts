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

import { Subscription } from '../subscription/subscription';

export class Alarm {

    constructor(
        public id: string,
        public name: string,
        public description: string,
        public target: string,
        public from: string,
        public until: string,
        public graphiteBaseUrl: string,
        public warn: number,
        public error: number,
        public enabled: boolean,
        public live: boolean,
        public allowNoData: boolean,
        public state: string,
        public lastCheck: number,
        public subscriptions: Subscription[]
    ) {  }
}
