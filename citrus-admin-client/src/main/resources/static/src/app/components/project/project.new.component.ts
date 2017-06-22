import {Component} from '@angular/core';
import {ProjectSetupService} from "../../service/project.setup.service";
import {Router} from "@angular/router";
import {Archetype} from "../../model/archetype";

@Component({
    selector: 'project-new',
    templateUrl: 'project-new.html'
})
export class NewProjectComponent {

    constructor(private _router: Router,
        private _projectSetupService: ProjectSetupService) {
    }

    repositoryUrl: string;
    archetype: Archetype = new Archetype();
    success: string;
    error: any;
    loading: boolean = false;

    cancel() {
        this._router.navigate(["/"]);
    }

    clearError() {
        this.error = undefined;
    }

    clearSuccess() {
        this.success = undefined;
    }

    onSubmit() {
        this.loading = true;
        if (this.repositoryUrl) {
            this._projectSetupService.loadProject(this.repositoryUrl)
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