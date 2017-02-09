import {NgModule} from "@angular/core";
import {ProjectSettingsComponent} from "./settings/project.settings.component";
import {RouterModule, Routes} from "@angular/router";
import {CanActivateRoutes} from "../../service/can-activate-routes";
import {DashboardComponent} from "./dashboard.component";
import {ServiceModule} from "../../service/service.module";
import {BuildComponent} from "./settings/build/build.component";
import {ProjectComponent} from "./settings/project/project.component";
import {ConnectorComponent} from "./settings/connector/connector.component";
import {SourcesComponent} from "./settings/sources/sources.component";

const routes:Routes = [
    { path: 'project', component: DashboardComponent, canActivate:[CanActivateRoutes] },
    {
        path: 'project/settings',
        component: ProjectSettingsComponent,
        canActivate:[CanActivateRoutes],
        children: [
            { path: 'project', component: ProjectComponent},
            { path: 'connector', component: ConnectorComponent},
            { path: 'sources', component: SourcesComponent},
            { path: 'build', component: BuildComponent}
        ]
    },
]

@NgModule({
    imports: [
        ServiceModule,
        RouterModule.forChild(routes)
    ],
    exports: [
        RouterModule
    ]
})
export class ProjectRoutingModule {}