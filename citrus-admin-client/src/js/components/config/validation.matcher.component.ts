import {Component, OnInit} from 'angular2/core';
import {ConfigService} from '../../service/config.service';
import {ValidationMatcherLibrary, ValidationMatcher} from "../../model/validation.matcher.library";

@Component({
    selector: 'validation-matcher',
    templateUrl: 'templates/config/validation-matcher.html',
    providers: [ConfigService]
})
export class ValidationMatcherComponent implements OnInit {

    constructor(private _configService: ConfigService) {
        this.libraries = [];
        this.newValidationMatcher = new ValidationMatcher();
    }

    errorMessage: string;
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
                error => this.errorMessage = <any>error);
    }

    selectLibrary(endpoint: ValidationMatcherLibrary) {
        this.selectedLibrary = endpoint;
    }

    removeLibrary(library: ValidationMatcherLibrary, event) {
        this._configService.deleteComponent(library.id)
            .subscribe(
                response => this.libraries.splice(this.libraries.indexOf(library), 1),
                error => this.errorMessage = <any>error);

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
                response => { this.libraries.push(this.newLibrary); this.newLibrary = undefined; },
                error => this.errorMessage = <any>error);
    }

    saveLibrary() {
        this._configService.updateValidationMatcherLibrary(this.selectedLibrary)
            .subscribe(
                response => { this.selectedLibrary = undefined },
                error => this.errorMessage = <any>error);
    }

    cancel() {
        if (this.selectedLibrary) {
            this.selectedLibrary = undefined;
            this.getLibraries();
        } else {
            this.newLibrary = undefined;
        }
    }
}