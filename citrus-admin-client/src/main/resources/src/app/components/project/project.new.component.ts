import {Component} from '@angular/core';
import {ProjectSetupService} from "../../service/project.setup.service";
import {Archetype} from "../../model/archetype";
import {Repository} from "../../model/repository";

@Component({
    selector: 'project-new',
    templateUrl: 'project-new.html'
})
export class NewProjectComponent {

    constructor(private _projectSetupService: ProjectSetupService) {
    }

    repository: Repository;
    archetype: Archetype = new Archetype();
    success: string;
    error: any;
    loading: boolean = false;

    credentials: boolean = false;

    maven: boolean;
    vcs: boolean;

    clearError() {
        this.error = undefined;
    }

    clearSuccess() {
        this.success = undefined;
    }

    useVcs(type: string) {
        this._projectSetupService.getRepository(type)
            .subscribe(response => {
                    this.repository = response.json();
                    this.maven = false;
                    this.vcs = true;
                },
                error => this.error = error.json());
    }

    useMaven() {
        this.maven = true;
        this.vcs = false;
        this.repository = undefined;
    }

    onSubmit() {
        this.clearError();
        this.loading = true;
        if (this.vcs && this.repository.url) {
            this._projectSetupService.loadProject(this.repository)
                .subscribe(
                    success => window.location.href = "/",
                    error => {
                        this.error = error.json();
                        this.loading = false;
                    });
        } else if (this.maven) {
            this._projectSetupService.createProject(this.archetype)
                .subscribe(
                    success => window.location.href = "/",
                    error => {
                        this.error = error.json();
                        this.loading = false;
                    });
        } else {
            this.loading = false;
        }
    }
}
