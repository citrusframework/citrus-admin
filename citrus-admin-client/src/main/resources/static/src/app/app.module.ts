import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {HttpModule} from '@angular/http';
import {FormsModule} from '@angular/forms';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './components/app.component';
import {HeaderComponent} from './components/header.component';
import {SidebarComponent} from './components/sidebar.component';
import {LogComponent} from './components/log.component';
import {ComingComponent} from './components/coming.component';
import {AboutComponent} from './components/about.component';
import {DashboardComponent} from './components/project/dashboard.component';
import {ReportComponent} from './components/report/report.component';

import {OpenProjectComponent} from "./components/project/project.open.component";
import {NewProjectComponent} from "./components/project/project.new.component";
import {SetupComponent} from "./components/setup.component";
import {ConfigurationModule} from "./components/configuration/configuration.module";
import {UtilComponentsModule} from "./components/util/util.module";
import {ServiceModule} from "./service/service.module";
import {SettingsModule} from "./components/settings/settings.module";
import {UtilModule} from "./util/util.module";
import {TestModule} from "./components/test/test.module";
import {StateModule} from "./state.module";

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
        SettingsModule,
        TestModule,
        StateModule
    ],
    declarations: [
        AppComponent,
        HeaderComponent,
        SidebarComponent,
        LogComponent,
        ComingComponent,
        AboutComponent,
        DashboardComponent,
        ReportComponent,
        OpenProjectComponent,
        NewProjectComponent,
        SetupComponent
    ],
    providers: [
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
