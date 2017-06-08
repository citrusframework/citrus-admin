import {Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from "@angular/router";
import {TestDetail} from "../../../model/tests";
import {TestStateActions, TestStateService} from "../test.state";
import {Observable} from "rxjs/Observable";
import {log, notNull} from "../../../util/redux.util";
@Injectable()
export class TestDetailRouteResolver implements Resolve<TestDetail> {

    constructor(
        private testState:TestStateService,
        private testActions:TestStateActions
    ) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): TestDetail | Observable<TestDetail> | Promise<TestDetail> {
        const testName = route.params['name'];
        this.testActions.fetchPackages();
        this.testState.getTestByName(testName).share()
            .filter(t => t != null)
            .subscribe(t => {
                this.testActions.addTab(t);
                this.testActions.selectTest(t)

        })
        return this.testState.selectedTest.filter(notNull());
        //return Observable.from([new TestDetail()]);
    }



}