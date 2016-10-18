import {provide}    from 'angular2/core'
import {bootstrap}    from 'angular2/platform/browser'
import {ROUTER_PROVIDERS, LocationStrategy, HashLocationStrategy} from 'angular2/router';
import {AppComponent} from './components/app.component'
import {AlertService} from "./service/alert.service";
import 'rxjs/Rx';

//noinspection TypeScriptValidateTypes
bootstrap( AppComponent, [ROUTER_PROVIDERS, AlertService, provide(LocationStrategy, {useClass: HashLocationStrategy})] );