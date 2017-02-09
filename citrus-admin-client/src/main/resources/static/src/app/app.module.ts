import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpModule } from '@angular/http';
import { FormsModule} from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './components/app.component';
import { DashboardComponent } from './components/dashboard.component';
import { HeaderComponent } from './components/header.component';
import { LogComponent } from './components/log.component';
import { ComingComponent } from './components/coming.component';

import { SourceCodeComponent } from './components/source.code.component';
import { TestDetailComponent } from './components/test.detail.component';
import { TestExecuteComponent } from './components/test.execute.component';
import { TestMessageComponent } from './components/test.message.component';
import { TestProgressComponent } from './components/test.progress.component';
import { TestListGroupComponent } from './components/test.listgroup.component';
import { TestsComponent } from './components/tests.component';
import { TestReportComponent } from './components/test.report.component';
import { TestActionComponent } from './components/design/test.action.component';
import { TestContainerComponent } from './components/design/test.container.component';
import { TestDesignerComponent } from './components/design/test.designer.component';
import { TestTransitionComponent } from './components/design/test.transition.component';

import { ProjectSettingsComponent } from './components/project.settings.component';
import { AlertConsole } from './components/alert.console';
import { AlertDialog } from './components/alert.dialog';
import { TruncatePipe } from "./util/truncate.pipe";
import { AlertService } from "./service/alert.service";
import { ConfigService } from "./service/config.service";
import { EndpointService } from "./service/endpoint.service";
import { ProjectService } from "./service/project.service";
import { ReportService } from "./service/report.service";
import { SpringBeanService } from "./service/springbean.service";
import { TestService } from "./service/test.service";
import {ProjectSetupService} from "./service/project.setup.service";
import {FileSelectComponent} from "./components/file-select/file-select.component";
import {SetupComponent} from "./components/setup.component";
import {ConfigurationModule} from "./components/configuration/configuration.module";
import {UtilModule} from "./components/util/util.module";

@NgModule({
    imports: [
        BrowserModule,
        HttpModule,
        FormsModule,
        AppRoutingModule,
        ConfigurationModule,
        UtilModule
    ],
    declarations: [
        AppComponent,
        DashboardComponent,
        HeaderComponent,
        LogComponent,
        ComingComponent,
        SourceCodeComponent,
        TestDetailComponent,
        TestExecuteComponent,
        TestMessageComponent,
        TestProgressComponent,
        TestListGroupComponent,
        TestReportComponent,
        TestsComponent,
        TestActionComponent,
        TestContainerComponent,
        TestDesignerComponent,
        TestTransitionComponent,
        ProjectSettingsComponent,
        AlertConsole,
        AlertDialog,
        TruncatePipe,
        SetupComponent,
        FileSelectComponent
    ],
    providers: [
        AlertService,
        ConfigService,
        EndpointService,
        ProjectService,
        ReportService,
        SpringBeanService,
        TestService,
        ProjectSetupService
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
