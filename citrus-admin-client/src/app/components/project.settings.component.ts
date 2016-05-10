import {Component, OnInit} from 'angular2/core';
import {NgSwitch, NgFor} from 'angular2/common';
import {HTTP_PROVIDERS} from 'angular2/http';
import {Project} from "../model/project";
import {ProjectService} from "../service/project.service";
import {ReportService} from "../service/report.service";
import {TestReport} from "../model/test.report";
import {BuildProperty} from "../model/build.property";

@Component({
    templateUrl: 'app/components/project-settings.html',
    providers: [ProjectService, HTTP_PROVIDERS],
    directives: [NgSwitch, NgFor]
})
export class ProjectSettingsComponent implements OnInit {

    constructor(private _projectService: ProjectService) {}

    errorMessage: string;
    project: Project = new Project();

    propertyName: string;
    propertyValue: string;

    ngOnInit() {
        this.getProject();
    }

    getProject() {
        this._projectService.getActiveProject()
            .subscribe(
                project => this.project = project,
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
        this._projectService.saveSettings(this.project)
            .subscribe(error => this.errorMessage = <any>error);
    }
}