import {Injectable} from 'angular2/core';
import {Http, Response, Headers, RequestOptions} from 'angular2/http';
import {Observable} from 'rxjs/Observable';
import {TestPackage, Test, TestDetail} from '../model/tests';

@Injectable()
export class TestService {

    constructor (private http: Http) {}

    private _serviceUrl = 'tests';
    private _testDetailUrl = this._serviceUrl + '/detail';

    getTestPackages() {
        return this.http.get(this._serviceUrl)
            .map(res => <TestPackage[]> res.json())
            .catch(this.handleError);
    }

    getTestDetail(test: Test) {
        return this.http.get(this._testDetailUrl + '/' + test.type + '/' + test.packageName + '/' + test.name)
            .map(res => <TestDetail> res.json())
            .catch(this.handleError);
    }

    private handleError (error: Response) {
        return Observable.throw(error.json() || 'Server error');
    }
}