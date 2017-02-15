import {NgModule} from "@angular/core";
import {RouterModule, Routes} from "@angular/router";
import {CanActivateRoutes} from "../../service/can-activate-routes";
import {ServiceModule} from "../../service/service.module";
import {ConnectorSettingsComponent} from "./connector/connector.settings.component";
import {ProjectSettingsComponent} from "./project/project.settings.component";
import {SettingsComponent} from "./settings.component";

const routes:Routes = [
    {
        path: 'settings',
        component: SettingsComponent,
        canActivate: [CanActivateRoutes],
        children: [
            { path: '', redirectTo: 'project', pathMatch: 'full'},
            { path: 'project', component: ProjectSettingsComponent},
            { path: 'connector', component: ConnectorSettingsComponent}
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
export class SettingsRoutingModule {}