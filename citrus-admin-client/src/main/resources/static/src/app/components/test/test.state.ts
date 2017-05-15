import {Injectable} from "@angular/core";
import {Action, Store} from "@ngrx/store";
import {Test, TestGroup, TestDetail, TestResult} from "../../model/tests";
import {AppState} from "../../state.module";
import {
    AsyncActionType, AsyncActions, IdMap, toArray, toIdMap, checkPath, notNull, log,
    Action as GenericAction, deleteFromMap
} from "../../util/redux.util";
import {Effect, Actions} from "@ngrx/effects";
import {TestService} from "../../service/test.service";
import {Observable} from "rxjs";
import * as _ from 'lodash';
import {routerActions, go} from '@ngrx/router-store';
import {SocketEvent} from "../../model/socket.event";
import {Message} from "../../model/message";
import * as moment from 'moment';
import {Tupel} from "../../util/type.util";
import {ReportService} from "../../service/report.service";
import {TestReport} from "../../model/test.report";

export type TestMap = IdMap<Test>;
export type TestGroupMap = IdMap<TestGroup>;
export type TestDetailMap = IdMap<TestDetail>;

export class TestExecutionInfo {
    processOutput = "";
    currentOutput = "";
    running = false;
    failed = false;
    completed = 0;
    finishedActions = 0;
    messages: Message[] = [];
    lastUpdate:number;
    touched = false;
}

export interface TestState {
    tests: TestMap,
    packages: TestGroupMap,
    testNames: string[],
    openTabs: string[],
    selectedTest: string,
    details: TestDetailMap,
    latestDetailView: string,
    results: IdMap<TestResult>,
    executionInfo: IdMap<TestExecutionInfo>
}

export const TestStateInit: TestState = {
    tests: {},
    packages: {},
    testNames: [],
    openTabs: [],
    selectedTest: '',
    details: {},
    latestDetailView: 'info',
    results: {},
    executionInfo: {}
};


@Injectable()
export class TestStateService {

    constructor(private store: Store<AppState>) {
    }

    get packages(): Observable<TestGroup[]> {
        return this.store.select(s => s.tests.packages).map(toArray)
    }

    get openTabs(): Observable<Test[]> {
        return Observable.combineLatest(
            this.store.select(p => p.tests.openTabs),
            this.store.select(p => p.tests.tests)
        )
            .map(([openTabs, tests]) => openTabs.map(t => tests[t]))
    }

    get testNames(): Observable<string[]> {
        return this.tests.map(p => p.map(t => t.name))
    }

    get tests(): Observable<Test[]> {
        return this.packages.map(tg => tg.reduce((c: Test[], tg: TestGroup) => [...c, ...tg.tests], [] as Test[]))
    }

    getTestByName(name: string) {
        return this.tests.map(tests => _.find(tests, t => t.name === name));
    }

    get selectedTest(): Observable<Test> {
        return (Observable.combineLatest(
            this.store.select(s => s.tests.selectedTest),
            this.store.select(s => s.tests.tests)
        ).map(([s, t]) => t[s]))
    }

    get selectedTestDetail(): Observable<TestDetail> {
        return Observable.combineLatest(
            this.selectedTest.filter(t => t != null),
            this.details)
            .switchMap(([t]) => this.getDetail(t))
    }

    get details(): Observable<IdMap<TestDetail>> {
        return this.store.select(s => s.tests.details);
    }

    get results(): Observable<IdMap<TestResult>> {
        return this.store.select(s => s.tests.results);
    }

    getDetail(test: Test): Observable<TestDetail> {
        return this.details
            .map(details => details[test.name])
            .filter(d => d != null)
    }

    get resultsForSelectedTest(): Observable<TestResult> {
        return Observable.combineLatest(
            this.selectedTestDetail,
            this.results)
            .switchMap(([t]) => this.getResult(t))
    }

    getResult(test: Test): Observable<TestResult> {
        return this.results.map(results => results[test.name])
            .filter(notNull())
    }

    get latestDetailView() {
        return this.store.select(s => s.tests.latestDetailView || 'info')
    }

    get executionInfos(): Observable<IdMap<TestExecutionInfo>> {
        return this.store.select(s => s.tests.executionInfo);
    }

    getExecutionInfo(test: Test): Observable<TestExecutionInfo> {
        return this.executionInfos.map(e => e[test.name]).map(e => e || new TestExecutionInfo());
    }

    get executionInfoForSelectedTest() {
        return Observable.combineLatest(
            this.selectedTestDetail,
            this.executionInfos)
            .switchMap(([t]) => this.getExecutionInfo(t))
    }

}

@Injectable()
export class TestStateEffects {
    constructor(private actions: AsyncActions,
                private testService: TestService,
                private testState: TestStateService,
                private reportService: ReportService,
                private actions$: Actions) {
    }

    socketEventFinally = this.actions$
        .ofType(TestStateActions.SOCKET_EVENT)
        .filter((a: GenericAction<Tupel<TestDetail, SocketEvent>>) => a.payload[1].type === SocketEvent.Types.PROCESS_SUCCESS || a.payload[1].type === SocketEvent.Types.PROCESS_FAILED)
        .map((a: GenericAction<Tupel<TestDetail, SocketEvent>>) => a.payload[0])

    @Effect() package = this.actions
        .handleEffect(TestStateActions.PACKAGES, () => this.testService.getTestPackages());

    @Effect() detail = this.actions
        .handleEffect<Test>(TestStateActions.DETAIL, ({payload}) => this.testService.getTestDetail(payload));

    @Effect() execute = this.actions
        .handleEffect<TestDetail>(TestStateActions.EXECUTE, ({payload}) => this.testService.execute(payload));

    @Effect() executeGroup = this.actions
        .handleEffect<TestGroup>(TestStateActions.EXECUTE_GROUP, ({payload}) => this.testService.executeGroup(payload));

    @Effect() executeAll = this.actions
        .handleEffect<TestDetail>(TestStateActions.EXECUTE_ALL, () => this.testService.executeAll());

    @Effect() latestResultsAfterExecution = this.actions$
        .ofType(
            TestStateActions.EXECUTE_ALL.SUCCESS, TestStateActions.EXECUTE_GROUP.SUCCESS
        )
        .switchMap(r => this.socketEventFinally)
        .map(() => ({type:TestStateActions.REPORT_LATEST.FETCH}));


    @Effect() report = this.actions
        .handleEffect<TestDetail>(TestStateActions.REPORT, ({payload}) => this.reportService.getTestResult(payload));

    @Effect() reportLatest = this.actions
        .handleEffect<TestDetail>(TestStateActions.REPORT_LATEST, () => this.reportService.getLatest());

    @Effect() ditributeLatestResults = this.actions$.ofType(TestStateActions.REPORT_LATEST.SUCCESS)
        .switchMap(({payload:r}:GenericAction<TestReport>) => {
            return Observable.from(r.results.map(payload => ({type:TestStateActions.REPORT.SUCCESS, payload})))
        })


    @Effect() resultsAfterExecute = this.socketEventFinally
        .withLatestFrom(this.testState.testNames)
        .filter(([td, names]) => _.includes(names, td.name))
        .map(([payload]) => ({type: TestStateActions.REPORT.FETCH, payload}));

    @Effect() routingToLatestView = this.actions$.ofType(routerActions.GO, routerActions.UPDATE_LOCATION)
        .map(({payload: {path}}) => path)
        .filter(path => checkPath(path, [/^tests$/, /^detail$/, /.*/]))
        .switchMap(path => {
            return this.testState.latestDetailView.map(lv => go(`${path}/${lv}`))
        })

    @Effect() routingAfterLastTabClosed = this.actions$.ofType(TestStateActions.REMOVE_TAB)
        .switchMap(a => this.testState.openTabs.map(ot => ot.length === 0))
        .filter(notNull())
        .map(() => go(['/tests', 'detail']))

    @Effect() routingToLastOpenTabs = this.actions$.ofType(routerActions.GO, routerActions.UPDATE_LOCATION)
        .map(({payload: {path}}) => path)
        .map(path => Array.isArray(path) ? path.join('/') : path)
        .filter(path => path === '/tests/detail')
        .switchMap(() => {
            return this.testState.selectedTest.filter(notNull()).map(t => go(['/tests', 'detail', t.name]))
        })
}

@Injectable()
export class TestStateActions {
    static PACKAGES = AsyncActionType('TEST.PACKAGE');
    static DETAIL = AsyncActionType('TEST.DETAIL');
    static EXECUTE = AsyncActionType('TEST.EXECUTE');
    static ADD_TAB = 'TEST.ADD_TAB';
    static REMOVE_TAB = 'TEST.REMOVE_TAB';
    static SELECT_TAB = 'TEST.SELECT_TAB';
    static RESET_EXECUTION = 'TEST.RESET_EXECUTION';
    static SOCKET_EVENT = 'TEST.SOCKET_EVENT';
    static TEST_MESSAGE = 'TEST.TEST_MESSAGE';
    static REPORT = AsyncActionType('TEST.REPORT');
    static EXECUTE_GROUP = AsyncActionType('TEST.EXECUTE_GROUP');
    static EXECUTE_ALL = AsyncActionType('TEST.EXECUTE_ALL');
    static REPORT_LATEST = AsyncActionType('Test.REPORT_LATEST');

    constructor(private store: Store<AppState>) {
    }

    fetchPackages() {
        this.store.dispatch({type: TestStateActions.PACKAGES.FETCH})
    }

    addTab(payload: Test) {
        this.store.dispatch({type: TestStateActions.ADD_TAB, payload})
    }

    removeTab(payload: Test) {
        this.store.dispatch({type: TestStateActions.REMOVE_TAB, payload})
    }

    selectTest(payload: Test) {
        this.store.dispatch({type: TestStateActions.SELECT_TAB, payload})
    }

    fetchDetails(payload: Test) {
        this.store.dispatch(({type: TestStateActions.DETAIL.FETCH, payload}))
    }

    executeTest(payload: TestDetail) {
        this.store.dispatch({type: TestStateActions.EXECUTE.FETCH, payload});
    }

    executeTestGroup(payload: TestGroup) {
        this.store.dispatch({type: TestStateActions.EXECUTE_GROUP.FETCH, payload});
    }

    executeAll() {
        this.store.dispatch({type: TestStateActions.EXECUTE_ALL.FETCH});
    }

    resetResults(payload: TestDetail) {
        this.store.dispatch({type: TestStateActions.RESET_EXECUTION, payload})
    }

    handleSocketEvent(detail: TestDetail, event: SocketEvent) {
        this.store.dispatch({type: TestStateActions.SOCKET_EVENT, payload: [detail, event]})
    }

    handleMessage(detail: TestDetail, m: SocketEvent) {
        const message = new Message(_.uniqueId(), m.type, m.msg, moment().toISOString());
        this.store.dispatch({type: TestStateActions.TEST_MESSAGE, payload: [detail, message]});
    }

    fetchResults(payload: TestDetail) {
        this.store.dispatch({type: TestStateActions.REPORT.FETCH, payload})
    }

    resultSuccess(payload: TestResult) {
        this.store.dispatch(({type: TestStateActions.REPORT.SUCCESS, payload}))
    }
}

export function reduce(state: TestState = TestStateInit, action: Action) {
    switch (action.type) {
        case TestStateActions.PACKAGES.SUCCESS: {
            const packages = toIdMap(action.payload as TestGroup[], tg => tg.name);
            const tests = toIdMap((action.payload as TestGroup[]).reduce((c: Test[], tg: TestGroup) => [...c, ...tg.tests], [] as Test[]), t => t.name);
            const testNames = Object.keys(tests);
            return {...state, packages, tests, testNames}
        }
        case TestStateActions.ADD_TAB: {
            const {name} = action.payload as Test;
            const exists = state.openTabs.indexOf(name);
            const openTabs = exists >= 0 ? state.openTabs : [...state.openTabs, name];
            return {...state, openTabs, selectedTest: name};
        }
        case TestStateActions.REMOVE_TAB: {
            const openTabs = state.openTabs.filter(t => t !== action.payload.name);
            let {selectedTest} = state;
            if (state.selectedTest === action.payload.name) {
                const i = state.openTabs.findIndex(t => t === state.selectedTest);
                selectedTest = openTabs[Math.max(Math.min(i - 1, openTabs.length - 1), 0)];
            }
            if (openTabs.length === 0) {
                selectedTest = '';
            }
            return {...state, selectedTest, openTabs};
        }
        case TestStateActions.SELECT_TAB: {
            if (state.selectedTest === action.payload.name ||
                state.openTabs.indexOf(action.payload.name) === -1) {
                return state;
            } else {
                return {...state, selectedTest: action.payload.name};
            }
        }
        case TestStateActions.DETAIL.SUCCESS: {
            const detail = action.payload as TestDetail;
            return {...state, details: {[detail.name]: detail, ...state.details}};
        }
        case routerActions.UPDATE_LOCATION: {
            const [, , latestDetailView] = /^\/tests\/detail\/([^\\\/]+?)\/([^\\\/]+?)(?:\/(?=$))?$/i.exec(action.payload.path) || ['', '', ''];
            if (latestDetailView !== '') {
                return {...state, latestDetailView}
            }
            return state;
        }
        case TestStateActions.REPORT.SUCCESS: {
            const {payload: result} = action as GenericAction<TestResult>;
            return ({
                ...state,
                results: {
                    ...state.results,
                    [result.test.name]: result
                }
            })
        }
        case TestStateActions.RESET_EXECUTION: {
            const {payload: detail} = action as GenericAction<TestDetail>;
            return ({
                ...state,
                results: deleteFromMap(state.results, detail.name),
                executionInfo: {...state.executionInfo, [detail.name]: new TestExecutionInfo()}
            })
        }
        case TestStateActions.SOCKET_EVENT: {
            const {payload: [detail, event]} = action as GenericAction<Tupel<TestDetail, SocketEvent>>;
            const info = addTimestamp(state.executionInfo[event.processId], event);
            return ({
                ...state,
                executionInfo: {
                    ...state.executionInfo,
                    [event.processId]: reduceSocketEvent(info, detail, event),
                }
            })
        }
        case TestStateActions.TEST_MESSAGE: {
            const {payload: [detail, message]} = action as GenericAction<Tupel<TestDetail, Message>>;
            const info = state.executionInfo[detail.name];
            return ({
                ...state,
                executionInfo: {
                    ...state.executionInfo,
                    [detail.name]: {
                        ...info,
                        messages: [...info.messages,
                            message
                        ]
                    },
                }
            })
        }
    }
    return state;
}

export function addTimestamp(info:TestExecutionInfo, event:SocketEvent):TestExecutionInfo {
    return ({...info, lastUpdate:event.timestamp, touched:true});
}

export function reduceSocketEvent(info: TestExecutionInfo, detail: TestDetail, event: SocketEvent): TestExecutionInfo {
    const {Types} = SocketEvent;
    switch (event.type) {
        case Types.PROCESS_START:
        case Types.TEST_START:
            return ({...info, completed: 1, running: true});
        case Types.TEST_ACTION_FINISH:
            return ({
                ...info,
                finishedActions: info.finishedActions + 1,
                completed: (detail.actions.length) ?
                    Math.round((info.finishedActions / detail.actions.length) * 100) :
                    (info.completed < 90) ?
                        info.completed + 2 : info.completed
            })
        case Types.TEST_FAILED:
            return ({...info, failed: true});
        case Types.PROCESS_FAILED:
        case Types.PROCESS_SUCCESS:
            return ({
                ...info,
                failed: Types.PROCESS_FAILED == event.type,
                completed: 100,
                running: false,
                currentOutput: info.processOutput
            });
        case Types.LOG_MESSAGE:
            return ({
                ...info,
                currentOutput: event.msg,
                processOutput: (info.processOutput || '') + event.msg
            });
    }
    return info;
}