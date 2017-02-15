import {Routes, RouterModule} from "@angular/router";
import {NgModule} from "@angular/core";
import {CanActivateRoutes} from "../../service/can-activate-routes";
import {TestsComponent} from "./tests.component";
import {ServiceModule} from "../../service/service.module";
import {InfoOutletComponent} from "./test-detail/info/info.component";
import {TestDesignerOutletComponent} from "./test-detail/test-designer/test-designer-outlet.component";
import {TestDetailComponent} from "./test-detail/test.detail.component";
import {SourcesOutletComponent} from "./test-detail/sources/sources-outlet.component";
import {TestRunOutlet} from "./test-detail/run/test.execute.component";
import {TestResultOutletComponent} from "./test-detail/results/test.result.component";
import {CanActivateTestTab, CannotActivate} from "./route-guards";

const routes:Routes = [
    {
        path: 'tests',
        component: TestsComponent,
        canActivate: [CanActivateRoutes],
        children: [
            {
                path: ':name',
                component: TestDetailComponent,
                canActivateChild: [
                    CanActivateTestTab
                ],
                children: [
                    { path: '', redirectTo: 'info', pathMatch: 'full'},
                    { path: 'info', component: InfoOutletComponent},
                    { path: 'sources', component: SourcesOutletComponent},
                    { path: 'design', component: TestDesignerOutletComponent},
                    { path: 'run', component: TestRunOutlet},
                    { path: 'results', component: TestResultOutletComponent}
                ]
            }
        ]
    }
]

@NgModule({
    imports: [
        ServiceModule,
        RouterModule.forChild(routes)
    ],
    providers: [
        CanActivateTestTab,
        CannotActivate
    ],
    exports: [
        RouterModule
    ]
})
export class TestRoutingModule {}