import {NgModule} from "@angular/core";
import {
    SchemaRepositoryActions, SchemaRepositoryEffects,
    SchemaRepositoryStateService
} from "./schema-repository.state";

import {SchemaRepositoryComponent} from "./schema.repository.component";
import {CommonModule} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {UtilComponentsModule} from "../../util/util.module";
import {UtilModule} from "../../../util/util.module";
import {EffectsModule} from "@ngrx/effects";
import {
    SchemaRepositoryEditorComponent,
    SchemaRepositoryEditorPresentationComponent
} from "./editor/schema-repository-editor.component";
import { ReactiveFormsModule } from '@angular/forms'
import {SchemaRepositoryOverviewComponent} from "./schema-repository-overview.component";
import {Routes, RouterModule} from "@angular/router";
import {
    GlobalSchemaEditorComponent,
    GlobalSchemaEditorPresentationComponent
} from "./editor/global-schema-editor.component";

export const routes:Routes = [{
    path: 'schema-repository',
    component: SchemaRepositoryComponent,
    children: [
        { path: '', component: SchemaRepositoryOverviewComponent },
        { path: 'edit-schema-repository', component: SchemaRepositoryEditorComponent },
        { path: 'edit-schema-repository/:id', component: SchemaRepositoryEditorComponent },
        { path: 'edit-global-schema', component: GlobalSchemaEditorComponent },
        { path: 'edit-global-schema/:id', component: GlobalSchemaEditorComponent }
    ]
}]

@NgModule({
    imports: [
        UtilModule,
        UtilComponentsModule,
        FormsModule,
        CommonModule,
        RouterModule,
        EffectsModule.run(SchemaRepositoryEffects),
        ReactiveFormsModule,
    ],
    providers: [
        SchemaRepositoryActions,
        SchemaRepositoryEffects,
        SchemaRepositoryStateService,
    ],
    declarations: [
        SchemaRepositoryComponent,
        SchemaRepositoryEditorComponent,
        SchemaRepositoryOverviewComponent,
        SchemaRepositoryEditorPresentationComponent,
        GlobalSchemaEditorComponent,
        GlobalSchemaEditorPresentationComponent
    ],
    exports: [
        SchemaRepositoryComponent
    ]
})
export class SchemaRepositoryModule {
}