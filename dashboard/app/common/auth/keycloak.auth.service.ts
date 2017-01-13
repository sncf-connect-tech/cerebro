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

declare var Keycloak: any;

@Injectable()
export class AuthService {
    static auth: any = {};

    static refreshToken() {
        new Promise<string>((resolve, reject) => {
            let kca = AuthService.auth.authz;
            if (kca && kca.token) {
                kca.updateToken(5).success(() => {
                        resolve(<string>kca.token);
                    })
                    .error(() => {
                        reject('Failed to refresh token');
                    });
            } else {
                reject('Failed to refresh token');
            }
        });
    }

    static initTokenRefresh(pollingInterval?: number){
        if (pollingInterval){
            Observable.interval(pollingInterval).subscribe(() => this.refreshToken());
        }
    }

    static init(): Promise<any> {
        let keycloakAuth: any = new Keycloak('config/keycloak.json');
        AuthService.auth.loggedIn = false;

        return new Promise((resolve, reject) => {
            keycloakAuth.init({ onLoad: 'login-required' })
              .success(() => {
                AuthService.auth.loggedIn = true;
                AuthService.auth.authz = keycloakAuth;
                AuthService.auth.logoutUrl = keycloakAuth.authServerUrl + "/realms/demo/protocol/openid-connect/logout?redirect_uri=/angular2-product/index.html";
                AuthService.initTokenRefresh(3600000);
                resolve();
              })
              .error(() => {
                reject();
              });
        });
    }

    getProfile(): Profile {
        let tokenInfo: any = AuthService.auth.authz.idTokenParsed;
        /*
         * Les noms des champs du keycloakAuth.idTokenParsed sont déterminés par le mapping keycloak que l'on trouve dans Clients/le client cerebro dashboard/Mappers
         */
        return new Profile(tokenInfo.preferred_username,
            tokenInfo.email,
            tokenInfo.given_name,
            tokenInfo.family_name);
    }

    logout() {
        AuthService.auth.loggedIn = false;
        AuthService.auth.authz.logout();
        AuthService.auth.authz = null;
    }

    getToken(): Promise<string> {
        return new Promise<string>((resolve, reject) => {
            if (AuthService.auth.authz.token) {
              AuthService.auth.authz.updateToken(5)
                .success(() => {
                  resolve(<string>AuthService.auth.authz.token);
                })
                .error(() => {
                  reject('Failed to refresh token');
                });
            }
        });
    }

}
