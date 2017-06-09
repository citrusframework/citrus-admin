import {Injectable} from "@angular/core";
import {
    AsyncActions, ReducerBuilder, AsyncActionType, AsyncCrudActionType,
    CreateVoidAction, CreateAction, toIdMap, IdMap, deleteFromMap, Action, ActionToString, toArray
} from "../../../util/redux.util";
import {Store} from "@ngrx/store";
import {AppState} from "../../../state.module";
import {Endpoint} from "../../../model/endpoint";
import {Effect} from "@ngrx/effects";
import {EndpointService} from "../../../service/endpoint.service";
import {Alert} from "../../../model/alert";

export interface EndpointState {
    endpoints:IdMap<Endpoint>;
    endpointTypes:IdMap<Endpoint>;
    endpointTypeNames:string[];
}

export const EndpointStateInit:EndpointState = {
    endpoints: {},
    endpointTypes: {},
    endpointTypeNames: [],
};

@Injectable()
export class EndpointEffects {
    constructor(
       private actions:AsyncActions,
       private endPointService:EndpointService
    ) {}

    @Effect() endpoints = this.actions
        .handleEffect(EndpointActions.ENDPOINTS, (a) => this.endPointService.getEndpoints())

    @Effect() endpointCreate = this.actions
        .handleEffect(EndpointActions.ENDPOINT.CREATE, ({payload}) => this.endPointService.createEndpoint(payload).map(r => payload))

    @Effect() endpointUpdate = this.actions
        .handleEffect(EndpointActions.ENDPOINT.UPDATE, ({payload}) => this.endPointService.updateEndpoint(payload).map(r => payload))

    @Effect() endpointDelete = this.actions
        .handleEffect(EndpointActions.ENDPOINT.DELETE, ({payload}:Action<string>) => this.endPointService.deleteEndpoint(payload).map(r => payload))

    @Effect() endpointTypes = this.actions
        .handleEffect(EndpointActions.ENDPOINT_TYPES, (a) => this.endPointService.getEndpointTypes())

    @Effect() endpointType = this.actions
        .handleEffect(EndpointActions.ENDPOINT_TYPE, ({payload}:Action<string>) => this.endPointService.getEndpointType(payload))

    @Effect({dispatch:false}) endpointsError = this.actions
        .handleError([
                EndpointActions.ENDPOINTS, EndpointActions.ENDPOINT_TYPES, EndpointActions.ENDPOINT_TYPE,
                EndpointActions.ENDPOINT.CREATE, EndpointActions.ENDPOINT.DELETE,
                EndpointActions.ENDPOINT.READ, EndpointActions.ENDPOINT.UPDATE
            ],
            ({type, payload}) => Alert.danger(`Error '${ActionToString(type)}' ${payload}`, true))

    @Effect({dispatch:false}) endpointsSucess = this.actions
        .handleSuccess([
                EndpointActions.ENDPOINT.CREATE, EndpointActions.ENDPOINT.DELETE,
                EndpointActions.ENDPOINT.READ, EndpointActions.ENDPOINT.UPDATE
            ],
            ({type, payload}) => Alert.success(`Successfully '${ActionToString(type)}'`, true))
}

@Injectable()
export class EndpointActions {
    static ENDPOINT = AsyncCrudActionType('ENDPOINT');
    static ENDPOINTS = AsyncActionType('ENDPOINTS');
    static ENDPOINT_TYPE = AsyncActionType('ENDPOINT_TYPE');
    static ENDPOINT_TYPES = AsyncActionType('ENDPOINT_TYPES');

    constructor(private store:Store<AppState>) {}

    fetchEndpoints() {
        this.store.dispatch(CreateVoidAction(EndpointActions.ENDPOINTS.FETCH))
    }

    createEndpoint(bean: Endpoint) {
        this.store.dispatch(CreateAction(EndpointActions.ENDPOINT.CREATE.FETCH, bean))
    }

    updateEndpoint(bean: Endpoint) {
        this.store.dispatch(CreateAction(EndpointActions.ENDPOINT.UPDATE.FETCH, bean))
    }

    deleteEndpoint(id: string) {
        this.store.dispatch(CreateAction(EndpointActions.ENDPOINT.DELETE.FETCH, id))
    }

    fetchEndpointTypes() {
        this.store.dispatch(CreateVoidAction(EndpointActions.ENDPOINT_TYPES.FETCH))
    }

    fetchEndpointType(type: string) {
        this.store.dispatch(CreateAction(EndpointActions.ENDPOINT_TYPE.FETCH, type))
    }

}

@Injectable()
export class EndpointStateService {
    constructor(private store:Store<AppState>) {}

    get endpoints() { return this.store.select(s => s.endpoint.endpoints).map(toArray) }
    get endpointTypes() { return this.store.select(s => s.endpoint.endpointTypes).map(toArray) }

    get endpointTypeNames() { return this.store.select(s => s.endpoint.endpointTypeNames) }

    getEndpoint(byId:string) { return  this.store.select(s => s.endpoint.endpoints).map(ep => ep[byId])}
    getEndpointType(byName:string) { return  this.store.select(s => s.endpoint.endpointTypes).map(ep => ep[byName])}
}

export const EndpointStateProviders = [
    EndpointEffects,
    EndpointActions,
    EndpointStateService
];

export const endpointReducer = new ReducerBuilder<EndpointState>(EndpointStateInit)
    .on(EndpointActions.ENDPOINTS.SUCCESS)
        ((state:EndpointState, endPoints:Endpoint[]) => {
            return ({...state, endpoints: toIdMap(endPoints, e => e.id)})
        })
    .on(EndpointActions.ENDPOINT.CREATE.SUCCESS, EndpointActions.ENDPOINT.UPDATE.SUCCESS)
        ((state:EndpointState, endpoint:Endpoint) => ({...state, endpoints: {...state.endpoints, [endpoint.id]:endpoint}}))
    .on(EndpointActions.ENDPOINT.DELETE.SUCCESS)
        ((state:EndpointState, id:string) => ({...state, endpoints: deleteFromMap(state.endpoints,id)}))
    .on(EndpointActions.ENDPOINT_TYPE.SUCCESS)
        ((state:EndpointState, endPointType:Endpoint) => ({...state, endpointTypes: {...state.endpointTypes, [endPointType.type]:endPointType}}))
    .on(EndpointActions.ENDPOINT_TYPES.SUCCESS)
        ((state:EndpointState, endpointTypeNames:string[]) => ({...state, endpointTypeNames}))
    .createReducer()