import { NgModule }             from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LocationStrategy, HashLocationStrategy} from '@angular/common';

import { DashboardComponent }   from './components/dashboard.component';
import { LogComponent }   from './components/log.component';
import { ComingComponent }   from './components/coming.component';
import { ProjectSettingsComponent }   from './components/project.settings.component';
import { ConfigurationComponent }   from './components/configuration.component';
import { TestsComponent }   from './components/tests.component';
import { TestReportComponent } from "./components/test.report.component";

const routes: Routes = [
    { path: '', redirectTo: '/project', pathMatch: 'full' },
    { path: 'project', component: DashboardComponent },
    { path: 'project/settings', component: ProjectSettingsComponent},
    { path: 'project/settings/:activeTab', component: ProjectSettingsComponent},
    { path: 'configuration', component: ConfigurationComponent},
    { path: 'configuration/:activeTab', component: ConfigurationComponent},
    { path: 'tests', component: TestsComponent},
    { path: 'tests/:name', component: TestsComponent},
    { path: 'report', component: TestReportComponent},
    { path: 'new', component: ComingComponent},
    { path: 'about', component: ComingComponent },
    { path: 'log', component: LogComponent },
];

@NgModule({
    imports: [ RouterModule.forRoot(routes) ],
    exports: [ RouterModule ],
    providers:    [
        {provide: LocationStrategy, useClass: HashLocationStrategy}
    ],
})
export class AppRoutingModule {}
