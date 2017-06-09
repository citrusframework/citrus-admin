import {Component, OnInit, OnDestroy} from '@angular/core';
import {TestDetail, Test} from "../../../model/tests";
import {Alert} from "../../../model/alert";
import {AlertService} from "../../../service/alert.service";
import {ActivatedRoute} from "@angular/router";
import {TestStateService, TestStateActions} from "../test.state";
import {Observable, Subscription} from "rxjs";

@Component({
    selector: "test-detail",
    templateUrl: 'test-detail.html'
})
export class TestDetailComponent implements OnInit, OnDestroy {

    constructor(private _alertService: AlertService,
                private testState:TestStateService,
                private testAction:TestStateActions,
                private route:ActivatedRoute) {}

    private subscription: Subscription;

    detail: Observable<TestDetail>;
    type: Observable<string>;

    ngOnInit() {
        this.subscription = this.route
            .params
            .filter(p => p['name'] != null)
            .flatMap(({name}) => this.testState.getTestByName(name))
            .filter(t => t != null)
            .subscribe(t => {
                this.testAction.addTab(t);
                this.testAction.selectTest(t);
                this.testAction.fetchDetails(t);
            });

        this.detail = this.testState.selectedTestDetail;
        this.type = this.detail.map(detail => detail.type);
    }

    ngOnDestroy(): void {
        if(this.subscription) {
            this.subscription.unsubscribe();
        }
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error, false));
    }
}

