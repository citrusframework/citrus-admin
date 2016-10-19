import {Component, OnInit} from 'angular2/core';
import {ConfigService} from '../../service/config.service';
import {FunctionLibrary, Function} from "../../model/function.library";
import {Alert} from "../../model/alert";
import {AlertService} from "../../service/alert.service";

@Component({
    selector: 'function-library',
    templateUrl: 'app/components/config/function-library.html',
    providers: [ConfigService]
})
export class FunctionLibraryComponent implements OnInit {

    constructor(private _configService: ConfigService,
                private _alertService: AlertService) {
        this.libraries = [];
        this.newFunction = new Function();
    }

    newFunction: Function;
    newLibrary: FunctionLibrary;
    selectedLibrary: FunctionLibrary;
    libraries: FunctionLibrary[];

    ngOnInit() {
        this.getLibraries();
    }

    getLibraries() {
        this._configService.getFunctionLibraries()
            .subscribe(
                libraries => this.libraries = libraries,
                error => this.notifyError(<any>error));
    }

    selectLibrary(endpoint: FunctionLibrary) {
        this.selectedLibrary = endpoint;
    }

    removeLibrary(library: FunctionLibrary, event) {
        this._configService.deleteComponent(library.id)
            .subscribe(
                response => {
                    this.libraries.splice(this.libraries.indexOf(library), 1);
                    this.notifySuccess("Removed function library '" + library.id + "'");
                },
                error => this.notifyError(<any>error));

        event.stopPropagation();
        return false;
    }

    removeFunction(selected: Function) {
        if (this.selectedLibrary) {
            this.selectedLibrary.functions.splice(this.selectedLibrary.functions.indexOf(selected), 1);
        } else {
            this.newLibrary.functions.splice(this.newLibrary.functions.indexOf(selected), 1);
        }
    }

    addFunction() {
        if (this.selectedLibrary) {
            this.selectedLibrary.functions.push(this.newFunction);
        } else {
            this.newLibrary.functions.push(this.newFunction);
        }

        this.newFunction = new Function();
    }

    initLibrary() {
        this.newLibrary = new FunctionLibrary();
    }

    createLibrary() {
        this._configService.createFunctionLibrary(this.newLibrary)
            .subscribe(
                response => {
                    this.notifySuccess("Created new function library '" + this.newLibrary.id + "'");
                    this.libraries.push(this.newLibrary); this.newLibrary = undefined;
                },
                error => this.notifyError(<any>error));
    }

    saveLibrary() {
        this._configService.updateFunctionLibrary(this.selectedLibrary)
            .subscribe(
                response => {
                    this.notifySuccess("Successfully saved function library '" + this.selectedLibrary.id + "'");
                    this.selectedLibrary = undefined;
                },
                error => this.notifyError(<any>error));
    }

    cancel() {
        if (this.selectedLibrary) {
            this.selectedLibrary = undefined;
            this.getLibraries();
        } else {
            this.newLibrary = undefined;
        }
    }

    notifySuccess(message: string) {
        this._alertService.add(new Alert("success", message, true));
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error.message, false));
    }
}