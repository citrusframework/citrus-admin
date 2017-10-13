import {Routes, RouterModule} from "@angular/router";
import {NgModule} from "@angular/core";
import {CanActivateRoutes} from "../../service/can-activate-routes";
import {TestsComponent} from "./tests.component";
import {TestEditorComponent} from "./test.editor.component";
import {TestListComponent} from "./test.list.component";
import {ServiceModule} from "../../service/service.module";
import {TestGroupRunComponent} from "./test.group.run.component";

const routes:Routes = [
    {
        path: 'tests',
        component: TestsComponent,
        canActivate: [CanActivateRoutes],
        children: [
            { path: '', redirectTo: 'run', pathMatch: 'full'},
            { path: 'list', component: TestListComponent},
            { path: 'run', component: TestGroupRunComponent},
            { path: 'editor', component: TestEditorComponent},
            { path: 'editor/:name', component: TestEditorComponent }
        ]
    }
];

@NgModule({
    imports: [
        ServiceModule,
        RouterModule.forChild(routes)
    ],
    exports: [
        RouterModule
    ]
})
export class TestRoutingModule {}