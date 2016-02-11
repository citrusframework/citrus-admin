import {Injectable} from 'angular2/core';
import {Http, Response, Headers, RequestOptions} from 'angular2/http';
import {Observable} from 'rxjs/Observable';
import {GlobalVariables} from '../model/global.variables';
import {NamespaceContext} from '../model/namespace.context';
import {FunctionLibrary} from '../model/function.library';
import {ValidationMatcherLibrary} from '../model/validation.matcher.library';

@Injectable()
export class ConfigService {

    constructor (private http: Http) {}

    private _serviceUrl = 'config';
    private _globalVariablesUrl = this._serviceUrl + '/global-variables';
    private _schemaRepositoryUrl = this._serviceUrl + '/schema-repository';
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

    getFunctionLibrary(id: string) {
        return this.http.get(this._functionLibraryUrl + '/' + id)
            .map(res => <FunctionLibrary> res.json())
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

    getValidationMatcherLibrary(id: string) {
        return this.http.get(this._validationMatcherUrl + '/' + id)
            .map(res => <ValidationMatcherLibrary> res.json())
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

    deleteComponent(id: string) {
        return this.http.delete(this._serviceUrl + '/' + id)
                .catch(this.handleError);
    }

    private handleError (error: Response) {
        return Observable.throw(error.json() || 'Server error');
    }

}