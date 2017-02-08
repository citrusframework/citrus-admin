import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpModule } from '@angular/http';
import { FormsModule} from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './components/app.component';
import { DashboardComponent } from './components/dashboard.component';
import { HeaderComponent } from './components/header.component';
import { SidebarComponent } from './components/sidebar.component';
import { LogComponent } from './components/log.component';
import { ComingComponent } from './components/coming.component';

import { ConfigurationComponent } from './components/configuration/configuration.component';
import { DataDictionaryComponent } from './components/configuration/data-dictionary/data.dictionary.component';
import { EndpointsComponent } from './components/configuration/endpoints/endpoints.component';
import { FunctionLibraryComponent } from './components/configuration/function-library/function.library.component';
import { GlobalVariablesComponent } from './components/configuration/global-variables/global.variables.component';
import { NamespaceContextComponent } from './components/configuration/namespace-context/namespace.context.component';
import { SchemaRepositoryComponent } from './components/configuration/schema-repository/schema.repository.component';
import { ValidationMatcherComponent } from './components/configuration/validation-matcher/validation.matcher.component';

import { SourceCodeComponent } from './components/source.code.component';
import { TestDetailComponent } from './components/test.detail.component';
import { TestExecuteComponent } from './components/test.execute.component';
import { TestResultComponent } from './components/test.result.component';
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
import { AutoComplete } from './components/util/autocomplete';
import { Dialog } from './components/util/dialog';
import { Pills, Pill } from './components/util/pills';
import { SidebarMenu, MenuItem } from './components/util/sidebar';
import { Tabs, Tab } from './components/util/tabs';
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

@NgModule({
    imports: [
        BrowserModule,
        HttpModule,
        FormsModule,
        AppRoutingModule
    ],
    declarations: [
        AppComponent,
        DashboardComponent,
        HeaderComponent,
        SidebarComponent,
        LogComponent,
        ComingComponent,
        ConfigurationComponent,
        DataDictionaryComponent,
        EndpointsComponent,
        FunctionLibraryComponent,
        GlobalVariablesComponent,
        NamespaceContextComponent,
        SchemaRepositoryComponent,
        ValidationMatcherComponent,
        SourceCodeComponent,
        TestDetailComponent,
        TestExecuteComponent,
        TestResultComponent,
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
        AutoComplete,
        Dialog,
        Pills,
        Pill,
        SidebarMenu,
        MenuItem,
        Tabs,
        Tab,
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
