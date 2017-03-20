import {Component, OnInit, Input, Output, EventEmitter} from "@angular/core";
import {SchemaRepositoryStateService, SchemaRepositoryActions} from "../schema-repository.state";
import {Observable} from "rxjs";
import {Schema} from "../../../../model/schema.repository";
import {ActivatedRoute, Router} from "@angular/router";
import {EditorDataTupel, EditorMode} from "./editor-mode.enum";
@Component({
    selector: 'global-schema-editor',
    template: `        
        <global-schema-editor-presentation 
            [schema]="schema|async"
            [mode]="mode|async"
            (save)="onSave($event)"
            (cancel)="onCancel()"
        ></global-schema-editor-presentation>
    `
})
export class GlobalSchemaEditorComponent implements OnInit{
    schema:Observable<Schema>;
    mode:Observable<EditorMode>;

    constructor(
        private route:ActivatedRoute,
        private router:Router,
        private schemaRepositoryState:SchemaRepositoryStateService,
        private schemaRepoitoryStateActions:SchemaRepositoryActions
    ) {}

    ngOnInit(): void {
        this.schema = this.route.params
            .switchMap(({id}) => this.schemaRepositoryState.getSchema(id))
            .map(r => r || new Schema())
        this.mode = this.schema.map(s => s.id ? EditorMode.EDIT : EditorMode.NEW)

    }

    onSave([mode, schema]:EditorDataTupel<Schema>) {
        if(EditorMode.NEW === mode) {
            this.schemaRepoitoryStateActions.createSchema(schema);
        }
        if(EditorMode.EDIT === mode) {
            this.schemaRepoitoryStateActions.updateSchema(schema);
        }
        return this.onCancel();
    }

    onCancel() {
        return this.router.navigate(['configuration', 'schema-repository'])
    }
}

@Component({
    selector: 'global-schema-editor-presentation',
    templateUrl: 'global-schema-editor.html'

})
export class GlobalSchemaEditorPresentationComponent {
    @Input() schema:Schema;
    @Input() mode:EditorMode;

    @Output() save = new EventEmitter<EditorDataTupel<Schema>>();
    @Output() cancel = new EventEmitter<undefined>();

    get isNew() {
        return this.mode === EditorMode.NEW;
    }

    invokeSave() { this.save.next([this.mode, this.schema])}
    invokeCancel() { this.cancel.next()}
}