import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {TestDetail, TestResult} from "../../../../model/tests";
import {SocketEvent} from "../../../../model/socket.event";
import {Alert} from "../../../../model/alert";
import {AlertService} from "../../../../service/alert.service";
import {TestExecutionInfo, TestStateActions, TestStateService} from "../../test.state";
import {Observable} from "rxjs";
import {LoggingService} from "../../../../service/logging.service";
import {log} from "../../../../util/redux.util";
import {Subscription} from "rxjs/Subscription";

@Component({
    selector: 'test-run-outlet',
    template: `
      <test-run
          [detail]="detail|async"
          [executionInfo]="executionInfo|async"
          [result]="result|async"
      ></test-run>
    `
})
export class TestRunOutlet implements OnInit {
    detail: Observable<TestDetail>;
    executionInfo: Observable<TestExecutionInfo>;
    result: Observable<TestResult>;

    constructor(private testState: TestStateService) {
    }

    ngOnInit() {
        this.detail = this.testState.selectedTestDetail;
        this.result = this.testState.resultsForSelectedTest;
        this.executionInfo = this.testState.executionInfoForSelectedTest;
    }
}

@Component({
    selector: "test-run",
    templateUrl: 'test-run.html'
})
export class TestRunComponent implements OnInit, OnDestroy {
    @Input() detail: TestDetail;
    @Input() executionInfo: TestExecutionInfo;
    @Input() result: TestResult;

    subsciption = new Subscription();

    constructor(private loggingService: LoggingService,
                private testActions: TestStateActions,
                private _alertService: AlertService) {
    }

    execute() {
        this.testActions.resetResults(this.detail);
        this.testActions.executeTest(this.detail);
    }

    ngOnInit() {
        [
            this.loggingService.logOutput
                .subscribe((e: SocketEvent) => {
                    this.testActions.handleSocketEvent(this.detail, e)
                }),
            this.loggingService.testEvents
                .subscribe(e => this.testActions.handleSocketEvent(this.detail, e)),

            this.loggingService.messages.do(log('Message'))
                .subscribe(m => this.testActions.handleMessage(
                    this.detail, m
                ))
        ].forEach(s => this.subsciption.add(s));
    }

    ngOnDestroy() {
        this.subsciption.unsubscribe();
    }

    openConsole() {
        (jQuery('#dialog-console') as any).modal();
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error, false));
    }
}