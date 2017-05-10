import {Component, OnInit} from '@angular/core';
import {TestGroup, Test} from "../../model/tests";
import {Alert} from "../../model/alert";
import {AlertService} from "../../service/alert.service";
import {TestStateService} from "./test.state";
import {Observable} from "rxjs";
import {AppState} from "../../state.module";
import {Store} from "@ngrx/store";
import {go} from '@ngrx/router-store';

@Component({
    templateUrl: 'test-list.html'
})
export class TestListComponent implements OnInit {

    constructor(
            private alertService: AlertService,
            private store:Store<AppState>,
            private testState:TestStateService) {
    }

    packages:Observable<TestGroup[]>;
    testNames: Observable<string[]>;
    tests: Observable<Test[]>;

    ngOnInit() {
        this.packages = this.testState.packages;
        this.testNames = this.testState.testNames;
        this.tests = this.testState.tests;
    }

    open(test: Test) {
        this.navigateToTestInfo(test);
    }

    notifyError(error: any) {
        this.alertService.add(new Alert("danger", error, false));
    }

    private navigateToTestInfo(test:Test) {
        this.store.dispatch(go(['/tests', 'detail', test.name]));
    }
}