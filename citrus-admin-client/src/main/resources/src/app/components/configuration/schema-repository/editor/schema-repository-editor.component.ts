import {Component, OnInit, Input, Output, EventEmitter} from "@angular/core";
import {FormGroup, FormBuilder, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {Observable} from "rxjs";
import {SchemaRepository, Schema, SchemaReference, Location, Locations} from "../../../../model/schema.repository";
import {SchemaRepositoryStateService, SchemaRepositoryActions} from "../schema-repository.state";
import * as _ from 'lodash'
import {EditorMode} from "../../editor-mode.enum";


@Component({
    selector: 'schema-repository-editor',
    template: `
        <schema-repository-editor-presentation 
            [repository]="repository|async" 
            [mode]="mode|async" 
            [referenceCandidates]="referenceCandidates|async"
            [mappingStrategies]="mappingStrategies|async"
            (save)="onSave($event)"
            (cancel)="onCancel()"
        ></schema-repository-editor-presentation>
    `
})
export class SchemaRepositoryEditorComponent implements OnInit{
    editor:FormGroup;
    repository:Observable<SchemaRepository>;
    referenceCandidates:Observable<Schema[]>;
    mappingStrategies:Observable<string[]>;
    mode:Observable<EditorMode>;

    constructor(
        private fb:FormBuilder,
        private route:ActivatedRoute,
        private router:Router,
        private schemaRepositoryState:SchemaRepositoryStateService,
        private schemaRepositoryActions:SchemaRepositoryActions
    ) {}

    ngOnInit() {
        this.schemaRepositoryActions.fetchMappingStrategies();
        this.mappingStrategies = this.schemaRepositoryState.mappingStrategies;
        this.repository = this.route.params
            .switchMap(({id}) => this.schemaRepositoryState.getRepository(id))
            .map(r => r || new SchemaRepository())

        this.referenceCandidates = Observable.combineLatest(
            this.repository,
            this.schemaRepositoryState.schemas
        )
            .map(([repository, schemas]) => _.differenceBy(schemas, repository.schemas.schemas, (s) => s.id))

        this.mode = this.repository.map(r => r.id ? EditorMode.EDIT : EditorMode.NEW)

        this.editor = this.fb.group({
            id: ['', Validators.required],
            mappingStrategy: [],
            schema: this.fb.group({
                id: [],
                location: []
            }),
            referencedSchema: []
        })
    }

    onSave([mode, repo]:[EditorMode, SchemaRepository]) {
        if(mode === EditorMode.NEW) {
            this.schemaRepositoryActions.createRepository(repo);
        }
        if(mode === EditorMode.EDIT) {
            this.schemaRepositoryActions.updateRepository(repo);
        }
        return this.onCancel();
    }

    onCancel() {
        return this.router.navigate(['configuration','schema-repository'])
    }
}

@Component({
    selector: 'schema-repository-editor-presentation',
    templateUrl: 'schema-repository-editor.html'
})
export class SchemaRepositoryEditorPresentationComponent {
    editor:FormGroup;
    @Input() repository:SchemaRepository;
    @Input() mode:EditorMode;
    @Input() referenceCandidates:Schema[];
    @Input() mappingStrategies:string[];

    @Output() cancel = new EventEmitter<any>();
    @Output() save = new EventEmitter<[EditorMode, SchemaRepository]>();

    selectedRef:Schema;
    newSchemaId = '';
    newSchemaLocation = '';
    newLocation = '';
    mappingStrategy:string = "";

    get isNew() {
        return this.mode === EditorMode.NEW;
    }

    addSchema(id:string, location:string) {
        const s = new Schema();
        s.id = id;
        s.location = location;
        this.repository.schemas.schemas.push(s);
    }

    removeSchema(schema:Schema) {
        this.repository.schemas.schemas = this.repository.schemas.schemas.filter(s => s !== schema);
    }

    addSchemaReference(schema:Schema) {
        this.selectedRef = null;
        const ref = new SchemaReference();
        ref.schema = schema.id;
        this.repository.schemas.references.push(ref);
    }

    get repositoryReferenceCandidates() {
        return this.referenceCandidates.filter(r => !_.includes(this.repository.schemas.references.map(r => r.schema),r.id));
    }

    addLocation(location:string) {
        if(!this.repository.locations) {
            this.repository.locations = new Locations();
        }
        this.repository.locations.locations.push(new Location(location));
        this.newLocation = '';
    }

    removeLocation(location:Location) {
        this.repository.locations.locations = this.repository.locations.locations.filter(l => l !== location);
    }

    addMappingReference(schema:Schema) {
        this.selectedRef = null;
        const ref = new SchemaReference();
        ref.schema = schema.id;
        this.repository.schemas.references.push(ref);
    }

    removeSchemaReference(reference:SchemaReference) {
        this.repository.schemas.references = this.repository.schemas.references.filter(r => r !== reference);
    }

    invokeCancel() {
        this.cancel.next();
    }

    invokeSave() {
        this.save.next([this.mode, this.repository]);
    }
}

