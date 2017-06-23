import {Component, OnInit} from '@angular/core';
import {ConfigService} from '../../../service/config.service';
import {FunctionLibrary, Function} from "../../../model/function.library";
import {Alert} from "../../../model/alert";
import {AlertService} from "../../../service/alert.service";
import {EditorMode} from "../editor-mode.enum";
import {FormGroup, FormBuilder, Validators} from "@angular/forms";

@Component({
    selector: 'function-library',
    templateUrl: 'function-library.html',
    providers: [ConfigService]
})
export class FunctionLibraryComponent implements OnInit {

    constructor(private _configService: ConfigService,
                private fb: FormBuilder,
                private _alertService: AlertService) {
        this.libraries = [];
        this.newFunction = new Function();
    }

    mode: EditorMode;
    form: FormGroup;
    newFunction: Function;
    selectedLibrary: FunctionLibrary;
    libraries: FunctionLibrary[];
    EditorMode = EditorMode;

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
        this._configService.getFunctionLibraries()
            .subscribe(
                libraries => this.libraries = libraries,
                error => this.notifyError(<any>error));
    }

    selectLibrary(endpoint: FunctionLibrary) {
        this.selectedLibrary = endpoint;
        this.initForm(EditorMode.EDIT)
    }

    removeLibrary(library: FunctionLibrary, event: MouseEvent) {
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
        this.selectedLibrary.functions.splice(this.selectedLibrary.functions.indexOf(selected), 1);
    }

    addFunction() {
        this.selectedLibrary.functions.push(this.newFunction);
        this.newFunction = new Function();
    }

    initLibrary() {
        this.selectedLibrary = new FunctionLibrary();
        this.initForm(EditorMode.NEW);
    }

    createLibrary() {
        this._configService.createFunctionLibrary(this.selectedLibrary)
            .subscribe(
                response => {
                    this.notifySuccess("Created new function library '" + this.selectedLibrary.id + "'");
                    this.libraries.push(this.selectedLibrary);
                    this.reset();
                },
                error => this.notifyError(<any>error));
    }

    saveLibrary() {
        this._configService.updateFunctionLibrary(this.selectedLibrary)
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
        this._alertService.add(new Alert("danger", JSON.stringify(error), false));
    }
}