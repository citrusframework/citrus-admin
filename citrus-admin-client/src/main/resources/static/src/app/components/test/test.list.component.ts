import {Component, OnInit} from '@angular/core';
import {TestGroup, Test} from "../../model/tests";
import {Alert} from "../../model/alert";
import {AlertService} from "../../service/alert.service";
import {TestStateService} from "./test.state";
import {Observable} from "rxjs";
import {Router} from "@angular/router";

@Component({
    templateUrl: 'test-list.html'
})
export class TestListComponent implements OnInit {

    constructor(
            private alertService: AlertService,
            private router: Router,
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
        this.navigateToTest(test.name);
    }

    openByName(name:string) {
        this.navigateToTest(name);
    }

    notifyError(error: any) {
        this.alertService.add(new Alert("danger", error, false));
    }

    private navigateToTest(name:string) {
        this.router.navigate(['/tests', 'editor', name]);
    }
}