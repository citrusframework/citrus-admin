import {Component, OnInit} from '@angular/core';
import {TestGroup, TestResult, Test} from "../../model/tests";
import {Alert} from "../../model/alert";
import {AlertService} from "../../service/alert.service";
import {Observable} from "rxjs";
import {TestService} from "../../service/test.service";
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import {Frame} from "stompjs";
import {TestStateService} from "./test.state";
import {LoggingOutput} from "../../model/logging.output";

@Component({
    templateUrl: 'test-group-run.html'
})
export class TestGroupRunComponent implements OnInit {

    constructor(private testService: TestService,
                private testState: TestStateService,
                private alertService: AlertService) {
        this.stompClient = Stomp.over(new SockJS(`/api/logging`) as WebSocket);
        this.stompClient.connect({}, (frame:Frame) => {
            if (frame) {
                this.subscribe();
            }
        });
    }

    packages:Observable<TestGroup[]>;

    running = false;
    stompClient: any;

    finished: TestResult[];

    processOutput = "";

    ngOnInit() {
        this.packages = this.testState.packages;
    }

    executeAll() {
        this.testService.executeAll()
            .subscribe(
                result => {
                    this.processOutput = "";
                    this.running = true;
                    this.finished = [];
                },
                error => this.notifyError(<any>error));
    }

    execute(group: TestGroup) {
        this.testService.executeGroup(group)
            .subscribe(
                result => {
                    this.processOutput = "";
                    this.running = true;
                    this.finished = [];
                },
                error => this.notifyError(<any>error));
    }

    subscribe() {
        if (this.stompClient) {
            this.stompClient.subscribe('/topic/log-output', (output:Stomp.Message)=> {
                var loggingOutput: LoggingOutput = JSON.parse(output.body);
                jQuery('pre.logger').scrollTop(jQuery('pre.logger')[0].scrollHeight);
                this.processOutput += loggingOutput.msg;
            });
            this.stompClient.subscribe('/topic/results', (output:Stomp.Message) => {
                var result = JSON.parse(output.body);
                this.handleResult(result);
            });
        }
    }

    handleResult(result: any) {
        let testResult = new TestResult();
        testResult.test = new Test();

        testResult.test.name = result.name;
        testResult.success = result.success;

        this.finished.push(testResult);
    }

    notifyError(error: any) {
        this.alertService.add(new Alert("danger", error.message, false));
    }
}