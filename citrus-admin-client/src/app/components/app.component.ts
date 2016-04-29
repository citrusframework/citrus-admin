import {Component} from 'angular2/core';
import {RouteConfig, ROUTER_DIRECTIVES} from "angular2/router";

import {HeaderComponent} from './header.component';
import {DashboardComponent} from './dashboard.component';
import {ProjectSettingsComponent} from './project.settings.component';
import {ConfigurationComponent} from './configuration.component';
import {TestsComponent} from './tests.component';
import {ComingComponent} from "./coming.component";

@Component({
    selector: 'app',
    directives: [HeaderComponent, ROUTER_DIRECTIVES],
    templateUrl: 'app/components/app.html'
})
@RouteConfig([
    {path:'/', name: 'Dashboard', component: DashboardComponent},
    {path:'/settings', name: 'Settings', component: ProjectSettingsComponent},
    {path:'/config', name: 'Configuration', component: ConfigurationComponent},
    {path:'/tests', name: 'Tests', component: TestsComponent},
    {path:'/stats', name: 'Stats', component: ComingComponent},
    {path:'/new', name: 'New', component: ComingComponent},
    {path:'/about', name: 'About', component: ComingComponent}
])
export class AppComponent { }