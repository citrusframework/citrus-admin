import {Injectable} from '@angular/core';
import {Http, Response} from '@angular/http';
import {TestReport} from "../model/test.report";
import {Observable} from 'rxjs/Observable';

@Injectable()
export class ReportService {

    constructor (private http: Http) {}

    private _serviceUrl = 'api/report';

    getLatest() {
        return this.http.get(this._serviceUrl + '/latest')
                        .map(res => <TestReport> res.json())
                        .catch(this.handleError);
    }

    private handleError (error: Response) {
        return Observable.throw(error.json() || 'Server error');
    }

}