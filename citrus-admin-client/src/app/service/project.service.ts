import {Injectable} from 'angular2/core';
import {Http, Response, Headers, RequestOptions} from 'angular2/http';
import {Project} from "../model/project";
import {Observable} from 'rxjs/Observable';

@Injectable()
export class ProjectService {

    constructor (private http: Http) {}

    private _serviceUrl = 'project';

    getActiveProject() {
        return this.http.get(this._serviceUrl)
                        .map(res => <Project> res.json())
                        .catch(this.handleError);
    }

    saveSettings(project: Project) {
        return this.http.post(this._serviceUrl, JSON.stringify(project), new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) }))
            .catch(this.handleError);
    }

    private handleError (error: Response) {
        return Observable.throw(error.json() || 'Server error');
    }

}