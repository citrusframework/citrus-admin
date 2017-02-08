import {Injectable} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class EndpointService {

    constructor (private http: Http) {}

    private _serviceUrl = 'api/endpoints';

    getEndpoints() {
        return this.http.get(this._serviceUrl)
                        .map(res => res.json())
                        .catch(this.handleError);
    }

    createEndpoint(bean: any) {
        return this.http.post(this._serviceUrl, JSON.stringify(bean), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
                .catch(this.handleError);
    }

    updateEndpoint(bean: any) {
        return this.http.put(this._serviceUrl + '/' + bean.id, JSON.stringify(bean), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
                .catch(this.handleError);
    }

    deleteEndpoint(id: string) {
        return this.http.delete(this._serviceUrl + '/' + id)
                .catch(this.handleError);
    }

    getEndpointTypes() {
        return this.http.get(this._serviceUrl + '/types')
            .map(res => res.json())
            .catch(this.handleError);
    }

    getEndpointType(type: string) {
        return this.http.get(this._serviceUrl + '/type/' + type)
                .map(res => res.json())
                .catch(this.handleError);
    }

    private handleError (error: Response) {
        return Observable.throw(error.json() || 'Server error');
    }

}