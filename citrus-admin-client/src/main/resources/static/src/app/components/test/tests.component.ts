import {Component, OnInit, HostListener} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {TabComponent} from "../util/tabs";
import {TestGroup, Test} from "../../model/tests";
import {TestService} from "../../service/test.service";
import {Alert} from "../../model/alert";
import {AlertService} from "../../service/alert.service";
import * as _ from 'lodash';
import * as jQueryVar from 'jquery'
import {TestStateActions, TestStateService} from "./test.state";
import {Observable} from "rxjs";

declare var jQuery:typeof jQueryVar;

@Component({
    host: {
       '(document:keyup)': 'handleKeyUp($event)',
    },
    templateUrl: 'tests.html'
})
export class TestsComponent implements OnInit {

    constructor(
                private _alertService: AlertService,
                private _router: Router,
                private testActions:TestStateActions,
                private testState:TestStateService,
                private route:ActivatedRoute
    ) {
    }

    packageAvailable:Observable<boolean>;
    openTests: Observable<Test[]>;
    packages:Observable<TestGroup[]>
    testNames: Observable<string[]>;
    tests: Observable<Test[]>;

    ngOnInit() {
        this.packages = this.testState.packages;
        this.packageAvailable = this.testState.packagesAvailable;
        this.openTests = this.testState.openTabs;
        this.testNames = this.testState.testNames
        this.tests = this.testState.tests;
        this.testActions.fetchPackages();
        this.testState.openTabs.filter(ot => ot.length <= 0).subscribe(() =>this._router.navigate(['/tests']));
    }

    onTabClosed(test:Test) {
        this.testActions.removeTab(test);

    }

    onTabSelected(test:Test) {
        this.testActions.selectTest(test)
        this.navigateToTestInfo(test);
    }

    onTestSelected(name: string) {
        this.tests
            .map(ts => ts.find(t => t.name === name))
            .subscribe(t => this.testActions.addTab(t))
    }

    openTestList() {
        (jQuery('#dialog-list-tests') as any).modal();
    }

    closeTestList() {
        (jQuery('#dialog-list-tests') as any).modal('hide');
    }

    open(test: Test) {
        this.testActions.addTab(test);
        this.navigateToTestInfo(test)
    }

    handleKeyUp(event:KeyboardEvent) {
        if (event && event.key == "o") {
            this.openTestList();
        }
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error.message, false));
    }

    private navigateToTestInfo(test:Test) {
        this._router.navigate(['/tests', test.name, 'info']);
    }
}