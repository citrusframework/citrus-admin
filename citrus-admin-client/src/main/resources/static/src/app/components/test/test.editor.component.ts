import {Component, OnInit} from '@angular/core';
import {Router, ActivatedRoute, NavigationStart} from '@angular/router';
import {TestGroup, Test} from "../../model/tests";
import {Alert} from "../../model/alert";
import {AlertService} from "../../service/alert.service";
import * as jQueryVar from 'jquery'
import {TestStateActions, TestStateService} from "./test.state";
import {Observable} from "rxjs";
import {go} from "@ngrx/router-store";
import {AppState} from "../../state.module";
import {Store} from "@ngrx/store";

declare var jQuery:typeof jQueryVar;

@Component({
    host: {
       '(document:keyup)': 'handleKeyUp($event)',
    },
    templateUrl: 'test-editor.html'
})
export class TestEditorComponent implements OnInit {

    constructor(
                private alertService: AlertService,
                private route: ActivatedRoute,
                private testActions:TestStateActions,
                private testState:TestStateService,
                private store:Store<AppState>
    ) {
    }

    packages:Observable<TestGroup[]>;
    selectedTest:Observable<Test>;

    ngOnInit() {
        this.packages = this.testState.packages;
        this.selectedTest = this.testState.selectedTest;
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
        if (event && event.key.toUpperCase() == "O") {
            this.openTestList();
        }
    }

    notifyError(error: any) {
        this.alertService.add(new Alert("danger", error, false));
    }

    private navigateToTestInfo(test:Test) {
        this.store.dispatch(go(['/tests', 'detail', test.name]));
    }

}