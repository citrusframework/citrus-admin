import {Injectable} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {TestReport} from "../model/test.report";
import {Observable} from 'rxjs/Observable';
import {Test} from "../model/tests";

@Injectable()
export class ReportService {

    constructor (private http: Http) {}

    private _serviceUrl = 'api/report';

    getLatest() {
        return this.http.get(this._serviceUrl + '/latest')
                        .map(res => <TestReport> res.json())
                        .catch(this.handleError);
    }

    getTestResult(test: Test) {
        return this.http.post(this._serviceUrl + '/result', JSON.stringify(test), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
                        .map(res => <TestReport> res.json())
                        .catch(this.handleError);
    }

    private handleError (error: Response) {
        return Observable.throw(error.json() || 'Server error');
    }

}
