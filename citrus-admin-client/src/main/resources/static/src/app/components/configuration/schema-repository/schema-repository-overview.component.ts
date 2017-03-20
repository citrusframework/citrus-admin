import {Component, OnInit} from '@angular/core';
import {ConfigService} from '../../../service/config.service';
import {SchemaRepository, Schema, SchemaReference} from "../../../model/schema.repository";
import {Alert} from "../../../model/alert";
import {AlertService} from "../../../service/alert.service";
import {SchemaRepositoryActions, SchemaRepositoryStateService} from "./schema-repository.state";
import {Observable} from "rxjs";

@Component({
    selector: 'schema-repository-overview',
    templateUrl: 'schema-repository-overview.html',
})
export class SchemaRepositoryOverviewComponent implements OnInit {

    constructor(private schemaRepositoryActions:SchemaRepositoryActions,
                private schemaRepositoryState:SchemaRepositoryStateService,
    ) {}

    repositories: Observable<SchemaRepository[]>;
    schemas: Observable<Schema[]>;
    noRepositories: Observable<boolean>;
    noSchemas: Observable<boolean>;

    ngOnInit() {
        this.schemas = this.schemaRepositoryState.schemas;
        this.repositories = this.schemaRepositoryState.repositories;
        this.noSchemas = this.schemas.map(s => s.length === 0)
        this.noRepositories = this.schemas.map(s => s.length === 0)
        this.schemaRepositoryActions.fetchRepository();
        this.schemaRepositoryActions.fetchSchema()
    }

    linkFor(schema:string) {
        return ['edit-schema-repository', schema]
    }

    removeRepository(repository:SchemaRepository) {
        this.schemaRepositoryActions.deleteRepository(repository);
    }

    removeSchema(schema:Schema) {
        this.schemaRepositoryActions.deleteSchema(schema);
    }

}