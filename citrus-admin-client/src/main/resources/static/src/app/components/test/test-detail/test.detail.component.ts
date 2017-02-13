import {Component, Input, OnChanges, OnInit} from '@angular/core';
import {Test, TestDetail} from "../../../model/tests";
import {TestService} from "../../../service/test.service";
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
export class TestDetailComponent implements OnInit, OnChanges {

    test: Test;

    menuEntries = [
        {name:"Info",       link:'info',    icon:'fa-cube'},
        {name:"Sources",    link:'sources', icon:'fa-code'},
        {name:"Design",     link:'design',  icon:'fa-sitemap'},
        {name:"Run",        link:'run',     icon:'fa-play-circle'},
        {name:"Results",    link:'results', icon:'fa-tasks'},
    ]

    constructor(private _testService: TestService,
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
            this.activatedRoute.params.filter(p => p != null),
            this.testState.tests.filter(t => t != null),
            this.testState.openTabs
        ).subscribe(([{name}, tests, openTests]) => {
            const test = tests.find(t => t.name === name);
            if(test) {
                const inTab = _.includes(openTests, test);
                if(!inTab) {
                    this.testAction.addTab(test);
                }
                this.testAction.selectTest(test);
                this.testAction.fetchDetails(test);
                this.router.navigate(['tests', test.name, 'info'])
            }
        })
    }

    isActive(link:string) {
        return _.endsWith(this.router.url, link);
    }

    ngOnChanges() {
        if (!this.test) {
            this.detail = undefined;
        } else {
            this.getTestDetail();
        }
    }

    getTestDetail() {
        this._testService.getTestDetail(this.test)
            .subscribe(
                detail => this.detail = detail,
                error => {
                    this.notifyError(<any>error);

                    // close tab as error usually makes it unusable
                    jQuery('li.active > button.close').click();
                });
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error.message, false));
    }
}

