import {Component, OnInit} from '@angular/core';
import {ConfigService} from '../../service/config.service';
import {DataDictionary, Mapping} from "../../model/data.dictionary";
import {Alert} from "../../model/alert";
import {AlertService} from "../../service/alert.service";

@Component({
    selector: 'data-dictionary',
    templateUrl: 'app/components/config/data-dictionary.html',
    providers: [ConfigService]
})
export class DataDictionaryComponent implements OnInit {

    constructor(private _configService: ConfigService,
                private _alertService: AlertService) {
        this.dictionaries = [];
        this.newMapping = new Mapping();
        this.newDictionaryType = "xpath";
    }

    newMapping: Mapping;
    newDictionaryType: string;
    newDictionary: DataDictionary;
    selectedDictionary: DataDictionary;
    dictionaries: DataDictionary[];

    ngOnInit() {
        this.getDictionaries();
    }

    getDictionaries() {
        this._configService.getDataDictionaries()
            .subscribe(
                dictionaries => this.dictionaries = dictionaries,
                error => this.notifyError(<any>error));
    }

    selectDictionary(endpoint: DataDictionary) {
        this.selectedDictionary = endpoint;
    }

    removeDictionary(library: DataDictionary, event) {
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
        if (this.selectedDictionary) {
            this.selectedDictionary.mappings.mappings.splice(this.selectedDictionary.mappings.mappings.indexOf(selected), 1);
        } else {
            this.newDictionary.mappings.mappings.splice(this.newDictionary.mappings.mappings.indexOf(selected), 1);
        }
    }

    addMapping() {
        if (this.selectedDictionary) {
            this.selectedDictionary.mappings.mappings.push(this.newMapping);
        } else {
            this.newDictionary.mappings.mappings.push(this.newMapping);
        }

        this.newMapping = new Mapping();
    }

    initDictionary() {
        this.newDictionary = new DataDictionary();
    }

    createDictionary() {
        this._configService.createDataDictionary(this.newDictionaryType, this.newDictionary)
            .subscribe(
                response => {
                    this.notifySuccess("Created new data dictionary '" + this.newDictionary.id + "'");
                    this.dictionaries.push(this.newDictionary); this.newDictionary = undefined;
                },
                error => this.notifyError(<any>error));
    }

    saveDictionary() {
        this._configService.updateDataDictionary(this.selectedDictionary)
            .subscribe(
                response => {
                    this.notifySuccess("Successfully saved data dictionary '" + this.selectedDictionary.id + "'");
                    this.selectedDictionary = undefined;
                },
                error => this.notifyError(<any>error));
    }

    cancel() {
        if (this.selectedDictionary) {
            this.selectedDictionary = undefined;
            this.getDictionaries();
        } else {
            this.newDictionary = undefined;
        }
    }

    notifySuccess(message: string) {
        this._alertService.add(new Alert("success", message, true));
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error.message, false));
    }
}