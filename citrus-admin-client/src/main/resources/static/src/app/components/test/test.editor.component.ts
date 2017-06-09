import {Component, OnInit, OnDestroy} from '@angular/core';
import {Router, ActivatedRoute, NavigationStart} from '@angular/router';
import {TestGroup, Test} from "../../model/tests";
import {Alert} from "../../model/alert";
import {AlertService} from "../../service/alert.service";
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

    constructor(
                private alertService: AlertService,
                private router: Router,
                private testActions:TestStateActions,
                private testState:TestStateService,
                private routerState:RouterState) {}

    private openTestSubscription: Subscription;
    private selectedTestSubscription: Subscription;
    private lastTestSubscription: Subscription;

    openTests: Observable<Test[]>;
    packages:Observable<TestGroup[]>;
    selectedTest: Observable<Test>;

    ngOnInit() {
        this.packages = this.testState.packages;
        this.openTests = this.testState.openTabs;
        this.selectedTest = this.testState.selectedTest;

        /** Navigate to the main page if we dont have open tabs in state **/
        this.openTestSubscription = this.testState
            .openTabs
            .filter(ot => ot.length === 0)
            .subscribe(() => this.router.navigate(['tests', 'editor', 'open']));

        /** Navigate to a test route if selected tab is changed **/
        this.selectedTestSubscription = this.testState.selectedTest.filter(t => t != null).subscribe(t => {
            this.navigateToTest(t);
        });

        this.lastTestSubscription = Observable.combineLatest(
            this.routerState.path.filter(p => p === '/tests/editor').take(1),
            this.testState.selectedTest.filter(t => t != null).take(1)
        ).subscribe(([u, t]) => {
            this.navigateToTest(t);
        });
    }

    ngOnDestroy(): void {
        if(this.openTestSubscription) {
            this.openTestSubscription.unsubscribe();
        }

        if(this.selectedTestSubscription) {
            this.selectedTestSubscription.unsubscribe();
        }

        if(this.lastTestSubscription) {
            this.lastTestSubscription.unsubscribe();
        }
    }

    onTabClosed(test:Test) {
        this.testActions.removeTab(test);
    }

    onTabSelected(test:Test) {
        this.testActions.selectTest(test);
    }

    openTestList() {
        (jQuery('#dialog-list-tests') as any).modal();
    }

    closeTestList() {
        (jQuery('#dialog-list-tests') as any).modal('hide');
    }

    open(test: Test) {
        this.testActions.addTab(test);
    }

    handleKeyUp(event:KeyboardEvent) {
        if (event && event.key.toUpperCase() == "O") {
            this.openTestList();
        }
    }

    notifyError(error: any) {
        this.alertService.add(new Alert("danger", error, false));
    }

    private navigateToTest(test:Test) {
        console.log("Navigate to " + test.name);
        this.router.navigate(['/tests', 'editor', test.name]);
    }
}