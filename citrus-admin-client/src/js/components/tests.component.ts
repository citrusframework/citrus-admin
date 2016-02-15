import {Component, OnInit} from 'angular2/core';
import {NgSwitch, NgFor} from 'angular2/common';
import {HTTP_PROVIDERS} from 'angular2/http';
import {Tabs, Tab} from "./util/tabs";
import {TestPackage, Test} from "../model/tests";
import {TestService} from "../service/test.service";
import {TestDetailComponent} from "./test.detail.component";

@Component({
    templateUrl: 'templates/tests.html',
    providers: [TestService, HTTP_PROVIDERS],
    directives: [NgSwitch, NgFor, Tabs, Tab, TestDetailComponent]
})
export class TestsComponent implements OnInit {

    constructor(private _testService: TestService) {
        this.packages = [];
        this.openTests = [];
    }

    errorMessage: string;

    activeTest: Test;
    openTests: Test[];
    packages: TestPackage[];

    ngOnInit() {
        this.getTestPackages();
    }

    getTestPackages() {
        this._testService.getTestPackages()
            .subscribe(
                packages => this.packages = packages,
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

    open(test: Test) {
        if (this.openTests.indexOf(test) < 0) {
            this.activeTest = test;
            this.openTests.push(test);
        }
    }
}