import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LocationStrategy, PathLocationStrategy} from '@angular/common';
import {LogComponent}   from './components/log.component';
import {ComingComponent}   from './components/coming.component';
import {ReportComponent} from "./components/report/report.component";
import {DashboardComponent} from "./components/project/dashboard.component";
import {SetupComponent} from "./components/setup.component";
import {CanActivateRoutes} from "./service/can-activate-routes";
import {ServiceModule} from "./service/service.module";

const routes: Routes = [
    { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
    { path: 'dashboard', component: DashboardComponent, canActivate:[CanActivateRoutes]},
    { path: 'report', component: ReportComponent, canActivate:[CanActivateRoutes]},
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
