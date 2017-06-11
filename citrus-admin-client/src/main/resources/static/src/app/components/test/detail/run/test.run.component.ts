import {Component, Input, OnInit} from '@angular/core';
import {TestDetail} from "../../../../model/tests";
import {TestService} from "../../../../service/test.service";
import {SocketEvent} from "../../../../model/socket.event";
import {Alert} from "../../../../model/alert";
import {AlertService} from "../../../../service/alert.service";
import {LoggingService} from "../../../../service/logging.service";

@Component({
    selector: "test-run",
    templateUrl: 'test-run.html'
})
export class TestRunComponent implements OnInit {
    @Input() detail: TestDetail;

    constructor(private _testService: TestService,
                private loggingService: LoggingService,
                private _alertService: AlertService) {
    }

    completed = 0;
    failed = false;

    finishedActions = 0;

    processOutput = "";
    currentOutput = "";

    execute() {
        this.processOutput = "";
        this.currentOutput = "";
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

    ngOnInit() {
        this.loggingService.logOutput
            .subscribe((e: SocketEvent) => {
                jQuery('pre.logger').scrollTop(jQuery('pre.logger')[0].scrollHeight);
                this.processOutput += e.msg;
                this.currentOutput = e.msg;
                this.handle(e);
            });

        this.loggingService.testEvents
            .subscribe(this.handle);
    }

    openConsole() {
        (jQuery('#dialog-console') as any).modal();
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
            this.currentOutput = this.processOutput;
            jQuery('pre.logger').scrollTop(jQuery('pre.logger')[0].scrollHeight);
        }
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error, false));
    }
}