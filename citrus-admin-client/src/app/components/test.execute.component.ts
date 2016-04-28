import {Component,  Input} from 'angular2/core';
import {HTTP_PROVIDERS} from 'angular2/http';
import {TestDetail, TestResult} from "../model/tests";
import {TestService} from "../service/test.service";
import {TestProgressComponent} from "./test.progress.component";
import {LoggingOutput} from "../model/logging.output";

declare var SockJS;
declare var Stomp;
declare var jQuery;

@Component({
    selector: "test-execute",
    template: `<button (click)="execute()" type="button" class="btn btn-success" [disabled]="running"><i class="fa fa-play"></i> Run</button>
<test-progress [completed]="completed"></test-progress>
<pre class="logger" [textContent]="processOutput"></pre>`,
    directives: <any> [ TestProgressComponent ]
})
export class TestExecuteComponent {
    @Input() detail: TestDetail;

    constructor(private _testService: TestService) {
        this.stompClient = Stomp.over(new SockJS('/logging'));
        this.stompClient.connect({}, frame => {
            if (frame) {
                this.subscribe();
            }
        });
    }

    result: TestResult;
    running = false;
    completed = 0;
    errorMessage: string;
    stompClient: any;

    finishedActions = 0;

    processOutput = "";

    execute() {
        this.processOutput = "";
        this.running = true;
        this.completed = 0;
        this.finishedActions = 0;
        this._testService.execute(this.detail)
            .subscribe(
                result => {
                    this.result = result;
                },
                error => this.errorMessage = <any>error);
    }

    subscribe() {
        if (this.stompClient) {
            this.stompClient.subscribe('/topic/log-output', output => {
                var loggingOutput: LoggingOutput = JSON.parse(output.body);
                console.log(loggingOutput);
                jQuery('pre.logger').scrollTop(jQuery('pre.logger')[0].scrollHeight);
                this.processOutput += loggingOutput.msg;
                this.handle(loggingOutput);
            });
        }
    }

    handle(output: LoggingOutput) {
        if ("PROCESS_START" == output.event) {
            this.completed = 1;
        } else if ("PROCESS_FAILED" == output.event) {
            this.running = false;
        } else if ("TEST_START" == output.event) {
            this.completed = 1;
        } else if ("TEST_ACTION_FINISH" == output.event) {
            this.finishedActions++;

            if (this.detail.actions.length) {
                this.completed = Math.round((this.finishedActions / this.detail.actions.length) * 100);
            } else if (this.completed < 90) {
                this.completed += 10;
            }
        } else if ("TEST_SUCCESS" == output.event) {
            this.running = false;
            this.completed = 100;
        } else if ("TEST_FAILED" == output.event) {
            this.running = false;
            this.completed = 100;
        } else if ("INBOUND_MESSAGE" == output.event || "OUTBOUND_MESSAGE" == output.event) {
        }
    }
}