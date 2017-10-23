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

import {Injectable} from "@angular/core";
import {Http} from "@angular/http";

@Injectable()
export class AppConfig {

  private config: Object

  constructor(private http: Http) { }

  public load() {
    return new Promise((resolve, reject) => {
      this.http.get('../assets/globals.json')
               .map( res => res.json() )
               .catch((error: any):any => {
                 console.log('Error reading globals.json configuration file');
                 resolve(error);
               })
               .subscribe((responseData) => {
                 this.config = responseData;
                 resolve(true);
               });
      });
  }

  get(key: any) {
    return this.config[key];
  }

};
