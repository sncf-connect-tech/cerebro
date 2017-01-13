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

/**
 * System configuration for Angular samples
 * Adjust as necessary for your application needs.
 */
(function (global) {
  System.config({
    paths: {
      // paths serve as alias
      'npm:': 'node_modules/'
    },
    // map tells the System loader where to look for things
    map: {
      // our app is within the app folder
      app: 'app',
      // angular bundles
      '@angular/core': 'npm:@angular/core/bundles/core.umd.js',
      '@angular/common': 'npm:@angular/common/bundles/common.umd.js',
      '@angular/compiler': 'npm:@angular/compiler/bundles/compiler.umd.js',
      '@angular/platform-browser': 'npm:@angular/platform-browser/bundles/platform-browser.umd.js',
      '@angular/platform-browser-dynamic': 'npm:@angular/platform-browser-dynamic/bundles/platform-browser-dynamic.umd.js',
      '@angular/http': 'npm:@angular/http/bundles/http.umd.js',
      '@angular/router': 'npm:@angular/router/bundles/router.umd.js',
      '@angular/forms': 'npm:@angular/forms/bundles/forms.umd.js',
      '@angular/upgrade': 'npm:@angular/upgrade/bundles/upgrade.umd.js',
      // other libraries
      'rxjs':            'npm:rxjs',
      'ng2-pagination':  'https://rawgit.com/michaelbromley/ng2-pagination/master/dist',
      'ng2-bs3-modal':   'node_modules/ng2-bs3-modal',
      'moment':          'node_modules/moment',
      'angular2-moment': 'node_modules/angular2-moment',
      'nouislider':      'node_modules/nouislider',
      'ng2-nouislider':  'node_modules/ng2-nouislider',
    },
    // packages tells the System loader how to load when no filename and/or no extension
    packages: {
      app: {
        main: './main.js',
        defaultExtension: 'js'
      },
      rxjs: {
        defaultExtension: 'js'
      },
      'ng2-pagination':  { main: 'ng2-pagination.js',        defaultExtension: 'js' },
      'ng2-bs3-modal':   { main: 'ng2-bs3-modal.js',         defaultExtension: 'js' },
      'moment':          { main: 'moment.js',                defaultExtension: 'js' },
      'angular2-moment': { main: 'index.js',                 defaultExtension: 'js' },
      'nouislider':      { main: 'distribute/nouislider.js', defaultExtension: 'js' },
      'ng2-nouislider':  { main: 'src/nouislider.js',        defaultExtension: 'js' },
    }
  });
})(this);
