import {
    CreateAction, CreateVoidAction, AsyncActions, IdMap, toIdMap,
    Action, toArray, AsyncCrudActionType, deleteFromMap, AsyncActionType
} from "../../../util/redux.util";
import {Injectable} from "@angular/core";
import {AppState} from "../../../state.module";
import {Store} from "@ngrx/store";
import {ConfigService} from "../../../service/config.service";
import {Effect, Actions} from "@ngrx/effects";
import {SchemaRepository, Schema} from "../../../model/schema.repository";
import {AlertService} from "../../../service/alert.service";
import {Alert, AlertType} from "../../../model/alert";
import {memoize} from "../../../util/decorator";

export interface SchemaRepositoryState {
    repositories:IdMap<SchemaRepository>;
    schemas:IdMap<Schema>;
    mappingStrategies: string[];
}

export const SchemaRepositoryStateInit:SchemaRepositoryState = {
    repositories: {},
    schemas: {},
    mappingStrategies: []
};

@Injectable()
export class SchemaRepositoryEffects {
    constructor(
        private actions:AsyncActions,
        private configurationService:ConfigService,
        private alertService:AlertService,
        private actions$:Actions,
    ) {}

    @Effect() mappingstrategies = this.actions
        .handleEffect(SchemaRepositoryActions.MAPPING_STRATEGIES, () => this.configurationService.getMappingStrategies());

    @Effect() package = this.actions
        .handleEffect(SchemaRepositoryActions.SCHEMA.READ, () => this.configurationService.getSchemas());

    @Effect() detail = this.actions
        .handleEffect(SchemaRepositoryActions.REPOSITORY.READ, () => this.configurationService.getSchemaRepositories());

    @Effect() repositoryCreate = this.actions
        .handleEffect<SchemaRepository>(SchemaRepositoryActions.REPOSITORY.CREATE, ({payload}) => this.configurationService.createSchemaRepository(payload).map(r => payload));

    @Effect() repoSchemaDelete = this.actions
        .handleEffect<SchemaRepository>(SchemaRepositoryActions.REPOSITORY.DELETE, ({payload}) => this.configurationService.deleteComponent(payload.id).map(r => payload));

    @Effect() repoSchemaUpdate = this.actions
        .handleEffect<SchemaRepository>(SchemaRepositoryActions.REPOSITORY.UPDATE, ({payload}) => this.configurationService.updateSchemaRepository(payload).map(r => payload));

    @Effect() schemaCreate = this.actions
        .handleEffect<Schema>(SchemaRepositoryActions.SCHEMA.CREATE, ({payload}) => this.configurationService.createSchema(payload).map(r => payload));

    @Effect() schemaUpdate = this.actions
        .handleEffect<Schema>(SchemaRepositoryActions.SCHEMA.UPDATE, ({payload}) => this.configurationService.updateSchema(payload).map(r => payload));

    @Effect() schemaDelete = this.actions
        .handleEffect<Schema>(SchemaRepositoryActions.SCHEMA.DELETE, ({payload}) => this.configurationService.deleteComponent(payload.id).map(r => payload));

    @Effect({dispatch:false}) alerts = this.actions$.ofType(...Object.keys(Messages))
        .do((action) => {
            const [type, message] = Messages[action.type];
            this.alertService.add(new Alert(type, message((action as any).payload.id), true));
        })
}



@Injectable()
export class SchemaRepositoryActions {
    static SCHEMA = AsyncCrudActionType('SCHEMA');
    static REPOSITORY = AsyncCrudActionType('REPOSITORY')
    static MAPPING_STRATEGIES = AsyncActionType('MAPPING_STRATEGY');

    constructor(private store:Store<AppState>) {}

    fetchSchema() {
        this.store.dispatch(CreateVoidAction(SchemaRepositoryActions.SCHEMA.READ.FETCH))
    }

    fetchRepository() {
        this.store.dispatch(CreateVoidAction(SchemaRepositoryActions.REPOSITORY.READ.FETCH))
    }

    fetchMappingStrategies() {
        this.store.dispatch(CreateAction(SchemaRepositoryActions.MAPPING_STRATEGIES.FETCH))
    }


    createSchema(newGlobalSchema: Schema) {
        this.store.dispatch(CreateAction(SchemaRepositoryActions.SCHEMA.CREATE.FETCH, newGlobalSchema))
    }

    updateSchema(schema:Schema) {
        this.store.dispatch(CreateAction(SchemaRepositoryActions.SCHEMA.UPDATE.FETCH, schema))
    }

    deleteSchema(selected: Schema) {
        this.store.dispatch(CreateAction(SchemaRepositoryActions.SCHEMA.DELETE.FETCH, selected))
    }

    createRepository(newRepository: SchemaRepository) {
        this.store.dispatch(CreateAction(SchemaRepositoryActions.REPOSITORY.CREATE.FETCH, newRepository))
    }

    updateRepository(repository: SchemaRepository) {
        this.store.dispatch(CreateAction(SchemaRepositoryActions.REPOSITORY.UPDATE.FETCH, repository))
    }

    deleteRepository(repo:SchemaRepository) {
        this.store.dispatch(CreateAction(SchemaRepositoryActions.REPOSITORY.DELETE.FETCH, repo))
    }
}

type IMessages = IdMap<[AlertType, (id:string)=>string]>;
const Messages:IMessages = {
    [SchemaRepositoryActions.REPOSITORY.CREATE.SUCCESS]:['success', (id:string) => `Successfully created repository ${id}`],
    [SchemaRepositoryActions.REPOSITORY.DELETE.SUCCESS]:['success', (id:string) => `Successfully deleted repository ${id}`],
    [SchemaRepositoryActions.REPOSITORY.UPDATE.SUCCESS]:['success', (id:string) => `Successfully updated repository ${id}`],
    [SchemaRepositoryActions.SCHEMA.CREATE.SUCCESS]:['success', (id:string) => `Successfully created schema ${id}`],
    [SchemaRepositoryActions.SCHEMA.DELETE.SUCCESS]:['success', (id:string) => `Successfully deleted schema  ${id}`],
    [SchemaRepositoryActions.SCHEMA.UPDATE.SUCCESS]:['success', (id:string) => `Successfully updated schema  ${id}`],
    [SchemaRepositoryActions.REPOSITORY.CREATE.FAILED]:['danger', (id:string) => `Failed to create repository ${id}`],
    [SchemaRepositoryActions.REPOSITORY.DELETE.FAILED]:['danger', (id:string) => `Failed to delete repository ${id}`],
    [SchemaRepositoryActions.REPOSITORY.UPDATE.FAILED]:['danger', (id:string) => `Failed to update repository ${id}`],
    [SchemaRepositoryActions.SCHEMA.CREATE.FAILED]:['danger', (id:string) => `Failed to create schema ${id}`],
    [SchemaRepositoryActions.SCHEMA.DELETE.FAILED]:['danger', (id:string) => `Failed to delete schema ${id}`],
    [SchemaRepositoryActions.SCHEMA.UPDATE.FAILED]:['danger', (id:string) => `Failed to update schema ${id}`]
};

@Injectable()
export class SchemaRepositoryStateService {
    constructor(private store:Store<AppState>) {}

    get schemas() {
        return this.store.select(s => s.configuration.schemaRepository.schemas).map(toArray)
    }

    get repositories() {
        return this.store.select(s => s.configuration.schemaRepository.repositories).map(toArray)
    }

    get mappingStrategies() {
        return this.store.select(s => s.configuration.schemaRepository.mappingStrategies)
    }

    @memoize()
    getRepository(byId:string) {
        return this.store.select(s => s.configuration.schemaRepository.repositories[byId])
    }

    @memoize()
    getSchema(byId:string) {
        return this.store.select(s => s.configuration.schemaRepository.schemas[byId])
    }
}

export function reduce (state:SchemaRepositoryState = SchemaRepositoryStateInit, action:Action<any>): SchemaRepositoryState {
    switch(action.type) {
        case SchemaRepositoryActions.REPOSITORY.READ.SUCCESS: {
            const {payload:repos} = action as Action<SchemaRepository[]>;
            return ({
                ...state,
                repositories: toIdMap(repos, r => r.id)
            })

        }
        case SchemaRepositoryActions.SCHEMA.READ.SUCCESS: {
            const {payload:schemas} = action as Action<Schema[]>;
            return ({
                ...state,
                schemas: toIdMap(schemas, s => s.id)
            })
        }
        case SchemaRepositoryActions.SCHEMA.CREATE.SUCCESS: {
            return ({
                ...state,
                schemas: { ...state.schemas, [action.payload.id]: action.payload}
            })
        }
        case SchemaRepositoryActions.SCHEMA.UPDATE.SUCCESS: {
            return ({
                ...state,
                schemas: {...state.schemas, [action.payload.id]: action.payload}
            })
        }
        case SchemaRepositoryActions.SCHEMA.DELETE.SUCCESS: {
            return ({
                ...state,
                schemas: deleteFromMap(state.schemas, action.payload.id)
            })
        }
        case SchemaRepositoryActions.REPOSITORY.CREATE.SUCCESS: {
            return ({
                ...state,
                repositories: { ...state.repositories, [action.payload.id]: action.payload}
            })
        }
        case SchemaRepositoryActions.REPOSITORY.UPDATE.SUCCESS: {
            return ({
                ...state,
                repositories: { ...state.repositories, [action.payload.id]: action.payload}
            })
        }
        case SchemaRepositoryActions.REPOSITORY.DELETE.SUCCESS: {
            return ({
                ...state,
                repositories: deleteFromMap(state.repositories, action.payload.id)
            })
        }
        case SchemaRepositoryActions.MAPPING_STRATEGIES.SUCCESS: {
            return ({
                ...state,
                mappingStrategies: action.payload
            })
        }
    }
    return state;
}
