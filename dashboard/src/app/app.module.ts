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

import './rxjs-extensions';

import { NgModule }      from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule }   from '@angular/forms';
import { HttpModule }    from '@angular/http';

/** External module **/
import { NgxPaginationModule } from 'ngx-pagination';
import { BsModalModule }       from 'ng2-bs3-modal';
import { MomentModule }        from 'angular2-moment';
import { NouisliderModule }    from 'ng2-nouislider/src/nouislider';
import { ToastModule }         from 'ng2-toastr/ng2-toastr';

import { AppComponent }         from './app.component';

/** App component & Services **/
import { AlarmsComponent }        from './alarm/view/alarms.component';
import { AlarmDetailComponent }   from './alarm/view/alarm-detail.component';
import { AlarmStatusComponent }   from './alarm/view/alarm-status.component';
import { AddAlarmComponent }      from './alarm/add/add-alarm.component';
import { CopyAlarmFormComponent } from './alarm/copy/copy-alarm-form.component';

import { EditAlarmFormComponent }     from './alarm/edit/edit-alarm-form.component';
import { EditAlarm1GeneralComponent } from './alarm/edit/edit-alarm-1-general.component';
import { EditAlarm2ObserveComponent } from './alarm/edit/edit-alarm-2-observe.component';
import { EditAlarm3CompareComponent } from './alarm/edit/edit-alarm-3-compare.component';
import { EditAlarm4NotifyComponent }  from './alarm/edit/edit-alarm-4-notify.component';
import { EditAlarm5ConfirmComponent } from './alarm/edit/edit-alarm-5-confirm.component';

import { EditSubscriptionFormComponent } from './subscription/edit/edit-subscription-form.component';

import { DashboardComponent } from './dashboard/dashboard.component';

import { TargetOverviewComponent }     from './target/overview/target-overview.component';

import { AdminAlarmsComponent }        from './admin/alarm/admin-alarms.component';
import { AdminSubscriptionsComponent } from './admin/subscription/admin-subscriptions.component';
import { AdminAlertsComponent }        from './admin/alert/admin-alerts.component';
import { StatsAlarmSimple }            from './admin/alert/stats-alarm-simple.component';

import { AlarmService }        from './alarm/alarm.service';
import { AlertService }        from './alert/alert.service';
import { DatasourceService }        from './common/datasource.service';
import { ErrorMappingService } from './common/error/error-mapping.service';

/** Pipes **/
import { EmailSubPipe }               from './common/pipe/email-sub.pipe';
import { TimestampToDateHourPipe }    from './common/pipe/timestamp-to-date-hour.pipe';
import { SortSubscriptionPipe }       from './subscription/sort-subscription.pipe';
import { SubscriptionTimeFormatPipe } from './subscription/subscription-time-format.pipe';
import { SortStatPipe }               from './admin/sort-stat.pipe';
import { TopTargetGraphitePipe }      from './admin/top-target-graphite.pipe';

/** Authentication **/
import { AuthService }                      from './common/auth/basic.auth.service';
import { HttpInterceptor }                  from './common/auth/http-interceptor';
import { Http, XHRBackend, RequestOptions } from '@angular/http';

/** Config **/
import { APP_INITIALIZER } from '@angular/core';
import { AppConfig }       from './app.config';

import { AppRoutingModule } from './app-routing.module';

@NgModule({
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    NgxPaginationModule,
    BsModalModule,
    MomentModule,
    NouisliderModule,
    ToastModule.forRoot(),
    AppRoutingModule,
   ],
  declarations: [
    AppComponent,
    AlarmsComponent,
    AlarmDetailComponent,
    CopyAlarmFormComponent,
    EditAlarmFormComponent,
    EditSubscriptionFormComponent,
    AlarmStatusComponent,
    DashboardComponent,
    AddAlarmComponent,
    EditAlarm1GeneralComponent,
    EditAlarm2ObserveComponent,
    EditAlarm3CompareComponent,
    EditAlarm4NotifyComponent,
    EditAlarm5ConfirmComponent,
    TargetOverviewComponent,

    AdminAlarmsComponent,
    AdminSubscriptionsComponent,
    AdminAlertsComponent,
    StatsAlarmSimple,

    EmailSubPipe,
    SortSubscriptionPipe,
    TopTargetGraphitePipe,
    TimestampToDateHourPipe,
    SortStatPipe,
    SubscriptionTimeFormatPipe,
  ],
  providers: [
    AlarmService,
    AlertService,
    DatasourceService,
    ErrorMappingService,
    AuthService,
    {
      provide: Http,
      useFactory: createAuthService,
      deps: [XHRBackend, RequestOptions, AuthService]
    },

    AppConfig,
    {
      provide: APP_INITIALIZER,
      useFactory: createAppConfig,
      deps: [AppConfig], multi: true
    }
  ],
  bootstrap: [
    AppComponent
  ]
})
export class AppModule { }

export function createAuthService(backend: XHRBackend, defaultOptions: RequestOptions, authService: AuthService) {
    return new HttpInterceptor(backend, defaultOptions, authService);
}

export function createAppConfig(config: AppConfig) {
  return () => config.load();
}