import {Component, OnInit} from 'angular2/core';
import {ConfigService} from '../../service/config.service';
import {Variable} from "../../model/variable";
import {GlobalVariables} from "../../model/global.variables";

@Component({
    selector: 'global-variables',
    templateUrl: 'app/components/config/global-variables.html',
    providers: [ConfigService]
})
export class GlobalVariablesComponent implements OnInit {

    constructor(private _configService: ConfigService) {
        this.globalVariables = new GlobalVariables();
        this.newVariable = new Variable();
    }

    errorMessage: string;
    newVariable: Variable;
    globalVariables: GlobalVariables;

    ngOnInit() {
        this.getVariables();
    }

    getVariables() {
        this._configService.getGlobalVariables()
            .subscribe(
                globalVariables => this.globalVariables = globalVariables,
                error => this.errorMessage = <any>error);
    }

    addVariable() {
        this.globalVariables.variables.push(this.newVariable);

        this._configService.updateGlobalVariables(this.globalVariables)
            .subscribe(
                response => { this.newVariable = new Variable() },
                error => this.errorMessage = <any>error);
    }

    removeVariable(variable: Variable, event) {
        this.globalVariables.variables.splice(this.globalVariables.variables.indexOf(variable), 1);

        this._configService.updateGlobalVariables(this.globalVariables)
            .subscribe(
                response => { this.newVariable = new Variable() },
                error => this.errorMessage = <any>error);

        event.stopPropagation();
        return false;
    }

    cancel() {
        this.newVariable = new Variable();
    }
}