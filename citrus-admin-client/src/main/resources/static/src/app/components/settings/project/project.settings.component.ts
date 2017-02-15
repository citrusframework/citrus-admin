import {Component, OnInit} from '@angular/core';
import {Project} from "../../../model/project";
import {ProjectService} from "../../../service/project.service";
import {Alert} from "../../../model/alert";
import {AlertService} from "../../../service/alert.service";
import {BuildProperty} from "../../../model/build.property";


@Component({
    templateUrl: 'project-settings.html'
})
export class ProjectSettingsComponent implements OnInit {

    constructor(private _projectService: ProjectService,
                private _alertService: AlertService) {
    }

    project: Project = new Project();

    useCustomCommand: boolean = false;

    propertyName: string;
    propertyValue: string;

    ngOnInit() {
        this.getProject();
    }

    getProject() {
        this._projectService.getActiveProject()
            .subscribe(
                project => {
                    this.project = project;
                    if (this.project.settings.build.command) {
                        this.useCustomCommand = true;
                    }
                },
                error => this.notifyError(<any>error));
    }

    removeProperty(property: BuildProperty, event:MouseEvent) {
        this.project.settings.build.properties.splice(this.project.settings.build.properties.indexOf(property), 1);
        event.stopPropagation();
        return false;
    }

    saveSettings() {
        if (!this.useCustomCommand) {
            this.project.settings.build.command = "";
        }

        this._projectService.update(this.project)
            .subscribe(response => this.notifySuccess("Settings successfully saved"),
                error => this.notifyError(<any>error));
    }

    notifySuccess(message: string) {
        this._alertService.add(new Alert("success", message, true));
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error.message, false));
    }

}