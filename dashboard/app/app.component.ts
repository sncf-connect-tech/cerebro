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

import { Component, ViewContainerRef } from '@angular/core';
import { ToastsManager } from 'ng2-toastr/ng2-toastr';

import { AuthService } from './common/auth/basic.auth.service';
import { Profile } from './common/auth/profile';
import { DefaultProfile } from './common/auth/default-profile';
import { AppConfig }   from './app.config';

@Component({
    moduleId: module.id,
    selector: 'cerebro-app',
    templateUrl: 'app.component.html',
    styleUrls: ['app.component.css']
})
export class AppComponent {
    background:any = {
      "background": "url('" + this.config.get("theme").headerImage + "') center center no-repeat"
    };

    contacts: any = this.config.get("contacts");
    private profile:any = this.authService.getProfile();
    hasAuthentication:boolean = !(this.profile instanceof DefaultProfile);

    constructor(
      private authService: AuthService,
      public toastr: ToastsManager,
      vRef: ViewContainerRef,
      private config: AppConfig
    )
    {
      this.toastr.setRootViewContainerRef(vRef);
    }

    logout() {
        this.authService.logout();
    }
}
