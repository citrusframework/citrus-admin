import {Component} from '@angular/core';
import {ProjectSetupService} from "../../service/project.setup.service";
import {Router} from "@angular/router";
import {Archetype} from "../../model/archetype";
import {Repository} from "../../model/repository";

@Component({
    selector: 'project-new',
    templateUrl: 'project-new.html'
})
export class NewProjectComponent {

    constructor(private _router: Router,
        private _projectSetupService: ProjectSetupService) {
    }

    repository: Repository = new Repository();
    archetype: Archetype = new Archetype();
    success: string;
    error: any;
    loading: boolean = false;

    maven: boolean;
    git: boolean;

    clearError() {
        this.error = undefined;
    }

    clearSuccess() {
        this.success = undefined;
    }

    onSubmit() {
        this.loading = true;
        if (this.repository.url) {
            this._projectSetupService.loadProject(this.repository)
                .subscribe(
                    success => window.location.href = "/",
                    error => this.error = error.json());
        } else {
            this._projectSetupService.createProject(this.archetype)
                .subscribe(
                    success => window.location.href = "/",
                    error => this.error = error.json());
        }
    }
}
