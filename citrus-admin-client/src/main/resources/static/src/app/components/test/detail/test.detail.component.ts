import {Component, OnInit} from '@angular/core';
import {Test, TestDetail} from "../../../model/tests";
import {Alert} from "../../../model/alert";
import {AlertService} from "../../../service/alert.service";
import {ActivatedRoute, Router} from "@angular/router";
import {TestStateService, TestStateActions} from "../test.state";
import {Observable} from "rxjs";
import * as _ from 'lodash'

declare var jQuery:any;

@Component({
    selector: "test-detail",
    templateUrl: 'test-detail.html'
})
export class TestDetailComponent implements OnInit {

    test: Test;

    menuEntries = [
        {name:"Info",       link:'info',    icon:'fa-cube'},
        {name:"Sources",    link:'sources', icon:'fa-code'},
        {name:"Design",     link:'design',  icon:'fa-sitemap'},
        {name:"Run",        link:'run',     icon:'fa-play-circle'},
        {name:"Results",    link:'results', icon:'fa-tasks'},
    ]

    constructor(
                private _alertService: AlertService,
                private testState:TestStateService,
                private testAction:TestStateActions,
                private activatedRoute:ActivatedRoute,
                private router:Router
    ) {
    }

    detail: TestDetail;

    ngOnInit() {
        Observable.combineLatest(
            this.activatedRoute.params.filter(p => p["name"] != null),
            this.testState.tests.filter(t => t != null).first(),
        )
        .map(([{name}, tests]) => tests.find(t => t.name === name))
        .filter(t => t != null)
        .subscribe(test => {
            this.testState.openTabs
                //.take(1)
                .filter(openTests => !_.includes(openTests, test) && openTests.length > 0)
                .subscribe(() => this.testAction.addTab(test))
            this.testAction.selectTest(test);
            this.testAction.fetchDetails(test);
            this.testState.latestDetailView.first()
                .delay(100) // for some reasons we need to wait a bit before routing in ngOnInit
                .subscribe(lv => this.router.navigate(['tests','detail', test.name, lv]))
        })

    }

    isActive(link:string) {
        return _.endsWith(this.router.url, link);
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error.message, false));
    }
}

