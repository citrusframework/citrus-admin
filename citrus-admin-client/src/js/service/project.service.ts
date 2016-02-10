import {Injectable} from 'angular2/core';
import {Http, Response} from 'angular2/http';
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

    private handleError (error: Response) {
        return Observable.throw(error.json() || 'Server error');
    }

}