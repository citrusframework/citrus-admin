import {Component, OnInit} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {Tabs, Tab} from "./util/tabs";
import {TestGroup, Test} from "../model/tests";
import {TestService} from "../service/test.service";
import {Alert} from "../model/alert";
import {AlertService} from "../service/alert.service";

declare var _;
declare var jQuery:any;

@Component({
    host: {
        '(document:keyup)': 'handleKeyUp($event)',
    },
    templateUrl: 'app/components/tests.html'
})
export class TestsComponent implements OnInit {

    constructor(private _testService: TestService,
                private _alertService: AlertService,
                private _route: ActivatedRoute) {
        this.packages = [];
        this.tests = [];
        this.testNames = [];
        this.openTests = [];
    }

    activeTest: Test;
    openTests: Test[];
    packages: TestGroup[];
    testNames: string[];
    tests: string[];

    ngOnInit() {
        this.getTestPackages();
    }

    getTestPackages() {
        this._testService.getTestPackages()
            .subscribe(
                packages => {
                    this.packages = packages;
                    this.tests = _.reduce(packages, function(collected, testPackage) { return collected.concat(testPackage.tests); }, []);
                    this.testNames = _.map(this.tests, function(test) { return test.name; });

                    if (this._route.snapshot.params['name'] != null) {
                        this.onTestSelected(this._route.snapshot.params['name']);
                    }
                },
                error => this.notifyError(<any>error));
    }

    onTabClosed(tab: Tab) {
        var test = this.openTests.find(test => test.name === tab.id);
        if (test) {
            this.openTests.splice(this.openTests.indexOf(test), 1);
        }

        if (this.openTests.length === 0) {
            this.activeTest = undefined;
        }
    }

    onTabSelected(tab: Tab) {
        var test = this.openTests.find(test => test.name === tab.id);
        if (test) {
            this.activeTest = test;
        }
    }

    onTestSelected(name: string) {
        this.open(_.find(this.tests, function(test){ return test.name === name }));
    }

    openTestList() {
        jQuery('#dialog-list-tests').modal();
    }

    closeTestList() {
        jQuery('#dialog-list-tests').modal('hide');
    }

    open(test: Test) {
        if (this.openTests.indexOf(test) < 0) {
            this.activeTest = test;
            this.openTests.push(test);
        }
    }

    handleKeyUp(event) {
        if (event && event.key == "O") {
            this.openTestList();
        }
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error.message, false));
    }
}