import {Component,  Input, OnChanges} from 'angular2/core';
import {NgSwitch, NgFor} from 'angular2/common';
import {HTTP_PROVIDERS} from 'angular2/http';
import {Test, TestDetail} from "../model/tests";
import {TestService} from "../service/test.service";

@Component({
    selector: "test-detail",
    templateUrl: 'templates/test-detail.html',
    directives: [NgSwitch, NgFor]
})
export class TestDetailComponent implements OnChanges {

    @Input() test: Test;

    constructor(private _testService: TestService) {}

    errorMessage: string;
    detail: TestDetail;

    ngOnChanges() {
        if (!this.test) {
            this.detail = undefined;
        } else {
            this.getTestDetail();
        }
    }

    getTestDetail() {
        this._testService.getTestDetail(this.test)
            .subscribe(
                detail => this.detail = detail,
                error => this.errorMessage = <any>error);
    }
}