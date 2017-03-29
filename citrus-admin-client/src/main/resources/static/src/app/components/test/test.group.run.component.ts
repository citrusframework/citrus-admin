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
import {SocketEvent} from "../../model/socket.event";
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
        this._router.navigate(['/tests', 'detail', test.name, 'info']);
    }

    openConsole() {
        (jQuery('#dialog-console') as any).modal();
    }

    subscribe() {
        if (this.stompClient) {
            this.stompClient.subscribe('/topic/log-output', (output:Stomp.Message)=> {
                var event: SocketEvent = JSON.parse(output.body);
                this.processOutput += event.msg;
                this.currentOutput = event.msg;
                this.handle(event);
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

    handle(event: SocketEvent) {
        if ("PROCESS_FAILED" == event.type || "PROCESS_SUCCESS" == event.type) {
            this.running = false;
            this.currentOutput = this.processOutput;
            jQuery('pre.logger').scrollTop(jQuery('pre.logger')[0].scrollHeight);
        }

        if ("PROCESS_FAILED" == event.type) {
            this.alertService.add(new Alert("warning", "Test run failed '" + event.processId + "': " + event.msg, false));
        }
        
        if ("PROCESS_SUCCESS" == event.type) {
            this.alertService.add(new Alert("success", "Test run success '" + event.processId + "'", true));
        }
    }

    notifyError(error: any) {
        this.alertService.add(new Alert("danger", error.message, false));
    }
}