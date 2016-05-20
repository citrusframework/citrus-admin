import {Component, OnInit} from 'angular2/core';
import {NgSwitch, NgFor} from 'angular2/common';
import {HTTP_PROVIDERS} from 'angular2/http';
import {Tabs, Tab} from "./util/tabs";
import {TestGroup, Test} from "../model/tests";
import {TestService} from "../service/test.service";
import {TestDetailComponent} from "./test.detail.component";
import {AutoComplete} from "./util/autocomplete";
import {RouteParams} from 'angular2/router';

declare var _;

@Component({
    templateUrl: 'app/components/tests.html',
    providers: [TestService, HTTP_PROVIDERS],
    directives: [NgSwitch, NgFor, Tabs, Tab,
        TestDetailComponent, AutoComplete]
})
export class TestsComponent implements OnInit {

    constructor(private _testService: TestService,
                private _routeParams: RouteParams) {
        this.packages = [];
        this.tests = [];
        this.testNames = [];
        this.openTests = [];
    }

    errorMessage: string;

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

                    if (this._routeParams.get('name')) {
                        this.onTestSelected(this._routeParams.get('name'));
                    }
                },
                error => this.errorMessage = <any>error);
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

    open(test: Test) {
        if (this.openTests.indexOf(test) < 0) {
            this.activeTest = test;
            this.openTests.push(test);
        }
    }
}