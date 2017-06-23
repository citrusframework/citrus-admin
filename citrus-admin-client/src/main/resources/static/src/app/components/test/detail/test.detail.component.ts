import {Component, OnInit, Input} from '@angular/core';
import {TestDetail} from "../../../model/tests";
import {Alert} from "../../../model/alert";
import {AlertService} from "../../../service/alert.service";
import {Subscription} from "rxjs";
import {LoggingService} from "../../../service/logging.service";
import {TestService} from "../../../service/test.service";
import {SocketEvent} from "../../../model/socket.event";

@Component({
    selector: "test-detail",
    templateUrl: 'test-detail.html'
})
export class TestDetailComponent implements OnInit {

    @Input() detail: TestDetail;

    constructor(private _alertService: AlertService,
                private _testService: TestService,
                private loggingService: LoggingService) {}

    private logginOutputSubscription: Subscription;
    private logginEventSubscription: Subscription;

    completed = 0;
    failed = false;

    finishedActions = 0;

    logs = "";
    loggingFrame = "";

    ngOnInit() {
        this.logginOutputSubscription = this.loggingService.logOutput
            .subscribe((e: SocketEvent) => {
                jQuery('pre.logger').scrollTop(jQuery('pre.logger')[0].scrollHeight);
                this.logs += e.msg;
                this.loggingFrame = e.msg;
                this.handle(e);
            });

        this.logginEventSubscription = this.loggingService.testEvents
            .subscribe(this.handle);
    }

    ngOnDestroy(): void {
        if(this.logginOutputSubscription) {
            this.logginOutputSubscription.unsubscribe();
        }

        if(this.logginEventSubscription) {
            this.logginEventSubscription.unsubscribe();
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
        this._alertService.add(new Alert("danger", JSON.stringify(error), false));
    }
}

