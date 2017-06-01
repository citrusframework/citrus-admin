import {NgModule} from "@angular/core";
import {ConfigurationComponent} from "./configuration.component";
import {DataDictionaryComponent} from "./data-dictionary/data.dictionary.component";
import {FunctionLibraryComponent} from "./function-library/function.library.component";
import {GlobalVariablesComponent} from "./global-variables/global.variables.component";
import {NamespaceContextComponent} from "./namespace-context/namespace.context.component";
import {SpringBeansComponent} from "./spring-beans/spring.beans.component";
import {SpringContextComponent} from "./spring-context/spring.context.component";
import {ValidationMatcherComponent} from "./validation-matcher/validation.matcher.component";
import {UtilComponentsModule} from "../util/util.module";
import {ConfigurationRoutingModule} from "./configuration-routing.module";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {UtilModule} from "../../util/util.module";
import {SchemaRepositoryModule} from "./schema-repository/schema-repository.module";
import {EndpointModule} from "./endpoints/endpoint.module";

@NgModule({
    imports: [
        UtilModule,
        UtilComponentsModule,
        FormsModule,
        CommonModule,
        ConfigurationRoutingModule,
        SchemaRepositoryModule,
        EndpointModule,
        ReactiveFormsModule
    ],
    declarations: [
        ConfigurationComponent,
        DataDictionaryComponent,
        FunctionLibraryComponent,
        GlobalVariablesComponent,
        NamespaceContextComponent,
        SpringBeansComponent,
        SpringContextComponent,
        ValidationMatcherComponent
    ],
    exports: [
        ConfigurationComponent
    ]
})
export class ConfigurationModule {

}