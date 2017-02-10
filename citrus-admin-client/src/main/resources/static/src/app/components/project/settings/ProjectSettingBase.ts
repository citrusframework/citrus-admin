import {Project} from "../../../model/project";
import {OnInit} from "@angular/core";
import {BuildProperty} from "../../../model/build.property";
import {ProjectService} from "../../../service/project.service";
import {AlertService} from "../../../service/alert.service";
import * as jQueryVar from 'jquery';
import {Alert} from "../../../model/alert";

declare var jQuery: typeof jQueryVar;



export abstract class ProjectSettingBase implements OnInit{
    active = 'project';
    project:Project = new Project();

    useCustomCommand: boolean = false;
    dialogOpen: boolean = false;

    propertyName: string;
    propertyValue: string;

    constructor(
        private _projectService:ProjectService,
        private _alertService:AlertService
    ) {}

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

    openDialog() {
        (jQuery('#dialog-connector') as any).modal();
    }

    manageConnector() {
        if (this.project.settings.connectorActive) {
            this._projectService.removeConnector()
                .subscribe(
                    () => {
                        this.project.settings.useConnector = false;
                        this.project.settings.connectorActive = false;
                    },
                    error => this.notifyError(<any>error));
        } else {
            this._projectService.addConnector()
                .subscribe(() => {
                        this.project.settings.useConnector = true;
                        this.project.settings.connectorActive = true;
                    },
                    error => this.notifyError(<any>error));
        }

        this.getProject();
    }

    notifySuccess(message: string) {
        this._alertService.add(new Alert("success", message, true));
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error.message, false));
    }
}