import {Component,  Input} from 'angular2/core';
import {HTTP_PROVIDERS} from 'angular2/http';
import {TestDetail} from "../model/tests";
import {TestService} from "../service/test.service";
import {LoggingOutput} from "../model/logging.output";

declare var SockJS;
declare var Stomp;
declare var jQuery;

@Component({
    selector: "test-execute",
    template: '<button (click)="execute()" type="button" class="btn btn-success"><i class="fa fa-play"></i> Run</button><pre class="logger" [textContent]="processOutput"></pre>'
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

    processId: string;
    errorMessage: string;
    stompClient: any;

    processOutput = "";

    execute() {
        this.processOutput = "";
        this._testService.execute(this.detail)
            .subscribe(
                processId => {
                    this.processId = processId;
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
            });
        }
    }
}