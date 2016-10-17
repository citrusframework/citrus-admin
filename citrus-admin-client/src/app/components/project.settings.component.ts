import {Component, OnInit} from 'angular2/core';
import {NgSwitch, NgFor} from 'angular2/common';
import {HTTP_PROVIDERS} from 'angular2/http';
import {Project} from "../model/project";
import {ProjectService} from "../service/project.service";
import {Dialog} from "./util/dialog";
import {Pills, Pill} from "./util/pills";
import {BuildProperty} from "../model/build.property";

declare var jQuery:any;

@Component({
    templateUrl: 'app/components/project-settings.html',
    providers: [ProjectService, HTTP_PROVIDERS],
    directives: [NgSwitch, NgFor, Dialog, Pills, Pill]
})
export class ProjectSettingsComponent implements OnInit {

    constructor(private _projectService: ProjectService) {}

    errorMessage: string;
    project: Project = new Project();

    useCustomCommand: boolean = false;
    dialogOpen: boolean = false;

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
                error => this.errorMessage = <any>error);
    }

    addProperty() {
        var property = new BuildProperty();
        property.name = this.propertyName;
        property.value = this.propertyValue;
        this.project.settings.build.properties.push(property);

        this.propertyName = "";
        this.propertyValue = "";
    }

    removeProperty(property: BuildProperty, event) {
        this.project.settings.build.properties.splice(this.project.settings.build.properties.indexOf(property), 1);
        event.stopPropagation();
        return false;
    }

    saveSettings() {
        if (!this.useCustomCommand) {
            this.project.settings.build.command = "";
        }

        this._projectService.update(this.project)
            .subscribe(error => this.errorMessage = <any>error);
    }

    openDialog() {
        jQuery('#dialog-connector').modal();
    }

    manageConnector() {
        if (this.project.settings.connectorActive) {
            this._projectService.removeConnector()
                .subscribe(
                    response => {
                        this.project.settings.useConnector = false;
                        this.project.settings.connectorActive = false;
                    },
                    error => this.errorMessage = <any>error);
        } else {
            this._projectService.addConnector()
                .subscribe(response => {
                        this.project.settings.useConnector = true;
                        this.project.settings.connectorActive = true;
                    },
                    error => this.errorMessage = <any>error);
        }

        this.getProject();
    }
}