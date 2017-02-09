import {Injectable} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Project} from "../model/project";
import {Observable} from 'rxjs/Observable';

@Injectable()
export class ProjectService {

    constructor(private http: Http) {
    }

    private _serviceUrl = 'api/project';

    cachedProject: Project;
    cachedObservable: Observable<Project>;

    getActiveProject(): Observable<Project> {
        if (this.cachedProject) {
            return Observable.of(this.cachedProject)
        } else if (this.cachedObservable) {
            return this.cachedObservable;
        } else {
            this.cachedObservable = this.http.get(this._serviceUrl)
                .map(res => <Project> res.json())
                .do(p => this.cachedProject = p)
                .catch(this.handleError)
                .share();
            return this.cachedObservable;
        }
    }

    update(project: Project) {
        return this.http.put(this._serviceUrl, JSON.stringify(project), new RequestOptions({headers: new Headers({'Content-Type': 'application/json'})}))
            .catch(this.handleError);
    }

    addConnector() {
        return this.http.get(this._serviceUrl + "/connector/add")
            .catch(this.handleError);
    }

    removeConnector() {
        return this.http.get(this._serviceUrl + "/connector/remove")
            .catch(this.handleError);
    }

    private handleError(error: Response) {
        return Observable.throw(error || 'Server error');
    }

}