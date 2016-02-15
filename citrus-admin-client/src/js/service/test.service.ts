import {Injectable} from 'angular2/core';
import {Http, Response, Headers, RequestOptions} from 'angular2/http';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class TestService {

    constructor (private http: Http) {}

    private _serviceUrl = 'tests';

    getTestPackages() {
        return this.http.get(this._serviceUrl)
            .map(res => res.json())
            .catch(this.handleError);
    }

    private handleError (error: Response) {
        return Observable.throw(error.json() || 'Server error');
    }

}