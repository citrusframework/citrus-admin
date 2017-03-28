import {Component, OnInit} from '@angular/core';
import {TestGroup, Test} from "../../model/tests";
import {Alert} from "../../model/alert";
import {AlertService} from "../../service/alert.service";
import {TestStateService, TestStateActions} from "./test.state";
import {Observable} from "rxjs";
import {Router} from "@angular/router";

@Component({
    templateUrl: 'test-list.html'
})
export class TestListComponent implements OnInit {

    constructor(
            private alertService: AlertService,
            private router: Router,
            private testActions:TestStateActions,
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
        this.testActions.addTab(test);
        this.navigateToTestInfo(test);
    }

    openByName(name: string) {
        this.tests
            .first()
            .map(ts => ts.find(t => t.name === name))
            .subscribe(t => {
                this.testActions.addTab(t);
                this.navigateToTestInfo(t);
            })
    }

    notifyError(error: any) {
        this.alertService.add(new Alert("danger", error.message, false));
    }

    private navigateToTestInfo(test:Test) {
        this.router.navigate(['/tests', 'detail', test.name, 'info']);
    }
}