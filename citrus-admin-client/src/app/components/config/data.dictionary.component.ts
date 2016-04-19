import {Component, OnInit} from 'angular2/core';
import {ConfigService} from '../../service/config.service';
import {DataDictionary, Mapping} from "../../model/data.dictionary";

@Component({
    selector: 'data-dictionary',
    templateUrl: 'app/components/config/data-dictionary.html',
    providers: [ConfigService]
})
export class DataDictionaryComponent implements OnInit {

    constructor(private _configService: ConfigService) {
        this.dictionaries = [];
        this.newMapping = new Mapping();
        this.newDictionaryType = "xpath";
    }

    errorMessage: string;
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
                error => this.errorMessage = <any>error);
    }

    selectDictionary(endpoint: DataDictionary) {
        this.selectedDictionary = endpoint;
    }

    removeDictionary(library: DataDictionary, event) {
        this._configService.deleteComponent(library.id)
            .subscribe(
                response => this.dictionaries.splice(this.dictionaries.indexOf(library), 1),
                error => this.errorMessage = <any>error);

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
                response => { this.dictionaries.push(this.newDictionary); this.newDictionary = undefined; },
                error => this.errorMessage = <any>error);
    }

    saveDictionary() {
        this._configService.updateDataDictionary(this.selectedDictionary)
            .subscribe(
                response => { this.selectedDictionary = undefined },
                error => this.errorMessage = <any>error);
    }

    cancel() {
        if (this.selectedDictionary) {
            this.selectedDictionary = undefined;
            this.getDictionaries();
        } else {
            this.newDictionary = undefined;
        }
    }
}