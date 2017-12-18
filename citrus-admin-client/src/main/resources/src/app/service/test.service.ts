import {Injectable} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import {TestGroup, Test, TestDetail, TestAction} from '../model/tests';
import {URLSearchParams} from "@angular/http";
import {JsonHeader} from "./common";

@Injectable()
export class TestService {

    constructor (private http: Http) {}

    private _serviceUrl = 'api/tests';
    private _testCountUrl = this._serviceUrl + '/count';
    private _testLatest = this._serviceUrl + '/latest';
    private _testDetailUrl = this._serviceUrl + '/detail';
    private _testSourceUrl = this._serviceUrl + '/source';
    private _testExecuteUrl = this._serviceUrl + '/execute';
    private _testActionsUrl = 'api/test/actions';

    getTestPackages():Observable<TestGroup[]> {
        return this.http.get(this._serviceUrl)
            .map(res => res.json())
            .catch(this.handleError);
    }

    getLatest():Observable<TestGroup[]> {
        return this.http.get(this._testLatest)
            .map(res => res.json())
            .catch(this.handleError);
    }

    getTestCount() {
        return this.http.get(this._testCountUrl)
            .map(res => <number> res.json())
            .catch(this.handleError);
    }

    getTestDetail(test: Test) {
        return this.http.post(this._testDetailUrl, JSON.stringify(test), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
            .map(res => <TestDetail> res.json())
            .catch(this.handleError);
    }

    getSourceCode(path: string) {
        let params = new URLSearchParams();
        params.set('file', path);

        return this.http.get(this._testSourceUrl, { search: params })
            .map(res => <string> res.text())
            .catch(this.handleError);
    }

    updateSourceCode(path: string, sourceCode: string) {
        let params = new URLSearchParams();
        params.set('file', path);

        return this.http.put(this._testSourceUrl, sourceCode, { search: params })
            .catch(this.handleError);
    }

    execute(test: TestDetail) {
        return this.http.post(this._testExecuteUrl, JSON.stringify(test), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
            .map(res => <string> res.text())
            .catch(this.handleError);
    }

    executeGroup(group: TestGroup) {
        return this.http.post(this._testExecuteUrl + '/group', JSON.stringify(group), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
            .map(res => <string> res.text())
            .catch(this.handleError);
    }

    executeAll() {
        return this.http.get(this._testExecuteUrl)
            .map(res => <string> res.text())
            .catch(this.handleError);
    }

    getActionTypes(): Observable<string[]> {
        return this.http.get(`${this._testActionsUrl}/types`)
            .map(res => <string[]> res.json())
            .catch(this.handleError);
    }

    getActionType(type: string): Observable<TestAction> {
        return this.http.get(`${this._testActionsUrl}/type/${type}`)
            .map(res => <TestAction> res.json())
            .catch(this.handleError);
    }

    addAction(position: number, detail: TestDetail) {
        return this.http.post(`${this._testActionsUrl}?pos=${position}`, JSON.stringify(detail), JsonHeader)
            .catch(this.handleError);
    }

    updateAction(position: number, detail: TestDetail) {
        return this.http.put(`${this._testActionsUrl}?pos=${position}`, JSON.stringify(detail), JsonHeader)
            .catch(this.handleError);
    }

    deleteAction(position: number, detail: TestDetail) {
        return this.http.post(`${this._testActionsUrl}/delete?pos=${position}`, JSON.stringify(detail), JsonHeader)
            .catch(this.handleError);
    }

    private handleError (error: Response) {
        return Observable.throw(error.json() || 'Server error');
    }
}
