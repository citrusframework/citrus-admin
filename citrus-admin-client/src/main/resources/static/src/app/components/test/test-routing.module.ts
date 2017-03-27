import {Routes, RouterModule} from "@angular/router";
import {NgModule} from "@angular/core";
import {CanActivateRoutes} from "../../service/can-activate-routes";
import {TestsComponent} from "./tests.component";
import {TestEditorComponent} from "./test.editor.component";
import {TestListComponent} from "./test.list.component";
import {ServiceModule} from "../../service/service.module";
import {InfoOutletComponent} from "./detail/info/info.component";
import {TestDesignerOutletComponent} from "./detail/designer/test-designer-outlet.component";
import {TestDetailComponent} from "./detail/test.detail.component";
import {SourcesOutletComponent} from "./detail/sources/sources-outlet.component";
import {TestRunOutlet} from "./detail/run/test.run.component";
import {TestResultOutletComponent} from "./detail/results/test.result.component";
import {CanActivateTestTabChild, CannotActivate, CanActivateTestEditor} from "./route-guards";
import {TestGroupRunComponent} from "./test.group.run.component";

const routes:Routes = [
    {
        path: 'tests',
        component: TestsComponent,
        canActivate: [CanActivateRoutes],
        children: [
            { path: '', redirectTo: 'list', pathMatch: 'full'},
            { path: 'list', component: TestListComponent},
            { path: 'run', component: TestGroupRunComponent},
            {
                path: 'editor',
                component: TestEditorComponent,
                canActivate: [CanActivateRoutes],
                children: [
                    {
                        path: ':name',
                        component: TestDetailComponent,
                        canActivateChild: [CanActivateTestTabChild],
                        children: [
                            { path: '', component: InfoOutletComponent },
                            { path: 'info', component: InfoOutletComponent},
                            { path: 'sources', component: SourcesOutletComponent},
                            { path: 'design', component: TestDesignerOutletComponent},
                            { path: 'run', component: TestRunOutlet},
                            { path: 'results', component: TestResultOutletComponent}
                        ],
                    }
                ]
            }
        ]
    }
];

@NgModule({
    imports: [
        ServiceModule,
        RouterModule.forChild(routes)
    ],
    providers: [
        CanActivateTestTabChild,
        CannotActivate,
        CanActivateTestEditor
    ],
    exports: [
        RouterModule
    ]
})
export class TestRoutingModule {}