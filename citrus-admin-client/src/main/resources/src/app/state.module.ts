import {InjectionToken, NgModule} from "@angular/core";
import {ActionReducerMap, StoreModule} from "@ngrx/store";
import {StoreDevtoolsModule} from "@ngrx/store-devtools";
import { StoreRouterConnectingModule} from "@ngrx/router-store";
import {routerReducer as router} from '@ngrx/router-store'
import {reduce as tests, TestState} from './components/test/test.state'
import {reduce as configuration} from './components/configuration/configuration.state'
import {ConfigurationState} from "./components/configuration/configuration.state";
import {environment as env} from "../environments/environment";
import {endpointReducer as endpoint, EndpointState} from "./components/configuration/endpoints/endpoint.state";

export interface AppState {
    router:any,
    tests:TestState,
    configuration:ConfigurationState,
    endpoint:EndpointState
}

export const REDUCER_TOKEN = new InjectionToken<ActionReducerMap<AppState>>('Root reducer-map');

export function getReducers(): ActionReducerMap<any> {
  return ({
    router,
    tests,
    configuration,
    endpoint
  })
}


@NgModule({
    imports: [
        StoreModule.forRoot(REDUCER_TOKEN),
        //RouterStoreModule.connectRouter(),
        StoreRouterConnectingModule,
        StoreDevtoolsModule.instrument({})
    ],
  providers: [
    {
      provide: REDUCER_TOKEN,
      deps: [],
      useFactory: getReducers
    }
  ]
})
export class StateModule {}
