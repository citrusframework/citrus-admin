import {Component, OnInit} from 'angular2/core';
import {NgSwitch, NgFor} from 'angular2/common';
import {HTTP_PROVIDERS} from 'angular2/http';
import {Project} from "../model/project";
import {ProjectService} from "../service/project.service";
import {ReportService} from "../service/report.service";
import {TestReport} from "../model/test.report";

@Component({
    templateUrl: 'app/components/project.html',
    providers: [ProjectService, HTTP_PROVIDERS],
    directives: [NgSwitch, NgFor]
})
export class ProjectComponent implements OnInit {

    constructor(private _projectService: ProjectService) {}

    errorMessage: string;
    project = new Project();

    ngOnInit() {
        this.getProject();
    }

    getProject() {
        this._projectService.getActiveProject()
            .subscribe(
                project => this.project = project,
                error => this.errorMessage = <any>error);
    }

    saveSettings() {
        this._projectService.saveSettings(this.project)
            .subscribe(
                project => this.project = project,
                error => this.errorMessage = <any>error);
    }
}