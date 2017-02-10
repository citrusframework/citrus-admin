import {Component, Input, OnInit} from '@angular/core';
import {Test, TestResult} from "../model/tests";
import {ReportService} from "../service/report.service";
import {Alert} from "../model/alert";
import {AlertService} from "../service/alert.service";

@Component({
    selector: "test-result",
    template: `<div class="test-result">
  <h3 [textContent]="result?.success ? 'SUCCESS' : 'FAILED'"></h3>
  <div *ngIf="result?.errorCause" class="error-details">
    <h3 [textContent]="result?.errorCause"></h3>
    <h4 [textContent]="result?.errorMessage"></h4>
    <pre [textContent]="result?.stackTrace"></pre>
  </div>   
</div>`
})
export class TestResultComponent implements OnInit {

    @Input() test: Test;

    result: TestResult;

    constructor(private _reportService: ReportService,
                private _alertService: AlertService) {}

    ngOnInit() {
        this.getTestResult();
    }

    getTestResult() {
        this._reportService.getTestResult(this.test)
            .subscribe(
                result => this.result = result,
                error => {
                    this.notifyError(<any>error);
                });
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error.message, false));
    }
}