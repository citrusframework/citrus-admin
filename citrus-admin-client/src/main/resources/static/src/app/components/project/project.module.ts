import {NgModule} from "@angular/core";
import {ProjectSettingsComponent} from "./settings/project.settings.component";
import {ProjectRoutingModule} from "./project-routing.module";
import {DashboardComponent} from "./dashboard.component";
import {ConnectorComponent} from "./settings/connector/connector.component";
import {BuildComponent} from "./settings/build/build.component";
import {SourcesComponent} from "./settings/sources/sources.component";
import {ProjectComponent} from "./settings/project/project.component";
import {CommonModule} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {UtilModule} from "../../util/util.module";
import {UtilComponentsModule} from "../util/util.module";

const components = [
    ProjectSettingsComponent,
    DashboardComponent,
    ConnectorComponent,
    BuildComponent,
    SourcesComponent,
    ProjectComponent
]

@NgModule({
    imports: [
        ProjectRoutingModule,
        CommonModule,
        FormsModule,
        UtilModule,
        UtilComponentsModule
    ],
    exports: components,
    declarations: components
})
export class ProjectModule {}