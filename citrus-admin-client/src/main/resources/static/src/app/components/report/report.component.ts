import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {TestReport} from "../../model/test.report";
import {ReportService} from "../../service/report.service";
import {AlertService} from "../../service/alert.service";
import {Test} from "../../model/tests";
import {Alert} from "../../model/alert";
import {AppState} from "../../state.module";
import {Store} from "@ngrx/store";
import {go} from '@ngrx/router-store'

@Component({
    templateUrl: "report.html"
})
export class ReportComponent implements OnInit {

    constructor(private _reportService: ReportService,
                private _alertService: AlertService,
                private _router: Router,
                private store:Store<AppState>
    ) {}

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
        console.log('Open')
        this.store.dispatch(go(['/tests', 'detail', test.name]));
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error, false));
    }
}