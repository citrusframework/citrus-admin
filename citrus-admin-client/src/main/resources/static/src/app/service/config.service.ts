import {Injectable} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/Rx';
import {GlobalVariables} from '../model/global.variables';
import {NamespaceContext} from '../model/namespace.context';
import {FunctionLibrary} from '../model/function.library';
import {ValidationMatcherLibrary} from '../model/validation.matcher.library';
import {DataDictionary} from '../model/data.dictionary';
import {SchemaRepository, Schema} from "../model/schema.repository";
import {JsonHeader} from "./common";

@Injectable()
export class ConfigService {

    constructor (private http: Http) {}

    private serviceUrl = 'api/config';
    private globalVariablesUrl = `${this.serviceUrl}/global-variables`;
    private schemaRepositoryUrl = `${this.serviceUrl}/schema-repository`;
    private schemaUrl = `${this.serviceUrl}/schema`;
    private namespaceContextUrl = `${this.serviceUrl}/namespace-context`;
    private functionLibraryUrl = `${this.serviceUrl}/function-library`;
    private validationMatcherUrl = `${this.serviceUrl}/validation-matcher`;
    private dataDictionaryUrl = `${this.serviceUrl}/data-dictionary`;
    private mappingStrategiesUrl = `${this.serviceUrl}/mapping-strategy`;

    getGlobalVariables() {
        return this.http.get(this.globalVariablesUrl)
                        .map(res => <GlobalVariables> res.json())
                        .catch(this.handleError);
    }

    updateGlobalVariables(component: GlobalVariables) {
        return this.http.put(this.globalVariablesUrl, JSON.stringify(component), JsonHeader)
            .catch(this.handleError);
    }

    getNamespaceContext() {
        return this.http.get(this.namespaceContextUrl)
            .map(res => <NamespaceContext> res.json())
            .catch(this.handleError);
    }

    updateNamespaceContext(component: NamespaceContext) {
        return this.http.put(this.namespaceContextUrl, JSON.stringify(component), JsonHeader)
            .catch(this.handleError);
    }

    getFunctionLibraries() {
        return this.http.get(this.functionLibraryUrl)
            .map(res => <FunctionLibrary[]> res.json())
            .catch(this.handleError);
    }

    updateFunctionLibrary(component: FunctionLibrary) {
        return this.http.put(this.functionLibraryUrl, JSON.stringify(component), JsonHeader)
            .catch(this.handleError);
    }

    createFunctionLibrary(component: FunctionLibrary) {
        return this.http.post(this.functionLibraryUrl, JSON.stringify(component), JsonHeader)
                .catch(this.handleError);
    }

    getValidationMatcherLibraries():Observable<ValidationMatcherLibrary[]> {
        return this.http.get(this.validationMatcherUrl)
            .map(res => <ValidationMatcherLibrary[]> res.json())
            .catch(this.handleError);
    }

    updateValidationMatcherLibrary(component: ValidationMatcherLibrary) {
        return this.http.put(this.validationMatcherUrl, JSON.stringify(component), JsonHeader)
            .catch(this.handleError);
    }

    createValidationMatcherLibrary(component: ValidationMatcherLibrary) {
        return this.http.post(this.validationMatcherUrl, JSON.stringify(component), JsonHeader)
            .catch(this.handleError);
    }

    getSchemaRepositories():Observable<SchemaRepository[]> {
        return this.http.get(this.schemaRepositoryUrl)
            .map(res => <SchemaRepository[]> res.json())
            .catch(this.handleError);
    }

    updateSchemaRepository(component: SchemaRepository) {
        return this.http.put(this.schemaRepositoryUrl, JSON.stringify(component), JsonHeader)
            .catch(this.handleError);
    }

    createSchemaRepository(component: SchemaRepository) {
        return this.http.post(this.schemaRepositoryUrl, JSON.stringify(component), JsonHeader)
            .catch(this.handleError);
    }

    getSchemas():Observable<Schema[]> {
        return this.http.get(this.schemaUrl)
            .map(res => res.json())
            .catch(this.handleError);
    }

    updateSchema(component: Schema) {
        return this.http.put(this.schemaUrl, JSON.stringify(component), JsonHeader)
            .catch(this.handleError);
    }

    createSchema(component: Schema) {
        return this.http.post(this.schemaUrl, JSON.stringify(component), JsonHeader)
            .catch(this.handleError);
    }

    getDataDictionaries() {
        return this.http.get(this.dataDictionaryUrl)
            .map(res => <DataDictionary[]> res.json())
            .catch(this.handleError);
    }

    updateDataDictionary(component: DataDictionary) {
        return this.http.put(this.dataDictionaryUrl, JSON.stringify(component), JsonHeader)
            .catch(this.handleError);
    }

    createDataDictionary(type: string, component: DataDictionary) {
        return this.http.post(`${this.dataDictionaryUrl}/${type}`, JSON.stringify(component), JsonHeader)
            .catch(this.handleError);
    }

    deleteComponent(id: string) {
        return this.http.delete(`${this.serviceUrl}/${id}`)
                .catch(this.handleError);
    }

    getMappingStrategies() {
        return this.http.get(this.mappingStrategiesUrl, JsonHeader)
            .map(r => <string[]>r.json())
    }

    private handleError (error: Response) {
        return Observable.throw(error.json() || 'Server error');
    }
}