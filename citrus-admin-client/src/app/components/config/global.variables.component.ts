import {Component, OnInit} from 'angular2/core';
import {ConfigService} from '../../service/config.service';
import {Variable} from "../../model/variable";
import {GlobalVariables} from "../../model/global.variables";
import {Alert} from "../../model/alert";
import {AlertService} from "../../service/alert.service";

@Component({
    selector: 'global-variables',
    templateUrl: 'app/components/config/global-variables.html',
    providers: [ConfigService]
})
export class GlobalVariablesComponent implements OnInit {

    constructor(private _configService: ConfigService,
                private _alertService: AlertService) {
        this.globalVariables = new GlobalVariables();
        this.newVariable = new Variable();
    }

    newVariable: Variable;
    globalVariables: GlobalVariables;

    ngOnInit() {
        this.getVariables();
    }

    getVariables() {
        this._configService.getGlobalVariables()
            .subscribe(
                globalVariables => this.globalVariables = globalVariables,
                error => this.notifyError(<any>error));
    }

    addVariable() {
        this.globalVariables.variables.push(this.newVariable);

        this._configService.updateGlobalVariables(this.globalVariables)
            .subscribe(
                response => {
                    this.notifySuccess("Created variable '" + this.newVariable.name + "'");
                    this.newVariable = new Variable();
                },
                error => this.notifyError(<any>error));
    }

    removeVariable(variable: Variable, event) {
        this.globalVariables.variables.splice(this.globalVariables.variables.indexOf(variable), 1);

        this._configService.updateGlobalVariables(this.globalVariables)
            .subscribe(
                response => {
                    this.newVariable = new Variable();
                    this.notifySuccess("Removed variable '" + variable.name + "'");
                },
                error => this.notifyError(<any>error));

        event.stopPropagation();
        return false;
    }

    cancel() {
        this.newVariable = new Variable();
    }

    notifySuccess(message: string) {
        this._alertService.add(new Alert("success", message, true));
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error.message, false));
    }
}