import {Component, OnInit} from '@angular/core';
import {TestReport} from "../model/test.report";
import {ReportService} from "../service/report.service";
import {AlertService} from "../service/alert.service";
import {Router} from "@angular/router";
import {Test} from "../model/tests";
import {Alert} from "../model/alert";

@Component({
    templateUrl: "test-report.html"
})
export class TestReportComponent implements OnInit {

    constructor(private _reportService: ReportService,
                private _alertService: AlertService,
                private _router: Router) {
    }

    report: TestReport = new TestReport();

    ngOnInit() {
        this.getTestReport();
    }

    getTestReport() {
        this._reportService.getLatest()
            .subscribe(
                report => this.report = report,
                error => this.notifyError(<any>error));
    }

    open(test: Test) {
        this._router.navigate(['/tests', test.name]);
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error.message, false));
    }
}