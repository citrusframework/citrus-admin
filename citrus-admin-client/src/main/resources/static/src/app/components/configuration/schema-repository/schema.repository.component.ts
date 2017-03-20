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
        /*
        this._configService.createSchema(this.newGlobalSchema)
            .subscribe(
                response => {
                    this.notifySuccess("Created new schema '" + this.newGlobalSchema.id + "'");
                    this.schemas.push(this.newGlobalSchema); this.newGlobalSchema = undefined;
                },
                error => this.notifyError(<any>error));
                */
        this.schemaRepositoryActions.createSchema(this.newGlobalSchema)
    }

    removeGlobalSchema(selected: Schema, event:MouseEvent) {
        /*
        this._configService.deleteComponent(selected.id)
            .subscribe(
                response => {
                    this.schemas.splice(this.schemas.indexOf(selected), 1);
                    this.notifySuccess("Removed schema '" + selected.id + "'");
                },
                error => this.notifyError(<any>error));
        */
        this.schemaRepositoryActions.deleteSchema(selected);
        event.stopPropagation();
        return false;
    }

    saveGlobalSchema() {
        /*
        this._configService.updateSchema(this.selectedGlobalSchema)
            .subscribe(
                response => {
                    this.notifySuccess("Successfully saved schema '" + this.selectedGlobalSchema.id + "'");
                    this.selectedGlobalSchema = undefined;
                },
                error => this.notifyError(<any>error));
                */
    }

    removeSchema(selected: Schema) {
        if (this.selectedRepository) {
            this.selectedRepository.schemas.schemas.splice(this.selectedRepository.schemas.schemas.indexOf(selected), 1);
        } else {
            this.newRepository.schemas.schemas.splice(this.newRepository.schemas.schemas.indexOf(selected), 1);
        }
    }
}