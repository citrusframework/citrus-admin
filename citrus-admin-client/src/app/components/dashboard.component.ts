import {Component, OnInit} from 'angular2/core';
import {NgSwitch, NgFor} from 'angular2/common';
import {HTTP_PROVIDERS} from 'angular2/http';
import {Project} from "../model/project";
import {ProjectService} from "../service/project.service";
import {ReportService} from "../service/report.service";
import {TestService} from "../service/test.service";
import {TestReport} from "../model/test.report";

@Component({
    templateUrl: 'app/components/dashboard.html',
    providers: [ProjectService, ReportService, TestService, HTTP_PROVIDERS],
    directives: [NgSwitch, NgFor]
})
export class DashboardComponent implements OnInit {

    constructor(private _projectService: ProjectService,
                private _reportService: ReportService,
                private _testService: TestService) {}

    errorMessage: string;
    project = new Project();
    testReport = new TestReport();
    testCount: number;

    ngOnInit() {
        this.getProject();
        this.getTestReport();
        this.getTestCount();
    }

    getTestCount() {
        this._testService.getTestCount()
            .subscribe(
                count => this.testCount = count,
                error => this.errorMessage = <any>error);
    }

    getProject() {
        this._projectService.getActiveProject()
                            .subscribe(
                                project => this.project = project,
                                error => this.errorMessage = <any>error);
    }

    getTestReport() {
        this._reportService.getLatest()
                            .subscribe(
                                report => this.testReport = report,
                                error => this.errorMessage = <any>error);
    }
}