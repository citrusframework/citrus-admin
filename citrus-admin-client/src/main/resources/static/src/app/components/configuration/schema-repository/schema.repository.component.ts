import {Component, OnInit} from '@angular/core';
import {ConfigService} from '../../../service/config.service';
import {SchemaRepository, Schema, SchemaReference} from "../../../model/schema.repository";
import {SchemaRepositoryActions, SchemaRepositoryStateService} from "./schema-repository.state";
import {Observable} from "rxjs";

@Component({
    selector: 'schema-repository',
    templateUrl: 'schema-repository.html',
    providers: [ConfigService]
})
export class SchemaRepositoryComponent implements OnInit {

    constructor(
                private schemaRepositoryActions:SchemaRepositoryActions,
                private schemaRepositoryState:SchemaRepositoryStateService,
                ) {
    }

    newSchema: Schema;
    newSchemaReference: SchemaReference;
    newRepository: SchemaRepository;
    selectedRepository: SchemaRepository;
    repositories: Observable<SchemaRepository[]>;

    newGlobalSchema: Schema;
    selectedGlobalSchema: Schema;
    schemas: Observable<Schema[]>;

    ngOnInit() {
        this.schemas = this.schemaRepositoryState.schemas;
        this.repositories = this.schemaRepositoryState.repositories;
        this.schemaRepositoryActions.fetchRepository();
        this.schemaRepositoryActions.fetchSchema()
    }

    initGlobalSchema() {
        this.newGlobalSchema = new Schema();
    }

    selectGlobalSchema(selected: Schema) {
        this.selectedGlobalSchema = selected;
    }

    createGlobalSchema() {
        this.schemaRepositoryActions.createSchema(this.newGlobalSchema)
    }

    removeGlobalSchema(selected: Schema, event:MouseEvent) {
        this.schemaRepositoryActions.deleteSchema(selected);
        event.stopPropagation();
        return false;
    }

    saveGlobalSchema() {
    }

    removeSchema(selected: Schema) {
        if (this.selectedRepository) {
            this.selectedRepository.schemas.schemas.splice(this.selectedRepository.schemas.schemas.indexOf(selected), 1);
        } else {
            this.newRepository.schemas.schemas.splice(this.newRepository.schemas.schemas.indexOf(selected), 1);
        }
    }
}