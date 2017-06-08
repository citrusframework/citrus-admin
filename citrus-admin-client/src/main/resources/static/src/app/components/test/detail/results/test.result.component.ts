import {Component, Input, OnChanges, OnInit} from '@angular/core';
import {TestResult, TestDetail, Test} from "../../../../model/tests";
import {ReportService} from "../../../../service/report.service";
import {Alert} from "../../../../model/alert";
import {AlertService} from "../../../../service/alert.service";
import {Observable} from "rxjs";
import {TestStateService} from "../../test.state";

@Component({
    selector: "test-result",
    template: `<div class="table-responsive">
  <h2 *ngIf="!result"><i class="fa fa-file-text-o"></i> No results found for test</h2>
  <div *ngIf="result" [class]="result.success ? 'color-success' : 'color-danger'">
      <h2><i class="fa fa-file-text-o"></i> {{detail?.name}}: &nbsp;&nbsp;&nbsp;<span [textContent]="result.success ? 'SUCCESS' : 'FAILED'"></span></h2>
      <div *ngIf="result.errorCause" class="error-details">
        <h3 [textContent]="result.errorCause"></h3>
        <h4 [textContent]="result.errorMessage"></h4>
        <pre [textContent]="result.stackTrace"></pre>
      </div>
  </div>
</div>`
})
export class TestResultComponent implements OnChanges {

    @Input() detail: TestDetail;

    result: TestResult;

    constructor(private _reportService: ReportService,
                private _alertService: AlertService) {}

    ngOnChanges() {
        this.getTestResult();
    }

    getTestResult() {
        this._reportService.getTestResult(<Test> this.detail)
            .subscribe(
                result => this.result = result,
                error => {
                    this.notifyError(<any>error);
                });
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error, false));
    }
}

@Component({
    selector: 'test-result-outlet',
    template: `<test-result [detail]="detail|async"></test-result>`
})
export class TestResultOutletComponent implements OnInit {
    detail:Observable<TestDetail>;
    constructor(private testState:TestStateService) {
    }

    ngOnInit(): void {
        this.detail = this.testState.selectedTestDetail;
    }

}