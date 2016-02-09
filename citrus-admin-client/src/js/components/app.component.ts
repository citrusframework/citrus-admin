import {Component} from 'angular2/core';
import {View} from 'angular2/core';
import {RouteConfig, ROUTER_DIRECTIVES} from "angular2/router";

import {HeaderComponent} from './header.component';
import {DashboardComponent} from './dashboard.component';
import {ConfigurationComponent} from './configuration.component';
import {ComingComponent} from "./coming.component";

@Component({
    selector: 'app'
})
@View({
    directives: [HeaderComponent, ROUTER_DIRECTIVES],
    templateUrl: 'templates/app.html'
})
@RouteConfig([
    {path:'/', name: 'Home', component: DashboardComponent},
    {path:'/project', name: 'Project', component: ComingComponent},
    {path:'/config', name: 'Configuration', component: ConfigurationComponent},
    {path:'/tests', name: 'Tests', component: ComingComponent},
    {path:'/stats', name: 'Stats', component: ComingComponent},
    {path:'/settings', name: 'Settings', component: ComingComponent},
    {path:'/about', name: 'About', component: ComingComponent}
])
export class AppComponent { }