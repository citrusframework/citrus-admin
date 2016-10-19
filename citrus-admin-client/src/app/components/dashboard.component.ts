import {Component, OnInit} from 'angular2/core';
import {NgSwitch, NgFor} from 'angular2/common';
import {HTTP_PROVIDERS} from 'angular2/http';
import {Project} from "../model/project";
import {ProjectService} from "../service/project.service";
import {ReportService} from "../service/report.service";
import {TestService} from "../service/test.service";
import {TestReport} from "../model/test.report";
import {TruncatePipe} from "../util/truncate.pipe";
import {TestGroup} from "../model/tests";
import {Alert, Link} from "../model/alert";
import {AlertService} from "../service/alert.service";

@Component({
    templateUrl: 'app/components/dashboard.html',
    providers: [ProjectService, ReportService, TestService, HTTP_PROVIDERS],
    directives: [NgSwitch, NgFor],
    pipes: [TruncatePipe]
})
export class DashboardComponent implements OnInit {

    constructor(private _projectService: ProjectService,
                private _reportService: ReportService,
                private _testService: TestService,
                private _alertService: AlertService) {}

    project: Project;
    testReport: TestReport;
    latestTests: TestGroup[];
    testCount: number;

    ngOnInit() {
        this.getProject();
        this.getTestReport();
        this.getTestCount();
        this.getLatestTests();
    }

    getTestCount() {
        this._testService.getTestCount()
            .subscribe(
                count => this.testCount = count,
                error => this.notifyError(<any>error));
    }

    getLatestTests() {
        this._testService.getLatest()
            .subscribe(
                tests => this.latestTests = tests,
                error => this.notifyError(<any>error));
    }

    getProject() {
        this._projectService.getActiveProject()
                            .subscribe(
                                project => {
                                    this.project = project;
                                    if (this.project.settings.useConnector && !this.project.settings.connectorActive) {
                                        this._alertService.add(new Alert("warning", "Admin connector is inactive!", true)
                                            .withLink(new Link("#/settings?show=connector", "Fix in project settings")));
                                    }
                                },
                                error => this.notifyError(<any>error));
    }

    getTestReport() {
        this._reportService.getLatest()
                            .subscribe(
                                report => this.testReport = report,
                                error => this.notifyError(<any>error));
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error.message, false));
    }
}