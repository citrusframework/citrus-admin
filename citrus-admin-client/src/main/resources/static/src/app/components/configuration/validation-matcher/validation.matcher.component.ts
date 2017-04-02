import {Component, OnInit} from '@angular/core';
import {ConfigService} from '../../../service/config.service';
import {ValidationMatcherLibrary, ValidationMatcher} from "../../../model/validation.matcher.library";
import {Alert} from "../../../model/alert";
import {AlertService} from "../../../service/alert.service";
import {EditorMode} from "../editor-mode.enum";
import {Validators, FormGroup, FormBuilder} from "@angular/forms";

@Component({
    selector: 'validation-matcher',
    templateUrl: 'validation-matcher.html',
    providers: [ConfigService]
})
export class ValidationMatcherComponent implements OnInit {

    constructor(private _configService: ConfigService,
                private fb:FormBuilder,
                private _alertService: AlertService) {
        this.libraries = [];
        this.newValidationMatcher = new ValidationMatcher();
    }

    form:FormGroup;
    mode:EditorMode;
    EditorMode = EditorMode;
    newValidationMatcher: ValidationMatcher;
    newLibrary: ValidationMatcherLibrary;
    selectedLibrary: ValidationMatcherLibrary;
    libraries: ValidationMatcherLibrary[];

    removeValidationMatcher(selected: ValidationMatcher) {
        this.selectedLibrary.matchers.splice(this.selectedLibrary.matchers.indexOf(selected), 1);
    }

    addValidationMatcher() {
        this.selectedLibrary.matchers.push(this.newValidationMatcher);

        this.newValidationMatcher = new ValidationMatcher();
    }

    ngOnInit() {
        this.getLibraries();
    }

    initForm(mode: EditorMode) {
        this.mode = mode;
        this.form = this.fb.group({
            id: [this.selectedLibrary.id, Validators.required],
            prefix: [this.selectedLibrary.prefix, Validators.required]
        })
    }

    getLibraries() {
        this._configService.getValidationMatcherLibraries()
            .subscribe(
                libraries => this.libraries = libraries,
                error => this.notifyError(<any>error));
    }

    selectLibrary(endpoint: ValidationMatcherLibrary) {
        this.selectedLibrary = endpoint;
        this.initForm(EditorMode.EDIT)
    }

    removeLibrary(library: ValidationMatcherLibrary, event: MouseEvent) {
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


    initLibrary() {
        this.selectedLibrary = new ValidationMatcherLibrary();
        this.initForm(EditorMode.NEW);
    }

    createLibrary() {
        this._configService.createValidationMatcherLibrary(this.selectedLibrary)
            .subscribe(
                response => {
                    this.notifySuccess("Created new function library '" + this.selectedLibrary.id + "'");
                    this.libraries.push(this.selectedLibrary);
                    this.reset();
                },
                error => this.notifyError(<any>error));
    }

    saveLibrary() {
        this._configService.updateValidationMatcherLibrary(this.selectedLibrary)
            .subscribe(
                response => {
                    this.notifySuccess("Successfully saved function library '" + this.selectedLibrary.id + "'");
                    this.reset();
                },
                error => this.notifyError(<any>error));
    }

    submitForm() {
        if (EditorMode.NEW === this.mode) {
            this.createLibrary()
        }
        if (EditorMode.EDIT === this.mode) {
            this.saveLibrary()
        }
    }

    cancel() {
        this.reset();
        this.getLibraries();

    }

    private reset() {
        this.mode = null;
        this.selectedLibrary = undefined;
        this.form = null;
    }

    notifySuccess(message: string) {
        this._alertService.add(new Alert("success", message, true));
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error, false));
    }
}