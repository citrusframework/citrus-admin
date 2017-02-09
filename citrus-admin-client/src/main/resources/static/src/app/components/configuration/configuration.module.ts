import {NgModule} from "@angular/core";
import {ConfigurationComponent} from "./configuration.component";
import {DataDictionaryComponent} from "./data-dictionary/data.dictionary.component";
import {EndpointsComponent} from "./endpoints/endpoints.component";
import {FunctionLibraryComponent} from "./function-library/function.library.component";
import {GlobalVariablesComponent} from "./global-variables/global.variables.component";
import {NamespaceContextComponent} from "./namespace-context/namespace.context.component";
import {SchemaRepositoryComponent} from "./schema-repository/schema.repository.component";
import {ValidationMatcherComponent} from "./validation-matcher/validation.matcher.component";
import {UtilComponentsModule} from "../util/util.module";
import {ConfigurationRoutingModule} from "./configuration-routing.module";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";



@NgModule({
    imports: [
        UtilComponentsModule,
        FormsModule,
        CommonModule,
        ConfigurationRoutingModule
    ],
    declarations: [
        ConfigurationComponent,
        DataDictionaryComponent,
        EndpointsComponent,
        FunctionLibraryComponent,
        GlobalVariablesComponent,
        NamespaceContextComponent,
        SchemaRepositoryComponent,
        ValidationMatcherComponent
    ],
    exports: [
        ConfigurationComponent
    ]
})
export class ConfigurationModule {

}