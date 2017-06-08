import {Component, OnInit} from '@angular/core';
import {Test, TestDetail} from "../../../model/tests";
import {Alert} from "../../../model/alert";
import {AlertService} from "../../../service/alert.service";
import {ActivatedRoute, Router} from "@angular/router";
import {TestStateService, TestStateActions} from "../test.state";
import {Observable} from "rxjs";
import * as _ from 'lodash'
import {notNull} from "../../../util/redux.util";
import {AppState} from "../../../state.module";
import {Store} from "@ngrx/store";
import {go} from '@ngrx/router-store';

declare var jQuery:any;

@Component({
    selector: "test-detail",
    templateUrl: 'test-detail.html'
})
export class TestDetailComponent implements OnInit {

    test: Test;
    openTests:Observable<Test[]>;
    selectedTest: Observable<Test>;
    detail: TestDetail;

    menuEntries = [
        {name:"Info",       link:'info',    icon:'fa-cube'},
        {name:"Sources",    link:'sources', icon:'fa-code'},
        {name:"Design",     link:'design',  icon:'fa-sitemap'},
        {name:"Run",        link:'run',     icon:'fa-play-circle'}
    ]

    constructor(
                private _alertService: AlertService,
                private testState:TestStateService,
                private testAction:TestStateActions,
                private router:Router,
                private route:ActivatedRoute,
                private store:Store<AppState>
    ) {
    }

    onTabClosed(test:Test) {
        this.testAction.removeTab(test);
    }

    onTabSelected(test:Test) {
        this.testAction.selectTest(test);
        this.navigateToTestInfo(test);
    }

    ngOnInit() {
        this.selectedTest = this.testState.selectedTest;
        this.openTests = this.testState.openTabs;
        this.testState.selectedTest.filter(notNull()).subscribe(t => this.testAction.fetchDetails(t))
        this.route
            .params
            .filter(p => p['name'] != null)
            .flatMap(({name}) => this.testState.getTestByName(name))
            .filter(t => t != null)
            .subscribe(t => {
                this.testAction.fetchDetails(t);
                this.testAction.addTab(t);
                this.testAction.selectTest(t);
            })
    }

    isActive(link:string) {
        return _.endsWith(this.router.url, link);
    }

    private navigateToTestInfo(test:Test) {
        this.store.dispatch(go(['/tests', 'detail', test.name]));
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error, false));
    }
}

