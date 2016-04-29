import {Component,  Input} from 'angular2/core';
import {HTTP_PROVIDERS} from 'angular2/http';
import {TestDetail, TestResult} from "../model/tests";
import {TestService} from "../service/test.service";
import {TestProgressComponent} from "./test.progress.component";
import {LoggingOutput} from "../model/logging.output";
import {Message} from "../model/message";
import {Pills, Pill} from "./util/pills";
import {NgFor, NgIf} from "angular2/common";

declare var SockJS;
declare var Stomp;
declare var jQuery;
declare var _;
declare var moment;

@Component({
    selector: "test-execute",
    template: `<button (click)="execute()" type="button" class="btn btn-default" [disabled]="running"><i class="fa fa-play"></i> Run</button>
<test-progress [completed]="completed" [failed]="failed"></test-progress>
<pills navigation="true">
    <pill pill-id="console" pill-title="Console" active="true" pill-icon="fa fa-file-text-o"><pre class="logger" [textContent]="processOutput"></pre></pill>
    <pill pill-id="messages" pill-title="Messages" pill-icon="fa fa-envelope-o">
        <pre *ngIf="!messages || messages?.length == 0">No messages yet!</pre>
        <div *ngFor="#message of messages">
            <span *ngIf="message.type == 'OUTBOUND'" class="badge badge-emphasis badge-outbound"><i class="fa fa-sign-out">&nbsp;<b>Out</b></i></span>
            <span *ngIf="message.type == 'INBOUND'" class="badge badge-emphasis badge-inbound"><i class="fa fa-sign-in">&nbsp;<b>In</b></i></span>
            <pre [style.color]="message.type == 'OUTBOUND' ? '#000099' : '#026ebe'">{{message.data}}</pre>
        </div>
    </pill>
</pills>`,
    directives: <any> [ NgIf, NgFor, Pills, Pill, TestProgressComponent ]
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
    failed = false;
    errorMessage: string;
    stompClient: any;

    finishedActions = 0;

    processOutput = "";

    messages: Message[];

    execute() {
        this.processOutput = "";
        this.running = true;
        this.failed = false;
        this.completed = 0;
        this.finishedActions = 0;
        this.messages = [];

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
            this.failed = true;
            this.completed = 100;
        } else if ("INBOUND_MESSAGE" == output.event) {
            this.messages.push(new Message(_.uniqueId(event), 'INBOUND', output.msg, moment()));
        } else if ("OUTBOUND_MESSAGE" == output.event) {
            this.messages.push(new Message(_.uniqueId(event), 'OUTBOUND', output.msg, moment()));
        } else {
            if (this.completed < 30) {
                this.completed++;
            }
        }
    }
}