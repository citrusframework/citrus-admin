import { NgModule }             from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LocationStrategy, PathLocationStrategy} from '@angular/common';
import { DashboardComponent }   from './components/dashboard.component';
import { LogComponent }   from './components/log.component';
import { ComingComponent }   from './components/coming.component';
import { TestsComponent }   from './components/tests.component';
import { TestReportComponent } from "./components/test.report.component";
import {SetupComponent} from "./components/setup.component";
import {CanActivateRoutes} from "./service/can-activate-routes";
import {ServiceModule} from "./service/service.module";

const routes: Routes = [
    { path: '', redirectTo: '/project', pathMatch: 'full' },
    { path: 'tests', component: TestsComponent, canActivate:[CanActivateRoutes]},
    { path: 'tests/:name', component: TestsComponent, canActivate:[CanActivateRoutes]},
    { path: 'report', component: TestReportComponent, canActivate:[CanActivateRoutes]},
    { path: 'new', component: ComingComponent},
    { path: 'about', component: ComingComponent },
    { path: 'log', component: LogComponent },
    { path: 'setup', component: SetupComponent}
];

@NgModule({
    imports: [
        RouterModule.forRoot(routes),
        ServiceModule
    ],
    exports: [ RouterModule ],
    providers:    [
        {provide: LocationStrategy, useClass: PathLocationStrategy}
    ],
})
export class AppRoutingModule {}
