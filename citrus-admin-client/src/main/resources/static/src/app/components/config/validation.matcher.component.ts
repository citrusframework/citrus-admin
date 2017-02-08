import {Component, OnInit} from '@angular/core';
import {ConfigService} from '../../service/config.service';
import {ValidationMatcherLibrary, ValidationMatcher} from "../../model/validation.matcher.library";
import {Alert} from "../../model/alert";
import {AlertService} from "../../service/alert.service";

@Component({
    selector: 'validation-matcher',
    templateUrl: 'validation-matcher.html',
    providers: [ConfigService]
})
export class ValidationMatcherComponent implements OnInit {

    constructor(private _configService: ConfigService,
                private _alertService: AlertService) {
        this.libraries = [];
        this.newValidationMatcher = new ValidationMatcher();
    }

    newValidationMatcher: ValidationMatcher;
    newLibrary: ValidationMatcherLibrary;
    selectedLibrary: ValidationMatcherLibrary;
    libraries: ValidationMatcherLibrary[];

    ngOnInit() {
        this.getLibraries();
    }

    getLibraries() {
        this._configService.getValidationMatcherLibraries()
            .subscribe(
                libraries => this.libraries = libraries,
                error => this.notifyError(<any>error));
    }

    selectLibrary(endpoint: ValidationMatcherLibrary) {
        this.selectedLibrary = endpoint;
    }

    removeLibrary(library: ValidationMatcherLibrary, event:MouseEvent) {
        this._configService.deleteComponent(library.id)
            .subscribe(
                response => {
                    this.libraries.splice(this.libraries.indexOf(library), 1);
                    this.notifySuccess("Removed validation matcher library '" + library.id + "'");
                },
                error => this.notifyError(<any>error));

        event.stopPropagation();
        return false;
    }

    removeValidationMatcher(selected: ValidationMatcher) {
        if (this.selectedLibrary) {
            this.selectedLibrary.matchers.splice(this.selectedLibrary.matchers.indexOf(selected), 1);
        } else {
            this.newLibrary.matchers.splice(this.newLibrary.matchers.indexOf(selected), 1);
        }
    }

    addValidationMatcher() {
        if (this.selectedLibrary) {
            this.selectedLibrary.matchers.push(this.newValidationMatcher);
        } else {
            this.newLibrary.matchers.push(this.newValidationMatcher);
        }

        this.newValidationMatcher = new ValidationMatcher();
    }

    initLibrary() {
        this.newLibrary = new ValidationMatcherLibrary();
    }

    createLibrary() {
        this._configService.createValidationMatcherLibrary(this.newLibrary)
            .subscribe(
                response => {
                    this.notifySuccess("Created new validation matcher library '" + this.newLibrary.id + "'");
                    this.libraries.push(this.newLibrary); this.newLibrary = undefined;
                },
                error => this.notifyError(<any>error));
    }

    saveLibrary() {
        this._configService.updateValidationMatcherLibrary(this.selectedLibrary)
            .subscribe(
                response => {
                    this.notifySuccess("Successfully saved validation matcher library '" + this.selectedLibrary.id + "'");
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