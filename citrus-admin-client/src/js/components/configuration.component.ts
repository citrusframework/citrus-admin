import {Component, OnInit} from 'angular2/core';
import {NgIf, NgFor, NgModel} from 'angular2/common';
import {HTTP_PROVIDERS} from 'angular2/http';
import {ConfigService} from "../service/config.service";
import {Pills, Pill} from "../components/util/pills";
import {EndpointsComponent} from "../components/config/endpoints.component"
import {GlobalVariablesComponent} from "../components/config/global.variables.component"

@Component({
    templateUrl: 'templates/config.html',
    providers: [ConfigService, HTTP_PROVIDERS],
    directives: [NgIf, NgFor, NgModel, Pills, Pill, EndpointsComponent, GlobalVariablesComponent]
})
export class ConfigurationComponent {
    errorMessage: string;
}