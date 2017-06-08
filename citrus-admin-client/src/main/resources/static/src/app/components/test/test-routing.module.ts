import {Routes, RouterModule} from "@angular/router";
import {NgModule} from "@angular/core";
import {CanActivateRoutes} from "../../service/can-activate-routes";
import {TestsComponent} from "./tests.component";
import {TestEditorComponent} from "./test.editor.component";
import {TestListComponent} from "./test.list.component";
import {ServiceModule} from "../../service/service.module";
import {InfoOutletComponent} from "./detail/info/info.component";
import {TestDesignerOutletComponent} from "./detail/designer/test-designer-outlet.component";
import {SourcesOutletComponent} from "./detail/sources/sources-outlet.component";
import {TestRunOutlet} from "./detail/run/test.run.component";
import {CanActivateTestTabChild, CannotActivate, CanActivateTestEditor} from "./route-guards";
import {TestGroupRunComponent} from "./test.group.run.component";
import {TestDetailComponent} from "./detail/test.detail.component";
import {TestDetailRouteResolver} from "./detail/route-resolver";

const routes: Routes = [
    {
        path: 'tests',
        component: TestsComponent,
        canActivate: [CanActivateRoutes],
        children: [
            {path: '', redirectTo: 'run', pathMatch: 'full'},
            {path: 'list', component: TestListComponent},
            {path: 'run', component: TestGroupRunComponent},
            {
                path: 'detail', component: TestEditorComponent,
                children: [
                    {
                        path: ':name',
                        component: TestDetailComponent,
                        resolve: {
                           // detail: TestDetailRouteResolver
                        },
                        canActivate: [CanActivateRoutes],
                        children: [
                            {path: 'info', component: InfoOutletComponent},
                            {path: 'sources', component: SourcesOutletComponent},
                            {path: 'design', component: TestDesignerOutletComponent},
                            {path: 'run', component: TestRunOutlet}
                        ],
                    }
                ]
            },

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
        CanActivateTestEditor,
        TestDetailRouteResolver
    ],
    exports: [
        RouterModule
    ]
})
export class TestRoutingModule {
}