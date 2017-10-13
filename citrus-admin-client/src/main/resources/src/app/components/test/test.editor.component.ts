import {Component, OnInit, OnDestroy} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {TestGroup, Test, TestDetail} from "../../model/tests";
import * as jQueryVar from 'jquery'
import {TestStateActions, TestStateService} from "./test.state";
import {Observable, Subscription} from "rxjs";
import {RouterState} from "../../service/router-state.service";

declare var jQuery:typeof jQueryVar;

@Component({
    host: {
       '(document:keyup)': 'handleKeyUp($event)',
    },
    templateUrl: 'test-editor.html'
})
export class TestEditorComponent implements OnInit, OnDestroy {

    constructor(private router: Router,
                private testActions:TestStateActions,
                private testState:TestStateService,
                private route:ActivatedRoute,
                private routerState:RouterState) {}

    private routeSubscription: Subscription;
    private openTestSubscription: Subscription;
    private selectedDetailSubscription: Subscription;
    private lastTestSubscription: Subscription;

    openTests: Test[] = [];
    packages:Observable<TestGroup[]>;
    selectedTest: Observable<Test>;
    selectedDetail: TestDetail;

    ngOnInit() {
        this.packages = this.testState.packages;
        this.selectedTest = this.testState.selectedTest;
        
        this.openTestSubscription = this.testState.openTabs.subscribe(tests => {
            this.openTests.forEach(open => {
                if (!tests.find(t => t.name == open.name)) {
                    this.openTests.splice(this.openTests.indexOf(open), 1);
                }
            });

            tests.forEach(t => {
                if (!this.openTests.find(open => open.name == t.name)) {
                    this.openTests.push(t);
                }
            });
        });

        this.routeSubscription = this.route
            .params
            .filter(p => p['name'] != null)
            .flatMap(({name}) => this.testState.getTestByName(name))
            .filter(t => t != null)
            .subscribe(t => {
                this.testActions.addTab(t);
                this.testActions.selectTest(t);
                this.testActions.fetchDetails(t);
            });

        this.selectedDetailSubscription = this.testState.selectedTestDetail
            .subscribe(detail => {
                this.selectedDetail = detail;
            });

        this.lastTestSubscription = Observable.combineLatest(
            this.routerState.path.filter(p => p === '/tests/editor').take(1),
            this.testState.selectedTest.filter(t => t != null).take(1)
        ).subscribe(([u, t]) => {
            this.navigateToTest(t);
        });
    }

    ngOnDestroy(): void {
        this.selectedDetail = undefined;
        this.openTests = [];
        
        if (this.routeSubscription) {
            this.routeSubscription.unsubscribe();
        }

        if (this.openTestSubscription) {
            this.openTestSubscription.unsubscribe();
        }

        if (this.selectedDetailSubscription) {
            this.selectedDetailSubscription.unsubscribe();
        }

        if (this.lastTestSubscription) {
            this.lastTestSubscription.unsubscribe();
        }
    }

    onTabClosed(test:Test) {
        if (this.openTests.length < 2) {
            this.selectedDetail = undefined;
            this.router.navigate(['/tests', 'editor']);
        }

        this.testActions.removeTab(test);
    }

    onTabSelected(test:Test) {
        this.navigateToTest(test);
    }

    openTestList() {
        (jQuery('#dialog-list-tests') as any).modal();
    }

    closeTestList() {
        (jQuery('#dialog-list-tests') as any).modal('hide');
    }

    open(test: Test) {
        this.navigateToTest(test);
    }

    handleKeyUp(event:KeyboardEvent) {
        if (event && event.key.toUpperCase() == "O") {
            this.openTestList();
        }
    }

    private navigateToTest(test:Test) {
        this.router.navigate(['/tests', 'editor', test.name]);
    }
}