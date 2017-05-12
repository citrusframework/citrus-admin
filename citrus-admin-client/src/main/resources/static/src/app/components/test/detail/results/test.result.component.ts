import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {TestResult, TestDetail, Test} from "../../../../model/tests";
import {Alert} from "../../../../model/alert";
import {AlertService} from "../../../../service/alert.service";
import {Observable} from "rxjs";
import {TestStateActions, TestStateService} from "../../test.state";
import {Subscription} from "rxjs/Subscription";

@Component({
    selector: "test-result",
    template: `
    <div class="table-responsive">
      <h3 *ngIf="!result"><i class="fa fa-file-text-o"></i> No results found for test {{detail?.name}}</h3>
      <div *ngIf="result" [class]="result.success ? 'color-success' : 'color-danger'">
          <h3><i class="fa fa-file-text-o"></i> {{detail?.name}}: &nbsp;&nbsp;&nbsp;<span [textContent]="result.success ? 'SUCCESS' : 'FAILED'"></span></h3>
          <div *ngIf="result.errorCause" class="error-details">
            <h4 [textContent]="result.errorCause"></h4>
            <h5 [textContent]="result.errorMessage"></h5>
            <pre [textContent]="result.stackTrace"></pre>
          </div>
        </div>
    </div>`
})
export class TestResultComponent {

    @Input() detail: TestDetail;
    @Input() result: TestResult;

    constructor(
                private _alertService: AlertService
    ) {}


    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error, false));
    }
}

@Component({
    selector: 'test-result-outlet',
    template: `
      <test-result [detail]="detail|async" [result]="result|async"></test-result>
    `
})
export class TestResultOutletComponent implements OnInit, OnDestroy {
    private subscribtion = new Subscription();
    detail:Observable<TestDetail>;
    result:Observable<TestResult>;
    constructor(
        private testState:TestStateService,
        private testActions:TestStateActions,
    ) {
    }

    ngOnInit(): void {
        this.detail = this.testState.selectedTestDetail;
        this.subscribtion.add(
            this.detail.subscribe(d => this.testActions.fetchResults(d))
        )
        this.result = this.testState.resultsForSelectedTest;
    }

    ngOnDestroy() {
        this.subscribtion.unsubscribe();
    }
}