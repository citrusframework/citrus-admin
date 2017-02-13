import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {ServiceModule} from "../../service/service.module";
import {TestsComponent} from "./tests.component";
import {SourcesComponent} from "../project/settings/sources/sources.component";
import {SourceCodeComponent} from "./test-detail/sources/source.code.component";
import {TestDetailComponent} from "./test-detail/test.detail.component";
import {TestExecuteComponent, TestRunOutlet} from "./test-detail/run/test.execute.component";
import {TestListGroupComponent} from "./test.listgroup.component";
import {TestMessageComponent} from "./test-detail/run/test.message.component";
import {TestProgressComponent} from "./test-detail/run/test.progress.component";
import {TestReportComponent} from "./test-report/test.report.component";
import {UtilComponentsModule} from "../util/util.module";
import {TestTransitionComponent} from "./test-detail/test-designer/test.transition.component";
import {TestDesignerComponent} from "./test-detail/test-designer/test.designer.component";
import {TestContainerComponent} from "./test-detail/test-designer/test.container.component";
import {TestActionComponent} from "./test-detail/test-designer/test.action.component";
import {UtilModule} from "../../util/util.module";
import {FormsModule} from "@angular/forms";
import {TestRoutingModule} from "./test-routing.module";
import {InfoComponent, InfoOutletComponent} from "./test-detail/info/info.component";
import {TestDesignerOutletComponent} from "./test-detail/test-designer/test-designer-outlet.component";
import {TestStateActions, TestStateEffects, TestStateService} from "./test.state";
import {EffectsModule} from "@ngrx/effects";
import {SourcesOutletComponent} from "./test-detail/sources/sources-outlet.component";
import {TestResultOutletComponent} from "./test-detail/results/test-result.component";

const components = [
    TestsComponent,
    SourceCodeComponent,
    TestDetailComponent,
    TestExecuteComponent,
    TestListGroupComponent,
    TestMessageComponent,
    TestProgressComponent,
    TestReportComponent,
    TestActionComponent,
    TestContainerComponent,
    TestDesignerComponent,
    TestTransitionComponent,
    InfoComponent,
    TestDesignerOutletComponent,
    SourcesOutletComponent,
    InfoOutletComponent,
    TestRunOutlet,
    TestResultOutletComponent
]

@NgModule({
    imports: [
        UtilComponentsModule,
        UtilModule,
        FormsModule,
        CommonModule,
        ServiceModule,
        TestRoutingModule,
        EffectsModule.run(TestStateEffects)
    ],
    providers: [
        TestStateActions,
        TestStateService,
        TestStateEffects
    ],
    declarations: components,
    exports: components
})
export class TestModule {
}