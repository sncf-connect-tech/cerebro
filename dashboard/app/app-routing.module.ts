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

import { NgModule }             from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AddAlarmComponent }           from './alarm/add/add-alarm.component';
import { AlarmsComponent }             from './alarm/view/alarms.component';
import { AlarmDetailComponent }        from './alarm/view/alarm-detail.component';
import { DashboardComponent }          from './dashboard/dashboard.component';
import { AdminAlarmsComponent }        from './admin/alarm/admin-alarms.component';
import { AdminSubscriptionsComponent } from './admin/subscription/admin-subscriptions.component';
import { AdminAlertsComponent }        from './admin/alert/admin-alerts.component';

const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'dashboard',           component: DashboardComponent,          data : { name: 'Dashboard' } },
  { path: 'alarms/add',          component: AddAlarmComponent,           data : { name: 'AddAlarm' }, },
  { path: 'alarms',              component: AlarmsComponent,             data : { name: 'Alarms' }, },
  { path: 'alarms/:id',          component: AlarmDetailComponent,        data : { name: 'AlarmDetail' }, },
  { path: 'admin/alarms',        component: AdminAlarmsComponent,        data : { name: 'AdminAlarme' }, },
  { path: 'admin/subscriptions', component: AdminSubscriptionsComponent, data : { name: 'AdminSubscriptions' }, },
  { path: 'admin/alerts',        component: AdminAlertsComponent,        data : { name: 'AdminAlert' }, }
];
@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule {}
