import {Injectable} from 'angular2/core';
import {Http, Response, Headers, RequestOptions} from 'angular2/http';
import {Observable} from 'rxjs/Observable';
import {TestPackage, Test, TestDetail} from '../model/tests';

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
        return this.http.get(this._testDetailUrl + '/' + test.type + '/' + test.packageName + '/' + test.name)
            .map(res => <TestDetail> res.json())
            .catch(this.handleError);
    }

    getSourceCode(test: TestDetail, type: string) {
        return this.http.get(this._testSourceUrl + '/' + type + '/' + test.packageName + '/' + test.name)
            .map(res => <string> res.text())
            .catch(this.handleError);
    }

    execute(test: TestDetail) {
        return this.http.get(this._testExecuteUrl + '/' + test.packageName + '/' + test.name)
            .map(res => <string> res.text())
            .catch(this.handleError);
    }

    private handleError (error: Response) {
        return Observable.throw(error.json() || 'Server error');
    }
}