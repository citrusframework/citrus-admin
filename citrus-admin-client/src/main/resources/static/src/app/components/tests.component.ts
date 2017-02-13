import {Component, OnInit, HostListener} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Tab} from "./util/tabs";
import {TestGroup, Test} from "../model/tests";
import {TestService} from "../service/test.service";
import {Alert} from "../model/alert";
import {AlertService} from "../service/alert.service";
import * as _ from 'lodash';
import * as jQueryVar from 'jquery'

declare var jQuery:typeof jQueryVar;

@Component({
    host: {
       '(document:keyup)': 'handleKeyUp($event)',
    },
    templateUrl: 'tests.html'
})
export class TestsComponent implements OnInit {

    constructor(private _testService: TestService,
                private _alertService: AlertService,
                private _route: ActivatedRoute,
                private _router: Router) {
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
                    // TODO: I dont think this will work... further investigation needed here
                    this.tests = _.reduce(packages, function(collected:any, testPackage:any) { return collected.concat(testPackage.tests); }, []);
                    this.testNames = _.map(this.tests, function(test:any) { return test.name; });
                    
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
            this._router.navigate(["/tests"]);
        }
    }

    onTabSelected(tab: Tab) {
        var test = this.openTests.find(test => test.name === tab.id);
        if (test) {
            this.activeTest = test;
            this._router.navigate(['/tests', test.name]);
        }
    }

    onTestSelected(name: string) {
        this.open(_.find(this.tests, function(test:any){ return test.name === name }));
    }

    openTestList() {
        (jQuery('#dialog-list-tests') as any).modal();
    }

    closeTestList() {
        (jQuery('#dialog-list-tests') as any).modal('hide');
    }

    open(test: Test) {
        if (this.openTests.indexOf(test) < 0) {
            this.openTests.push(test);
        } else {
            (jQuery('ul.nav-tabs').find("a[name = '" + test.name + "']") as any).tab('show');
        }

        this._router.navigate(['/tests', test.name]);
        this.activeTest = test;
    }

    handleKeyUp(event:KeyboardEvent) {
        console.log('Open with key')
        if (event && event.key == "o") {
            this.openTestList();
        }
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error.message, false));
    }
}