import {Injectable} from 'angular2/core';
import {Http, Response, Headers, RequestOptions} from 'angular2/http';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class ConfigService {

    constructor (private http: Http) {}

    private _serviceUrl = 'beans';

    getBeans(type: string) {
        return this.http.get(this._serviceUrl + '/' + type)
                        .map(res => res.json())
                        .catch(this.handleError);
    }

    createBean(bean: any) {
        return this.http.post(this._serviceUrl, JSON.stringify(bean), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
                .catch(this.handleError);
    }

    updateBean(bean: any) {
        return this.http.put(this._serviceUrl + '/' + bean.id, JSON.stringify(bean), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
                .catch(this.handleError);
    }

    deleteBean(id: string) {
        return this.http.delete(this._serviceUrl + '/' + id)
                .catch(this.handleError);
    }

    private handleError (error: Response) {
        return Observable.throw(error.json() || 'Server error');
    }

}