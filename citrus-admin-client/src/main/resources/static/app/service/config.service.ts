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

@Injectable()
export class ConfigService {

    constructor (private http: Http) {}

    private _serviceUrl = 'config';
    private _globalVariablesUrl = this._serviceUrl + '/global-variables';
    private _schemaRepositoryUrl = this._serviceUrl + '/schema-repository';
    private _schemaUrl = this._serviceUrl + '/schema';
    private _namespaceContextUrl = this._serviceUrl + '/namespace-context';
    private _functionLibraryUrl = this._serviceUrl + '/function-library';
    private _validationMatcherUrl = this._serviceUrl + '/validation-matcher';
    private _dataDictionaryUrl = this._serviceUrl + '/data-dictionary';

    getGlobalVariables() {
        return this.http.get(this._globalVariablesUrl)
                        .map(res => <GlobalVariables> res.json())
                        .catch(this.handleError);
    }

    updateGlobalVariables(component: GlobalVariables) {
        return this.http.put(this._globalVariablesUrl, JSON.stringify(component), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
            .catch(this.handleError);
    }

    getNamespaceContext() {
        return this.http.get(this._namespaceContextUrl)
            .map(res => <NamespaceContext> res.json())
            .catch(this.handleError);
    }

    updateNamespaceContext(component: NamespaceContext) {
        return this.http.put(this._namespaceContextUrl, JSON.stringify(component), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
            .catch(this.handleError);
    }

    getFunctionLibraries() {
        return this.http.get(this._functionLibraryUrl)
            .map(res => <FunctionLibrary[]> res.json())
            .catch(this.handleError);
    }

    updateFunctionLibrary(component: FunctionLibrary) {
        return this.http.put(this._functionLibraryUrl, JSON.stringify(component), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
            .catch(this.handleError);
    }

    createFunctionLibrary(component: FunctionLibrary) {
        return this.http.post(this._functionLibraryUrl, JSON.stringify(component), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
                .catch(this.handleError);
    }

    getValidationMatcherLibraries() {
        return this.http.get(this._validationMatcherUrl)
            .map(res => <ValidationMatcherLibrary[]> res.json())
            .catch(this.handleError);
    }

    updateValidationMatcherLibrary(component: ValidationMatcherLibrary) {
        return this.http.put(this._validationMatcherUrl, JSON.stringify(component), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
            .catch(this.handleError);
    }

    createValidationMatcherLibrary(component: ValidationMatcherLibrary) {
        return this.http.post(this._validationMatcherUrl, JSON.stringify(component), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
            .catch(this.handleError);
    }

    getSchemaRepositories() {
        return this.http.get(this._schemaRepositoryUrl)
            .map(res => <SchemaRepository[]> res.json())
            .catch(this.handleError);
    }

    updateSchemaRepository(component: SchemaRepository) {
        return this.http.put(this._schemaRepositoryUrl, JSON.stringify(component), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
            .catch(this.handleError);
    }

    createSchemaRepository(component: SchemaRepository) {
        return this.http.post(this._schemaRepositoryUrl, JSON.stringify(component), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
            .catch(this.handleError);
    }

    getSchemas() {
        return this.http.get(this._schemaUrl)
            .map(res => <Schema[]> res.json())
            .catch(this.handleError);
    }

    updateSchema(component: Schema) {
        return this.http.put(this._schemaUrl, JSON.stringify(component), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
            .catch(this.handleError);
    }

    createSchema(component: Schema) {
        return this.http.post(this._schemaUrl, JSON.stringify(component), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
            .catch(this.handleError);
    }

    getDataDictionaries() {
        return this.http.get(this._dataDictionaryUrl)
            .map(res => <DataDictionary[]> res.json())
            .catch(this.handleError);
    }

    updateDataDictionary(component: DataDictionary) {
        return this.http.put(this._dataDictionaryUrl, JSON.stringify(component), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
            .catch(this.handleError);
    }

    createDataDictionary(type: string, component: DataDictionary) {
        return this.http.post(this._dataDictionaryUrl + '/' + type, JSON.stringify(component), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
            .catch(this.handleError);
    }

    deleteComponent(id: string) {
        return this.http.delete(this._serviceUrl + '/' + id)
                .catch(this.handleError);
    }

    private handleError (error: Response) {
        return Observable.throw(error.json() || 'Server error');
    }

}