import {NgModule} from "@angular/core";
import {SettingsComponent} from "./settings.component";
import {SettingsRoutingModule} from "./settings-routing.module";
import {ConnectorSettingsComponent} from "./connector/connector.settings.component";
import {ProjectSettingsComponent} from "./project/project.settings.component";
import {ModuleSettingsComponent} from "./modules/module.settings.component";
import {CommonModule} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {UtilModule} from "../../util/util.module";
import {UtilComponentsModule} from "../util/util.module";

const components = [
    SettingsComponent,
    ProjectSettingsComponent,
    ModuleSettingsComponent,
    ConnectorSettingsComponent
];

@NgModule({
    imports: [
        SettingsRoutingModule,
        CommonModule,
        FormsModule,
        UtilModule,
        UtilComponentsModule
    ],
    exports: components,
    declarations: components
})
export class SettingsModule {}