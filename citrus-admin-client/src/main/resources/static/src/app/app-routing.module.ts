import { NgModule }             from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LocationStrategy, PathLocationStrategy} from '@angular/common';
import { DashboardComponent }   from './components/dashboard.component';
import { LogComponent }   from './components/log.component';
import { ComingComponent }   from './components/coming.component';
import { ProjectSettingsComponent }   from './components/project.settings.component';
import { TestsComponent }   from './components/tests.component';
import { TestReportComponent } from "./components/test.report.component";
import {SetupComponent} from "./components/setup.component";
import {CanActivateRoutes} from "./service/can-activate-routes";

const routes: Routes = [
    { path: '', redirectTo: '/project', pathMatch: 'full' },
    { path: 'project', component: DashboardComponent, canActivate:[CanActivateRoutes] },
    { path: 'project/settings', component: ProjectSettingsComponent},
    { path: 'project/settings/:activeTab', component: ProjectSettingsComponent},
    { path: 'tests', component: TestsComponent},
    { path: 'tests/:name', component: TestsComponent},
    { path: 'report', component: TestReportComponent},
    { path: 'new', component: ComingComponent},
    { path: 'about', component: ComingComponent },
    { path: 'log', component: LogComponent },
    { path: 'setup', component: SetupComponent}
];

@NgModule({
    imports: [ RouterModule.forRoot(routes) ],
    exports: [ RouterModule ],
    providers:    [
        {provide: LocationStrategy, useClass: PathLocationStrategy}
    ],
})
export class AppRoutingModule {}
