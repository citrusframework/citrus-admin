import {Injectable} from "@angular/core";
import {Action, Store} from "@ngrx/store";
import {Test, TestGroup, TestDetail} from "../../model/tests";
import {AppState} from "../../state.module";
import {AsyncActionType, AsyncActions, assign, Extender} from "../../util/redux.util";
import {Effect, Actions} from "@ngrx/effects";
import {TestService} from "../../service/test.service";
import {Observable} from "rxjs";

export interface TestState {
    tests:Test[]
    packages:TestGroup[],
    testNames:string[],
    openTabs:Test[],
    selectedTest:Test,
    details: {[testSignature:string]:TestDetail}
}

const TestStateInit:TestState = {
    tests:[],
    packages: [],
    testNames: [],
    openTabs:[],
    selectedTest:null,
    details: {}
}

@Injectable()
export class TestStateEffects {
    constructor(
        private actions:AsyncActions,
        private testService:TestService,
        private rxAction:Actions
    ) {}
    @Effect() package = this.actions
        .handleEffect(TestStateActions.PACKAGES, () => this.testService.getTestPackages())

    @Effect() detail = this.actions
        .handleEffect(TestStateActions.DETAIL, ({payload}) => this.testService.getTestDetail(payload).map(detail => ({detail, test:payload})))

    /*
    @Effect() selectTest = this.rxAction.ofType(TestStateActions.SELECT_TAB)
        .map(({payload}) => ({type:TestStateActions.DETAIL.FETCH, payload}))
    */
}

@Injectable()
export class TestStateService {
    constructor(private store:Store<AppState>) {}

    get packages():Observable<TestGroup[]> { return this.store.select(s => s.tests.packages) }

    get packagesAvailable():Observable<boolean> { return this.packages.map(p => p.length > 0)}

    get openTabs():Observable<Test[]> { return this.store.select(p => p.tests.openTabs) }

    get testNames():Observable<string[]> { return this.tests.map(p => p.map(t => t.name)) }

    get tests():Observable<Test[]> { return this.packages.map(tg => tg.reduce((c:Test[], tg:TestGroup) => [...c, ...tg.tests], [] as Test[]))}

    get selectedTest():Observable<Test> { return this.store.select(p => p.tests.selectedTest)}

    get selectedTestDetail():Observable<TestDetail> { return this.selectedTest.filter(t => t != null).switchMap(t => this.getDetail(t))}

    getDetail(test:Test):Observable<TestDetail> {
        return this.store.select(s => s.tests.details[testSignature(test)]).filter(d => d != null)
    }
}

@Injectable()
export class TestStateActions {
    static PACKAGES = AsyncActionType('TEST.PACKAGE')
    static DETAIL = AsyncActionType('TEST.DETAIL')
    static ADD_TAB = 'TEST.ADD_TAB';
    static REMOVE_TAB = 'TEST.REMOVE_TAB';
    static SELECT_TAB = 'TEST.SELECT_TAB';
    constructor(private store:Store<AppState>) {}
    fetchPackages() {
        this.store.dispatch({type:TestStateActions.PACKAGES.FETCH})
    }

    addTab(payload:Test) {
        this.store.dispatch({type:TestStateActions.ADD_TAB, payload})
    }

    removeTab(payload:Test) {
        this.store.dispatch({type:TestStateActions.REMOVE_TAB, payload})
    }

    selectTest(payload:Test) {
        this.store.dispatch({type:TestStateActions.SELECT_TAB, payload})
    }

    fetchDetails(payload: Test) {
        this.store.dispatch(({type:TestStateActions.DETAIL.FETCH, payload}))
    }
}

const testSignature = (t:Test) => `${t.packageName}.${t.className}.${t.methodName}:${t.name}.${t.type}`;

export function reduce(state:TestState = TestStateInit, action:Action) {
    const stateExtender = new Extender(state);
    switch (action.type) {
        case TestStateActions.PACKAGES.SUCCESS:
            const packages = action.payload as TestGroup[];
            const tests = packages.reduce((c:Test[], tg:TestGroup) => [...c, ...tg.tests], [] as Test[]);
            const testNames = tests.map(t => t.name);
            return stateExtender.with({ packages, tests, testNames });
        case TestStateActions.ADD_TAB: {
            const openTabs = [...state.openTabs, action.payload]
            return stateExtender.with({openTabs});
        }
        case TestStateActions.REMOVE_TAB: {
            const openTabs = state.openTabs.filter(t => t !== action.payload)
            return stateExtender.with({openTabs});
        }
        case TestStateActions.SELECT_TAB: {
            const selectedTest = action.payload
            return stateExtender.with({selectedTest});
        }
        case TestStateActions.DETAIL.SUCCESS: {
            const detail = action.payload.detail;
            const test = action.payload.test;
            const detailExtender = new Extender(state.details);
            const details = detailExtender.with({[testSignature(test)]: detail})
            return stateExtender.with({details});
        }
    }
    return state;
}