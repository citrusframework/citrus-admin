import {Injectable} from 'angular2/core';
import {Http, Response} from 'angular2/http';
import {TestReport} from "../model/test.report";
import {Observable} from 'rxjs/Observable';

@Injectable()
export class ReportService {

    constructor (private http: Http) {}

    private _reportUrl = 'report/latest';

    getLatest() {
        return this.http.get(this._reportUrl)
                        .map(res => <TestReport> res.json())
                        .catch(this.handleError);
    }

    private handleError (error: Response) {
        return Observable.throw(error.json() || 'Server error');
    }

}