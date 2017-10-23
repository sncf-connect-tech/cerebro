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

import { Injectable } from "@angular/core";
import { Observable } from 'rxjs/Observable';

import { Profile } from './profile';
import {AppConfig} from "../../app.config";
import { DefaultProfile }       from './default-profile';

/**
 * This is a very simple implementation of AuthService with no authentication provider and use default profile defined in the DefaultProfile class
 */
@Injectable()
export class AuthService {
    static auth: any = {};

    static refreshToken() {
        new Promise<string>((resolve) => {
            resolve();
        });
    }

    static initTokenRefresh(pollingInterval?: number){
        if (pollingInterval){
            Observable.interval(pollingInterval).subscribe(() => this.refreshToken());
        }
    }

    static init(): Promise<any> {
        return new Promise((resolve) => {
            resolve();
        });
    }

    /**
     * @returns {DefaultProfile}
     */
    getProfile(): Profile {
        return new DefaultProfile();
    }

    /**
     * Do nothing
     */
    logout() {
        //empty
    }

    getToken(): Promise<string> {
        return new Promise<string>((resolve) => {
            resolve();
        });
    }

}
