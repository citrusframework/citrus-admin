import {Component,  Input, OnChanges} from 'angular2/core';
import {NgSwitch, NgFor} from 'angular2/common';
import {HTTP_PROVIDERS} from 'angular2/http';
import {Pills, Pill} from "./util/pills";
import {Test, TestDetail} from "../model/tests";
import {TestService} from "../service/test.service";
import {SourceCodeComponent} from "./source.code.component";
import {TestActionComponent} from "./design/test.action.component";
import {TestTransitionComponent} from "./design/test.transition.component";

@Component({
    selector: "test-detail",
    templateUrl: 'templates/test-detail.html',
    directives: [NgSwitch, NgFor, Pills, Pill,
        SourceCodeComponent, TestActionComponent, TestTransitionComponent]
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