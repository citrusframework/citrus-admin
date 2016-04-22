import {Component,  Input, OnChanges} from 'angular2/core';
import {NgSwitch, NgFor} from 'angular2/common';
import {HTTP_PROVIDERS} from 'angular2/http';
import {Pills, Pill} from "./util/pills";
import {Test, TestDetail} from "../model/tests";
import {TestService} from "../service/test.service";
import {SourceCodeComponent} from "./source.code.component";
import {TestDesignerComponent} from "./design/test.designer.component";
import {TestExecuteComponent} from "./test.execute.component";

@Component({
    selector: "test-detail",
    templateUrl: 'app/components/test-detail.html',
    directives: [NgSwitch, NgFor, Pills, Pill,
        SourceCodeComponent, TestDesignerComponent, TestExecuteComponent]
})
export class TestDetailComponent implements OnChanges {

    @Input() test: Test;

    constructor(private _testService: TestService) {
    }

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