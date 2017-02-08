import {Component,  Input} from '@angular/core';
import {TestDetail, TestResult} from "../model/tests";
import {TestService} from "../service/test.service";
import {LoggingOutput} from "../model/logging.output";
import {Message} from "../model/message";
import {Alert} from "../model/alert";
import {AlertService} from "../service/alert.service";
import * as _ from 'lodash';
import * as jQuery from 'jquery';
import * as moment from 'moment';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import {Frame} from "stompjs";


@Component({
    selector: "test-execute",
    template: `<button (click)="execute()" type="button" class="btn btn-default" [disabled]="running"><i class="fa fa-play"></i> Run</button>
<test-progress [completed]="completed" [failed]="failed" *ngIf="running || completed == 100"></test-progress>
<pills navigation="true">
    <pill pill-id="console" pill-title="Console" active="true" pill-icon="fa fa-file-text-o"><pre class="logger" [textContent]="processOutput"></pre></pill>
    <pill pill-id="messages" pill-title="Messages" pill-icon="fa fa-envelope-o">
        <ul class="list-group">
          <li class="list-group-item" *ngIf="!messages || messages?.length == 0">No messages yet!</li>
          <li class="list-group-item test-message clickable" *ngFor="let message of messages" [message]="message"></li>
        </ul>
    </pill>
</pills>`
})
export class TestExecuteComponent {
    @Input() detail: TestDetail;

    constructor(private _testService: TestService,
                private _alertService: AlertService) {
        this.stompClient = Stomp.over(new SockJS(`/api/logging`) as WebSocket);
        this.stompClient.connect({}, (frame:Frame) => {
            if (frame) {
                this.subscribe();
            }
        });
    }

    result: TestResult;
    running = false;
    completed = 0;
    failed = false;
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
        console.log(this.detail)
        this._testService.execute(this.detail)
            .subscribe(
                result => {
                    this.result = result;
                },
                error => this.notifyError(<any>error));
    }

    subscribe() {
        if (this.stompClient) {
            this.stompClient.subscribe('/topic/log-output', (output:Stomp.Message)=> {
                var loggingOutput: LoggingOutput = JSON.parse(output.body);
                jQuery('pre.logger').scrollTop(jQuery('pre.logger')[0].scrollHeight);
                this.processOutput += loggingOutput.msg;
                this.handle(loggingOutput);
            });
            this.stompClient.subscribe('/topic/messages', (output:Stomp.Message) => {
                var message = JSON.parse(output.body);
                console.log(message);
                this.handleMessage(message);
            });
        }
    }

    handleMessage(message:any) {
        this.messages.push(new Message(_.uniqueId(), message.type, message.msg, moment().toISOString()));
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
        } else {
            if (this.completed < 11) {
                this.completed++;
            }
        }
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error.message, false));
    }
}