import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {ProjectSettings} from "../model/project";
import {Archetype} from "../model/archetype";
import {Repository} from "../model/repository";

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

    getRepository(type: string) {
        return this.http.get(this._serviceUrl + "/repository/" + type);
    }

    loadProject(repository: Repository) {
        return this.http.post(this._serviceUrl + "/repository", repository);
    }

    createProject(archetype: Archetype) {
        return this.http.post(this._serviceUrl + "/archetype", archetype);
    }

    saveDefaultProjectSettings(settings: ProjectSettings) {
        return this.http.post(this._serviceUrl + "/settings/default", settings);
    }

    getRecentProjects() {
        return this.http.get(this._serviceUrl + "/recent");
    }
}
