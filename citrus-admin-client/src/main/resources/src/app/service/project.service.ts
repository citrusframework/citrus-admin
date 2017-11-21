import {Injectable} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Project} from "../model/project";
import {Observable} from 'rxjs/Observable';
import {Module} from "../model/module";

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

    closeActiveProject() {
      this.cachedProject = null;
      this.cachedObservable = null;

      return this.http.delete(this._serviceUrl)
          .catch(this.handleError);
    }

    update(project: Project) {
        if(project === this.cachedProject) {
            this.cachedProject = null;
            this.cachedObservable = null;
        }
        return this.http.put(this._serviceUrl, JSON.stringify(project), new RequestOptions({headers: new Headers({'Content-Type': 'application/json'})}))
            .catch(this.handleError);
    }

    getModules(): Observable<Module[]> {
        return this.http.get(this._serviceUrl + "/modules")
                .map(res => <Module[]> res.json())
                .catch(this.handleError);
    }

    updateModule(module: Module) {
        return this.http.put(this._serviceUrl + "/module", JSON.stringify(module), new RequestOptions({headers: new Headers({'Content-Type': 'application/json'})}))
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
