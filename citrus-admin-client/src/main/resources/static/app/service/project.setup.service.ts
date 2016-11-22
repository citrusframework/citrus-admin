import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {ProjectSettings} from "../model/project";

@Injectable()
export class ProjectSetupService {

    constructor (private http: Http) {}

    private _serviceUrl = 'project';

    getProjectHome() {
        return this.http.get(this._serviceUrl + "/home");
    }

    openProject(projectHome: string) {
        return this.http.post(this._serviceUrl, projectHome);
    }

    getDefaultProjectSettings() {
        return this.http.get(this._serviceUrl + "/settings/default");
    }

    saveDefaultProjectSettings(settings: ProjectSettings) {
        return this.http.post(this._serviceUrl + "/settings/default", settings);
    }

}