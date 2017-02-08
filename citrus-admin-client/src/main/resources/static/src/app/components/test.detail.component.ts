import {Component,  Input, OnChanges} from '@angular/core';
import {Test, TestDetail} from "../model/tests";
import {TestService} from "../service/test.service";
import {Alert} from "../model/alert";
import {AlertService} from "../service/alert.service";

declare var jQuery:any;

@Component({
    selector: "test-detail",
    templateUrl: 'test-detail.html'
})
export class TestDetailComponent implements OnChanges {

    @Input() test: Test;

    constructor(private _testService: TestService,
                private _alertService: AlertService) {
    }

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
                error => {
                    this.notifyError(<any>error);

                    // close tab as error usually makes it unusable
                    jQuery('li.active > button.close').click();
                });
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error.message, false));
    }
}