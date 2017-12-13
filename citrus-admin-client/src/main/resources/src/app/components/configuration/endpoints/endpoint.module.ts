import {NgModule} from "@angular/core";
import {ReactiveFormsModule, FormsModule} from "@angular/forms";
import {EndpointEffects, EndpointStateProviders} from "./endpoint.state";
import {EffectsModule} from "@ngrx/effects";
import {RouterModule, Routes} from "@angular/router";
import {CommonModule} from "@angular/common";
import {UtilComponentsModule} from "../../util/util.module";
import {UtilModule} from "../../../util/util.module";
import {EndpointsComponent, EndpointsPresentationComponent} from "./endpoints.component";
import {EndpointFormComponent, EndpointFormPresentationComponent} from "./endpoint-form.component";
import {OutletComponent} from "../../util/outlet.component";

export const endpointRoutes:Routes = [{
    path: 'endpoints',
    component: OutletComponent,
    children: [
        {path: '', component: EndpointsComponent},
        {path: 'endpoint-editor', component: EndpointFormComponent},
        {path: 'endpoint-editor/:name', component: EndpointFormComponent},
    ]
}];

@NgModule({
    imports: [
        UtilModule,
        UtilComponentsModule,
        FormsModule,
        CommonModule,
        RouterModule,
        EffectsModule.forFeature([EndpointEffects]),
        ReactiveFormsModule,
    ],
    providers: [
        ...EndpointStateProviders
    ],
    declarations: [
        EndpointsComponent,
        EndpointsPresentationComponent,
        EndpointFormComponent,
        EndpointFormPresentationComponent
    ],
    exports: [
        EndpointsComponent
    ]
})
export class EndpointModule {}
