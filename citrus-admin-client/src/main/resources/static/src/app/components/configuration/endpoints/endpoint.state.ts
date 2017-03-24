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

export interface EndPointState {
    endpoints:IdMap<Endpoint>;
    endpointTypes:IdMap<Endpoint>;
    endpointTypeNames:string[];
}

export const EndpointStateInit:EndPointState = {
    endpoints: {},
    endpointTypes: {},
    endpointTypeNames: [],
};

@Injectable()
export class EndPointEffects {
    constructor(
       private actions:AsyncActions,
       private endPointService:EndpointService
    ) {}

    @Effect() endpoints = this.actions
        .handleEffect(EndPointActions.ENDPOINTS, (a) => this.endPointService.getEndpoints())

    @Effect() endpointCreate = this.actions
        .handleEffect(EndPointActions.ENDPOINT.CREATE, ({payload}) => this.endPointService.createEndpoint(payload).map(r => payload))

    @Effect() endpointUpdate = this.actions
        .handleEffect(EndPointActions.ENDPOINT.UPDATE, ({payload}) => this.endPointService.updateEndpoint(payload).map(r => payload))

    @Effect() endpointDelete = this.actions
        .handleEffect(EndPointActions.ENDPOINT.DELETE, ({payload}:Action<string>) => this.endPointService.deleteEndpoint(payload).map(r => payload))

    @Effect() endpointTypes = this.actions
        .handleEffect(EndPointActions.ENDPOINT_TYPES, (a) => this.endPointService.getEndpointTypes())

    @Effect() endpointType = this.actions
        .handleEffect(EndPointActions.ENDPOINT_TYPE, ({payload}:Action<string>) => this.endPointService.getEndpointType(payload))

    @Effect({dispatch:false}) endpointsError = this.actions
        .handleError([
                EndPointActions.ENDPOINTS, EndPointActions.ENDPOINT_TYPES, EndPointActions.ENDPOINT_TYPE,
                EndPointActions.ENDPOINT.CREATE, EndPointActions.ENDPOINT.DELETE,
                EndPointActions.ENDPOINT.READ, EndPointActions.ENDPOINT.UPDATE
            ],
            ({type, payload}) => Alert.danger(`Error '${ActionToString(type)}' ${payload}`, true))

    @Effect({dispatch:false}) endpointsSucess = this.actions
        .handleSuccess([
                EndPointActions.ENDPOINT.CREATE, EndPointActions.ENDPOINT.DELETE,
                EndPointActions.ENDPOINT.READ, EndPointActions.ENDPOINT.UPDATE
            ],
            ({type, payload}) => Alert.success(`Successfully '${ActionToString(type)}'`, true))
}

@Injectable()
export class EndPointActions {
    static ENDPOINT = AsyncCrudActionType('ENDPOINT');
    static ENDPOINTS = AsyncActionType('ENDPOINTS')
    static ENDPOINT_TYPE = AsyncActionType('ENDPOINT_TYPE');
    static ENDPOINT_TYPES = AsyncActionType('ENDPOINT_TYPES')

    constructor(private store:Store<AppState>) {}

    fetchEndpoints() {
        this.store.dispatch(CreateVoidAction(EndPointActions.ENDPOINTS.FETCH))
    }

    createEndpoint(bean: Endpoint) {
        this.store.dispatch(CreateAction(EndPointActions.ENDPOINT.CREATE.FETCH, bean))
    }

    updateEndpoint(bean: Endpoint) {
        this.store.dispatch(CreateAction(EndPointActions.ENDPOINT.UPDATE.FETCH, bean))
    }

    deleteEndpoint(id: string) {
        this.store.dispatch(CreateAction(EndPointActions.ENDPOINT.DELETE.FETCH, id))
    }

    fetchEndpointTypes() {
        this.store.dispatch(CreateVoidAction(EndPointActions.ENDPOINT_TYPES.FETCH))
    }

    fetchEndpointType(type: string) {
        this.store.dispatch(CreateAction(EndPointActions.ENDPOINT_TYPE.FETCH, type))
    }

}

@Injectable()
export class EndPointStateService {
    constructor(private store:Store<AppState>) {}

    get endpoints() { return this.store.select(s => s.endpoint.endpoints).map(toArray) }
    get endpointTypes() { return this.store.select(s => s.endpoint.endpointTypes).map(toArray) }

    get endpointTypeNames() { return this.store.select(s => s.endpoint.endpointTypeNames) }

    getEndpoint(byId:string) { return  this.store.select(s => s.endpoint.endpoints).map(ep => ep[byId])}
    getEndpointType(byName:string) { return  this.store.select(s => s.endpoint.endpointTypes).map(ep => ep[byName])}
}

export const EndPointStateProviders = [
    EndPointEffects,
    EndPointActions,
    EndPointStateService
]

export const endpointReducerBuilder = new ReducerBuilder<EndPointState>(EndpointStateInit)
    .on(EndPointActions.ENDPOINTS.SUCCESS)
        ((state:EndPointState, endPoints:Endpoint[]) => {
            return ({...state, endpoints: toIdMap(endPoints, e => e.id)})
        })
    .on(EndPointActions.ENDPOINT.CREATE.SUCCESS, EndPointActions.ENDPOINT.UPDATE.SUCCESS)
        ((state:EndPointState, endpoint:Endpoint) => ({...state, endpoints: {...state.endpoints, [endpoint.id]:endpoint}}))
    .on(EndPointActions.ENDPOINT.DELETE.SUCCESS)
        ((state:EndPointState, id:string) => ({...state, endpoints: deleteFromMap(state.endpoints,id)}))
    .on(EndPointActions.ENDPOINT_TYPE.SUCCESS)
        ((state:EndPointState, endPointType:Endpoint) => ({...state, endpointTypes: {...state.endpointTypes, [endPointType.type]:endPointType}}))
    .on(EndPointActions.ENDPOINT_TYPES.SUCCESS)
        ((state:EndPointState, endpointTypeNames:string[]) => ({...state, endpointTypeNames}))