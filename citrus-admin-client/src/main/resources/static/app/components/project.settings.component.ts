import {Component, OnInit} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {Project} from "../model/project";
import {ProjectService} from "../service/project.service";
import {Dialog} from "./util/dialog";
import {Pills, Pill} from "./util/pills";
import {BuildProperty} from "../model/build.property";
import {AlertService} from "../service/alert.service";
import {Alert} from "../model/alert";
import {AlertConsole} from "./alert.console";

declare var jQuery:any;
declare var _:any;

@Component({
    templateUrl: 'app/components/project-settings.html'
})
export class ProjectSettingsComponent implements OnInit {

    constructor(private _projectService: ProjectService,
                private _alertService: AlertService,
                private route: ActivatedRoute) {

        let activeTabParam: string = route.snapshot.params['activeTab'];
        if (activeTabParam != null) {
            this.active = activeTabParam;
        }
    }

    active = 'project';
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
                error => this.notifyError(<any>error));
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
            .subscribe(response => this.notifySuccess("Settings successfully saved"),
                error => this.notifyError(<any>error));
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
                    error => this.notifyError(<any>error));
        } else {
            this._projectService.addConnector()
                .subscribe(response => {
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