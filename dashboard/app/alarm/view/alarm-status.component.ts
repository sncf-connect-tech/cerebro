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

import { Component, Input } from '@angular/core';

@Component({
    selector: 'alarm-status',
    template: `
        <span *ngIf="status == 'UNKNOWN'" class="label label-default">{{ status }}</span>
        <span *ngIf="status == 'OK'" class="label label-success">{{ status }}</span>
        <span *ngIf="status == 'WARN'" class="label label-warning">{{ status }}</span>
        <span *ngIf="status == 'ERROR'" class="label label-danger">{{ status }}</span>
        <span *ngIf="status == 'EXCEPTION'" class="label label-danger">{{ status }}</span>
    `,
})
export class AlarmStatusComponent {
    @Input()
    status: string;
}
