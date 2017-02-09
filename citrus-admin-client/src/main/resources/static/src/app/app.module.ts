import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpModule } from '@angular/http';
import { FormsModule} from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './components/app.component';
import { DashboardComponent } from './components/project/dashboard.component';
import { HeaderComponent } from './components/header.component';
import { SidebarComponent } from './components/sidebar.component';
import { LogComponent } from './components/log.component';
import { ComingComponent } from './components/coming.component';

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

import { AlertConsole } from './components/alert.console';
import { AlertDialog } from './components/alert.dialog';
import { TruncatePipe } from "./util/truncate.pipe";
import {FileSelectComponent} from "./components/file-select/file-select.component";
import {SetupComponent} from "./components/setup.component";
import {ConfigurationModule} from "./components/configuration/configuration.module";
import {UtilComponentsModule} from "./components/util/util.module";
import {ServiceModule} from "./service/service.module";
import {ProjectModule} from "./components/project/project.module";
import {UtilModule} from "./util/util.module";

@NgModule({
    imports: [
        BrowserModule,
        HttpModule,
        FormsModule,
        AppRoutingModule,
        ConfigurationModule,
        UtilComponentsModule,
        UtilModule,
        ServiceModule,
        ProjectModule
    ],
    declarations: [
        AppComponent,
        HeaderComponent,
        SidebarComponent,
        LogComponent,
        ComingComponent,
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
        AlertConsole,
        AlertDialog,
        SetupComponent,
        FileSelectComponent
    ],
    providers: [
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
