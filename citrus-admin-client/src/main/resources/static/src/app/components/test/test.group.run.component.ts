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
import {Router} from "@angular/router";

@Component({
    templateUrl: 'test-group-run.html'
})
export class TestGroupRunComponent implements OnInit {

    constructor(private testService: TestService,
                private testState: TestStateService,
                private alertService: AlertService,
                private _router: Router) {
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

    results: TestResult[] = [];

    processOutput = "";
    currentOutput = "";

    selected:TestGroup;

    ngOnInit() {
        this.packages = this.testState.packages;
    }

    execute() {
        this.results.forEach(r => r.success = undefined);

        if (this.selected) {
            this.testService.executeGroup(this.selected)
                .subscribe(
                    result => {
                        this.processOutput = "";
                        this.currentOutput = "";
                        this.running = true;
                    },
                    error => this.notifyError(<any>error));
        } else {
            this.testService.executeAll()
                .subscribe(
                    result => {
                        this.processOutput = "";
                        this.currentOutput = "";
                        this.running = true;
                    },
                    error => this.notifyError(<any>error));
        }
    }

    select(group:TestGroup) {
        this.selected = group;

        if (this.selected) {
            this.results = this.selected.tests.map(t => {
                let result = new TestResult();
                result.test = t;
                return result;
            });
        } else {
            this.results = [];
        }
    }

    open(test: Test) {
        this._router.navigate(['/tests', 'editor', test.name, 'info']);
    }

    openConsole() {
        (jQuery('#dialog-console') as any).modal();
    }

    subscribe() {
        if (this.stompClient) {
            this.stompClient.subscribe('/topic/log-output', (output:Stomp.Message)=> {
                var loggingOutput: LoggingOutput = JSON.parse(output.body);
                this.processOutput += loggingOutput.msg;
                this.currentOutput = loggingOutput.msg;
                this.handle(loggingOutput);
            });
            this.stompClient.subscribe('/topic/results', (output:Stomp.Message) => {
                var result = JSON.parse(output.body);
                this.handleResult(result);
            });
        }
    }

    handleResult(result: TestResult) {
        let found:TestResult = this.results.find(r => r.test.name == result.test.name);

        if (found) {
            found.success = result.success;
            found.processId = result.processId;
        } else {
            this.results.unshift(result);
        }
    }

    handle(output: LoggingOutput) {
        if ("PROCESS_FAILED" == output.event || "PROCESS_SUCCESS" == output.event) {
            this.running = false;
            jQuery('pre.logger').scrollTop(jQuery('pre.logger')[0].scrollHeight);
            this.currentOutput = this.processOutput;
            jQuery('pre.logger').scrollTop(jQuery('pre.logger')[0].scrollHeight);
        }

        if ("PROCESS_FAILED" == output.event) {
            this.alertService.add(new Alert("danger", "Process failed " + output.processId + ":" + output.msg, false));
        }
        
        if ("PROCESS_SUCCESS" == output.event) {
            this.alertService.add(new Alert("success", "Process success " + output.processId, true));
        }
    }

    notifyError(error: any) {
        this.alertService.add(new Alert("danger", error.message, false));
    }
}