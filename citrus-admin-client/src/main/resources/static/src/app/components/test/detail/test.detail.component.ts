import {Component, OnInit, OnDestroy} from '@angular/core';
import {TestDetail} from "../../../model/tests";
import {Alert} from "../../../model/alert";
import {AlertService} from "../../../service/alert.service";
import {ActivatedRoute} from "@angular/router";
import {TestStateService, TestStateActions} from "../test.state";
import {Subscription} from "rxjs";
import {LoggingService} from "../../../service/logging.service";
import {TestService} from "../../../service/test.service";
import {SocketEvent} from "../../../model/socket.event";
import * as moment from 'moment';
import * as _ from 'lodash';
import {Message} from "../../../model/message";

@Component({
    selector: "test-detail",
    templateUrl: 'test-detail.html'
})
export class TestDetailComponent implements OnInit, OnDestroy {

    constructor(private _alertService: AlertService,
                private _testService: TestService,
                private loggingService: LoggingService,
                private testState:TestStateService,
                private testAction:TestStateActions,
                private route:ActivatedRoute) {}

    private routeSubscription: Subscription;
    private detailSubscription: Subscription;

    detail: TestDetail;

    completed = 0;
    failed = false;

    finishedActions = 0;

    logs = "";
    loggingFrame = "";

    ngOnInit() {
        this.routeSubscription = this.route
            .params
            .filter(p => p['name'] != null)
            .flatMap(({name}) => this.testState.getTestByName(name))
            .filter(t => t != null)
            .subscribe(t => {
                this.testAction.addTab(t);
                this.testAction.selectTest(t);
                this.testAction.fetchDetails(t);
            });

        this.detailSubscription = this.testState.selectedTestDetail
            .subscribe(detail => this.detail = detail);

        this.loggingService.logOutput
            .subscribe((e: SocketEvent) => {
                jQuery('pre.logger').scrollTop(jQuery('pre.logger')[0].scrollHeight);
                this.logs += e.msg;
                this.loggingFrame = e.msg;
                this.handle(e);
            });

        this.loggingService.testEvents
            .subscribe(this.handle);
    }

    ngOnDestroy(): void {
        if(this.routeSubscription) {
            this.routeSubscription.unsubscribe();
        }

        if(this.detailSubscription) {
            this.detailSubscription.unsubscribe();
        }
    }

    execute() {
        this.logs = "";
        this.loggingFrame = "";
        this.failed = false;
        this.completed = 0;
        this.finishedActions = 0;
        this.detail.messages = [];
        this.detail.running = true;
        this.detail.result = undefined;
        this._testService.execute(this.detail)
            .subscribe(
                result => {
                    this.detail.result = result;
                },
                error => this.notifyError(<any>error));
    }

    handle(event: SocketEvent) {
        console.log('Handle', event);
        if ("PROCESS_START" == event.type) {
            this.completed = 1;
        } else if ("TEST_START" == event.type) {
            this.completed = 1;
        } else if ("TEST_ACTION_FINISH" == event.type) {
            this.finishedActions++;

            if (this.detail.actions.length) {
                this.completed = Math.round((this.finishedActions / this.detail.actions.length) * 100);
            } else if (this.completed < 90) {
                this.completed += 2;
            }
        } else if ("TEST_FAILED" == event.type || "PROCESS_FAILED" == event.type) {
            this.failed = true;
        } else {
            if (this.completed < 11) {
                this.completed++;
            }
        }

        if ("PROCESS_FAILED" == event.type || "PROCESS_SUCCESS" == event.type) {
            this.completed = 100;
            this.detail.running = false;
            this.loggingFrame = this.logs;
            jQuery('pre.logger').scrollTop(jQuery('pre.logger')[0].scrollHeight);
        }
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error, false));
    }
}

