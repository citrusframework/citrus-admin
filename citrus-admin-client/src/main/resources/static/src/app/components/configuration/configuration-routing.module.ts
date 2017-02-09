import {NgModule} from "@angular/core";
import {RouterModule, Routes} from "@angular/router";
import {ConfigurationComponent} from "./configuration.component";
import {EndpointsComponent} from "./endpoints/endpoints.component";
import {SchemaRepositoryComponent} from "./schema-repository/schema.repository.component";
import {FunctionLibraryComponent} from "./function-library/function.library.component";
import {ValidationMatcherComponent} from "./validation-matcher/validation.matcher.component";
import {DataDictionaryComponent} from "./data-dictionary/data.dictionary.component";
import {NamespaceContextComponent} from "./namespace-context/namespace.context.component";
import {GlobalVariablesComponent} from "./global-variables/global.variables.component";
import {ServiceModule} from "../../service/service.module";
import {CanActivateRoutes} from "../../service/can-activate-routes";

const configurationRoutes:Routes = [
    {
        path: 'configuration',
        component: ConfigurationComponent,
        canActivate: [CanActivateRoutes],
        children: [
            { path: 'endpoints', component: EndpointsComponent},
            { path: 'schema-definition', component: SchemaRepositoryComponent},
            { path: 'functions', component: FunctionLibraryComponent},
            { path: 'validation-matcher', component: ValidationMatcherComponent},
            { path: 'data-dictionaries', component: DataDictionaryComponent},
            { path: 'namespaces', component: NamespaceContextComponent},
            { path: 'global-variables', component: GlobalVariablesComponent}
        ]
    },
]

@NgModule({
    imports: [
        RouterModule.forChild(configurationRoutes),
        ServiceModule
    ],
    exports: [
        RouterModule
    ]
})
export class ConfigurationRoutingModule {}