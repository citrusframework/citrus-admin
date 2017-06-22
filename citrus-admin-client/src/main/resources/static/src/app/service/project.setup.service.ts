import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {ProjectSettings} from "../model/project";
import {Archetype} from "../model/archetype";

@Injectable()
export class ProjectSetupService {

    constructor (private http: Http) {}

    private _serviceUrl = 'api/project';

    getProjectHome() {
        return this.http.get(this._serviceUrl + "/home");
    }

    openProject(projectHome: string) {
        return this.http.post(this._serviceUrl, projectHome);
    }

    getDefaultProjectSettings() {
        return this.http.get(this._serviceUrl + "/settings/default");
    }

    loadProject(repository: string) {
        return this.http.post(this._serviceUrl + "/load/repository", repository);
    }
    
    createProject(archetype: Archetype) {
        return this.http.post(this._serviceUrl + "/create/archetype", archetype);
    }
    
    saveDefaultProjectSettings(settings: ProjectSettings) {
        return this.http.post(this._serviceUrl + "/settings/default", settings);
    }

    getRecentProjects() {
        return this.http.get(this._serviceUrl + "/recent");
    }
}