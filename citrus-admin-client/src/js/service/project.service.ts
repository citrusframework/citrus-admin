import {Injectable} from 'angular2/core';
import {Http, Response} from 'angular2/http';
import {Project} from "../model/project";
import {Observable} from 'rxjs/Observable';

@Injectable()
export class ProjectService {

    constructor (private http: Http) {}

    private _projectUrl = 'project';

    getActiveProject() {
        return this.http.get(this._projectUrl)
                        .map(res => <Project> res.json())
                        .catch(this.handleError);
    }

    private handleError (error: Response) {
        return Observable.throw(error.json() || 'Server error');
    }

}