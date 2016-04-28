import {Injectable} from 'angular2/core';
import {Http, Response, Headers, RequestOptions} from 'angular2/http';
import {Observable} from 'rxjs/Observable';
import {TestPackage, Test, TestDetail, TestResult} from '../model/tests';

@Injectable()
export class TestService {

    constructor (private http: Http) {}

    private _serviceUrl = 'tests';
    private _testDetailUrl = this._serviceUrl + '/detail';
    private _testSourceUrl = this._serviceUrl + '/source';
    private _testExecuteUrl = this._serviceUrl + '/execute';

    getTestPackages() {
        return this.http.get(this._serviceUrl)
            .map(res => <TestPackage[]> res.json())
            .catch(this.handleError);
    }

    getTestDetail(test: Test) {
        return this.http.post(this._testDetailUrl, JSON.stringify(test), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
            .map(res => <TestDetail> res.json())
            .catch(this.handleError);
    }

    getSourceCode(test: TestDetail, type: string) {
        return this.http.post(this._testSourceUrl + '/' + type, JSON.stringify(test), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
            .map(res => <string> res.text())
            .catch(this.handleError);
    }

    execute(test: TestDetail) {
        return this.http.post(this._testExecuteUrl, JSON.stringify(test), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
            .map(res => <TestResult> res.json())
            .catch(this.handleError);
    }

    private handleError (error: Response) {
        return Observable.throw(error.json() || 'Server error');
    }
}