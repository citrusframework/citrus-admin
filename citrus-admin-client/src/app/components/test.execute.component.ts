import {Component,  Input, OnChanges, AfterViewInit} from 'angular2/core';
import {HTTP_PROVIDERS} from 'angular2/http';
import {TestDetail} from "../model/tests";
import {TestService} from "../service/test.service";

@Component({
    selector: "test-execute",
    template: '<button (click)="execute()" type="button" class="btn btn-success"><i class="fa fa-play"></i> Run</button>'
})
export class TestExecuteComponent {
    @Input() detail: TestDetail;

    constructor(private _testService: TestService) {}

    processId: string;
    errorMessage: string;

    execute() {
        this._testService.execute(this.detail)
            .subscribe(
                processId => {
                    this.processId = processId;
                },
                error => this.errorMessage = <any>error);
    }
}