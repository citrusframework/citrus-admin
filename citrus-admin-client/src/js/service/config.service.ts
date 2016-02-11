import {Injectable} from 'angular2/core';
import {Http, Response, Headers, RequestOptions} from 'angular2/http';
import {Observable} from 'rxjs/Observable';
import {GlobalVariables} from '../model/global.variables';
import {NamespaceContext} from '../model/namespace.context';

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

    createComponent(component: any) {
        return this.http.post(this._serviceUrl, JSON.stringify(component), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
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