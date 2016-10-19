import {Component, OnInit} from 'angular2/core';
import {NgIf, NgFor, NgModel} from 'angular2/common';
import {HTTP_PROVIDERS} from 'angular2/http';
import {ConfigService} from "../service/config.service";
import {Pills, Pill} from "./util/pills";
import {EndpointsComponent} from "./config/endpoints.component"
import {GlobalVariablesComponent} from "./config/global.variables.component"
import {NamespaceContextComponent} from "./config/namespace.context.component"
import {FunctionLibraryComponent} from "./config/function.library.component"
import {ValidationMatcherComponent} from "./config/validation.matcher.component"
import {DataDictionaryComponent} from "./config/data.dictionary.component"
import {SchemaRepositoryComponent} from "./config/schema.repository.component"
import {RouteParams} from 'angular2/router';
import {AlertConsole} from "./alert.console";

@Component({
    templateUrl: 'app/components/config.html',
    providers: [ConfigService, HTTP_PROVIDERS],
    directives: [NgIf, NgFor, NgModel, Pills, Pill,
        EndpointsComponent, GlobalVariablesComponent, NamespaceContextComponent,
        FunctionLibraryComponent, ValidationMatcherComponent, DataDictionaryComponent,
        SchemaRepositoryComponent, AlertConsole]
})
export class ConfigurationComponent {
    constructor(routeParams: RouteParams) {
        if (routeParams.get('show')) {
            this.active = routeParams.get('show');
        }
    }

    active = 'endpoints';

    isActive(name: string) {
        return this.active === name;
    }
}