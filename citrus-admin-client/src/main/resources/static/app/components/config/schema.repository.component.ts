import {Component, OnInit} from '@angular/core';
import {ConfigService} from '../../service/config.service';
import {SchemaRepository, Schema, SchemaReference} from "../../model/schema.repository";
import {Alert} from "../../model/alert";
import {AlertService} from "../../service/alert.service";

@Component({
    selector: 'schema-repository',
    templateUrl: 'app/components/config/schema-repository.html',
    providers: [ConfigService]
})
export class SchemaRepositoryComponent implements OnInit {

    constructor(private _configService: ConfigService,
                private _alertService: AlertService) {
        this.repositories = [];
        this.schemas = [];
        this.newSchema = new Schema();
        this.newSchemaReference = new SchemaReference();
    }

    newSchema: Schema;
    newSchemaReference: SchemaReference;
    newRepository: SchemaRepository;
    selectedRepository: SchemaRepository;
    repositories: SchemaRepository[];

    newGlobalSchema: Schema;
    selectedGlobalSchema: Schema;
    schemas: Schema[];

    ngOnInit() {
        this.getRepositories();
    }

    getRepositories() {
        this._configService.getSchemaRepositories()
            .subscribe(
                repositories => { this.repositories = repositories; this.getSchemas()},
                error => this.notifyError(<any>error));
    }

    getSchemas() {
        this._configService.getSchemas()
            .subscribe(
                schemas => this.schemas = schemas.filter(schema => this.repositories.filter(repository => repository.schemas.schemas.filter(candidate => candidate.id == schema.id).length > 0).length == 0),
                error => this.notifyError(<any>error));
    }

    initRepository() {
        this.newRepository = new SchemaRepository();
    }

    selectRepository(selected: SchemaRepository) {
        this.selectedRepository = selected;
    }

    createRepository() {
        this._configService.createSchemaRepository(this.newRepository)
            .subscribe(
                response => {
                    this.notifySuccess("Created new schema repository '" + this.newRepository.id + "'");
                    this.repositories.push(this.newRepository); this.newRepository = undefined;
                },
                error => this.notifyError(<any>error));
    }

    removeRepository(selected: SchemaRepository, event) {
        this._configService.deleteComponent(selected.id)
            .subscribe(
                response => {
                    this.repositories.splice(this.repositories.indexOf(selected), 1);
                    this.notifySuccess("Removed schema repository '" + selected.id + "'");
                },
                error => this.notifyError(<any>error));

        event.stopPropagation();
        return false;
    }

    saveRepository() {
        this._configService.updateSchemaRepository(this.selectedRepository)
            .subscribe(
                response => {
                    this.notifySuccess("Successfully saved schema repository '" + this.selectedRepository.id + "'");
                    this.selectedRepository = undefined;
                },
                error => this.notifyError(<any>error));
    }

    initGlobalSchema() {
        this.newGlobalSchema = new Schema();
    }

    selectGlobalSchema(selected: Schema) {
        this.selectedGlobalSchema = selected;
    }

    createGlobalSchema() {
        this._configService.createSchema(this.newGlobalSchema)
            .subscribe(
                response => {
                    this.notifySuccess("Created new schema '" + this.newGlobalSchema.id + "'");
                    this.schemas.push(this.newGlobalSchema); this.newGlobalSchema = undefined;
                },
                error => this.notifyError(<any>error));
    }

    removeGlobalSchema(selected: Schema, event) {
        this._configService.deleteComponent(selected.id)
            .subscribe(
                response => {
                    this.schemas.splice(this.schemas.indexOf(selected), 1);
                    this.notifySuccess("Removed schema '" + selected.id + "'");
                },
                error => this.notifyError(<any>error));

        event.stopPropagation();
        return false;
    }

    saveGlobalSchema() {
        this._configService.updateSchema(this.selectedGlobalSchema)
            .subscribe(
                response => {
                    this.notifySuccess("Successfully saved schema '" + this.selectedGlobalSchema.id + "'");
                    this.selectedGlobalSchema = undefined;
                },
                error => this.notifyError(<any>error));
    }

    removeSchema(selected: Schema) {
        if (this.selectedRepository) {
            this.selectedRepository.schemas.schemas.splice(this.selectedRepository.schemas.schemas.indexOf(selected), 1);
        } else {
            this.newRepository.schemas.schemas.splice(this.newRepository.schemas.schemas.indexOf(selected), 1);
        }
    }

    addSchema() {
        if (this.selectedRepository) {
            this.selectedRepository.schemas.schemas.push(this.newSchema);
        } else {
            this.newRepository.schemas.schemas.push(this.newSchema);
        }

        this.newSchema = new Schema();
    }

    removeSchemaReference(selected: SchemaReference) {
        if (this.selectedRepository) {
            this.selectedRepository.schemas.references.splice(this.selectedRepository.schemas.references.indexOf(selected), 1);
        } else {
            this.newRepository.schemas.references.splice(this.newRepository.schemas.references.indexOf(selected), 1);
        }
    }

    addSchemaReference() {
        if (this.selectedRepository) {
            this.selectedRepository.schemas.references.push(this.newSchemaReference);
        } else {
            this.newRepository.schemas.references.push(this.newSchemaReference);
        }

        this.newSchemaReference = new SchemaReference();
    }

    cancel() {
        if (this.selectedRepository) {
            this.selectedRepository = undefined;
            this.getRepositories();
        } else {
            this.newRepository = undefined;
        }

        if (this.selectedGlobalSchema) {
            this.selectedGlobalSchema = undefined;
            this.getSchemas();
        } else {
            this.newGlobalSchema = undefined;
        }

        this.newSchema = new Schema();
        this.newSchemaReference = new SchemaReference();
    }

    notifySuccess(message: string) {
        this._alertService.add(new Alert("success", message, true));
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error.message, false));
    }
}