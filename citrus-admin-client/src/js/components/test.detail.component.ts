import {Component,  Input, OnChanges} from 'angular2/core';
import {NgSwitch, NgFor} from 'angular2/common';
import {HTTP_PROVIDERS} from 'angular2/http';
import {Pills, Pill} from "./util/pills";
import {Test, TestDetail} from "../model/tests";
import {TestService} from "../service/test.service";

@Component({
    selector: "test-detail",
    templateUrl: 'templates/test-detail.html',
    directives: [NgSwitch, NgFor, Pills, Pill]
})
export class TestDetailComponent implements OnChanges {

    @Input() test: Test;

    constructor(private _testService: TestService) {
        this.display = "test-details";
    }

    errorMessage: string;
    display: string;
    detail: TestDetail;

    ngOnChanges() {
        if (!this.test) {
            this.detail = undefined;
        } else {
            this.getTestDetail();
        }
    }

    onPillSelected(pill: Pill) {
        this.display = pill.id;
    }

    getTestDetail() {
        this._testService.getTestDetail(this.test)
            .subscribe(
                detail => this.detail = detail,
                error => this.errorMessage = <any>error);
    }
}