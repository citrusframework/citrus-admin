import {provide}    from 'angular2/core'
import {bootstrap}    from 'angular2/platform/browser'
import {ROUTER_PROVIDERS, LocationStrategy, HashLocationStrategy} from 'angular2/router';
import {AppComponent} from './components/app.component'
import 'rxjs/Rx';

//noinspection TypeScriptValidateTypes
bootstrap( AppComponent, [ROUTER_PROVIDERS, provide(LocationStrategy, {useClass: HashLocationStrategy})] );