import {Component, OnInit} from '@angular/core';
import {ConfigService} from '../../../service/config.service';
import {DataDictionary, Mapping} from "../../../model/data.dictionary";
import {Alert} from "../../../model/alert";
import {AlertService} from "../../../service/alert.service";
import {EditorMode} from "../editor-mode.enum";
import {FormGroup, FormBuilder, Validators} from "@angular/forms";

@Component({
    selector: 'data-dictionary',
    templateUrl: 'data-dictionary.html',
    providers: [ConfigService]
})
export class DataDictionaryComponent implements OnInit {

    constructor(private _configService: ConfigService,
                private fb:FormBuilder,
                private _alertService: AlertService) {
        this.dictionaries = [];
        this.newMapping = new Mapping();
        this.dictionaryType = "xpath";
    }

    EditorMode = EditorMode;
    mode: EditorMode;
    form:FormGroup;
    newMapping: Mapping;
    dictionaryType = 'xpath';
    selectedDictionary: DataDictionary;
    dictionaries: DataDictionary[];

    ngOnInit() {
        this.getDictionaries();
    }

    initForm(mode:EditorMode) {
        this.mode = mode;
        this.form = this.fb.group({
            id: [this.selectedDictionary.id, Validators.required],
            globalScope: [this.selectedDictionary.globalScope, Validators.required],
            type: [{value:this.dictionaryType, disabled: EditorMode.EDIT === mode}, Validators.required],
            mappingStrategy: [this.selectedDictionary.mappingStrategy, Validators.required],
        })
    }

    getDictionaries() {
        this._configService.getDataDictionaries()
            .subscribe(
                dictionaries => this.dictionaries = dictionaries,
                error => this.notifyError(<any>error));
    }

    selectDictionary(endpoint: DataDictionary) {
        this.selectedDictionary = endpoint;
        this.initForm(EditorMode.EDIT)
    }

    removeDictionary(library: DataDictionary, event:MouseEvent) {
        this._configService.deleteComponent(library.id)
            .subscribe(
                response => {
                    this.dictionaries.splice(this.dictionaries.indexOf(library), 1);
                    this.notifySuccess("Removed data dictionary '" + library.id + "'");
                },
                error => this.notifyError(<any>error));

        event.stopPropagation();
        return false;
    }

    removeMapping(selected: Mapping) {
        this.selectedDictionary.mappings.mappings.splice(this.selectedDictionary.mappings.mappings.indexOf(selected), 1);
    }

    addMapping() {
        this.selectedDictionary.mappings.mappings.push(this.newMapping);
        this.newMapping = new Mapping();
    }

    initDictionary() {
        this.selectedDictionary = new DataDictionary();
        this.selectedDictionary.globalScope = true;
        this.selectedDictionary.mappingStrategy = 'EXACT';
        this.initForm(EditorMode.NEW)
    }

    createDictionary() {
        this._configService.createDataDictionary(this.dictionaryType, this.selectedDictionary)
            .subscribe(
                response => {
                    this.notifySuccess("Created new data dictionary '" + this.selectedDictionary.id + "'");
                    this.dictionaries.push(this.selectedDictionary); this.reset();
                },
                error => this.notifyError(<any>error));
    }

    saveDictionary() {
        this._configService.updateDataDictionary(this.selectedDictionary)
            .subscribe(
                response => {
                    this.notifySuccess("Successfully saved data dictionary '" + this.selectedDictionary.id + "'");
                    this.reset();
                },
                error => this.notifyError(<any>error));
    }

    submitForm() {
        if(EditorMode.EDIT === this.mode) {
            this.saveDictionary();
        } else if(EditorMode.NEW === this.mode) {
            this.createDictionary();
        }
    }

    cancel() {
        this.reset();
        this.getDictionaries();
    }

    notifySuccess(message: string) {
        this._alertService.add(new Alert("success", message, true));
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", JSON.stringify(error), false));
    }

    private reset() {
        this.mode = null;
        this.form = null;
        this.selectedDictionary = null;
        this.dictionaryType = 'xpath';
    }
}