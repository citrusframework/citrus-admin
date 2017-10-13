import {SchemaRepositoryState, SchemaRepositoryStateInit, reduce as schemaRepository} from "./schema-repository/schema-repository.state";
import {combineReducers} from "@ngrx/store";

export interface ConfigurationState {
    schemaRepository:SchemaRepositoryState
}

export const ConfigurationStateInit:ConfigurationState = {
    schemaRepository: SchemaRepositoryStateInit
};

export const reduce = combineReducers({
    schemaRepository
});