import {
    Component, EventEmitter, Input, OnDestroy, OnInit, Output
} from '@angular/core';
import {  style, transition,
    trigger, animate } from '@angular/animations';
import {TestGroup, TestResult, Test, TestDetail} from "../../model/tests";
import {Alert} from "../../model/alert";
import {AlertService} from "../../service/alert.service";
import {Observable} from "rxjs";
import {TestService} from "../../service/test.service";
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import {Frame} from "stompjs";
import {TestExecutionInfo, TestStateActions, TestStateService} from "./test.state";
import {SocketEvent} from "../../model/socket.event";
import {Router} from "@angular/router";
import {AppState} from "../../state.module";
import {Store} from "@ngrx/store";
import {go} from "@ngrx/router-store";
import {parseBody, StompConnectionService} from "../../service/stomp-connection.service";
import {LoggingService} from "../../service/logging.service";
import {Subscription} from "rxjs/Subscription";
import {IdMap, log, notNull, toArray} from "../../util/redux.util";
import {BehaviorSubject} from "rxjs/BehaviorSubject";
import {Tupel} from "../../util/type.util";
import * as _ from 'lodash';

const AllTests = new TestDetail('all-tests');

@Component({
    templateUrl: 'test-group-run.html'
})
export class TestGroupRunComponent implements OnInit, OnDestroy {

    constructor(private testService: TestService,
                private testState: TestStateService,
                private alertService: AlertService,
                private loggingService: LoggingService,
                private testActions: TestStateActions,
                private store: Store<AppState>) {

    }

    AllTests = AllTests

    private subscription = new Subscription();

    packages: Observable<TestGroup[]>;

    packagesOpen = new BehaviorSubject(true);
    testsOpen: BehaviorSubject<IdMap<boolean>> = new BehaviorSubject({});

    running = false;

    results: Observable<TestResult[]>;

    selected: BehaviorSubject<TestGroup> = new BehaviorSubject(null);

    togglePackage() {
        this.packagesOpen.next(!this.packagesOpen.getValue());
    }

    toggleTests(_package: TestGroup) {
        const open = this.testsOpen.getValue();
        this.testsOpen.next({
            ...open,
            [_package.name]: open[_package.name] ? false : true
        })
    }

    private resultFromTest(test: Test) {
        const result = new TestResult();
        result.test = test;
        return result;
    }

    get currentOutput() {
        return this.selected.switchMap(s => this.testState.getExecutionInfo(s ? new TestDetail(s.name) : AllTests)).map(ei => ei.processOutput)
    }

    get packageList() {
        return this.packagesOpen.switchMap(open => open ? this.packages : Observable.of([]));
    }

    isOpen(_package: TestGroup) {
        return this.testsOpen.map(open => open[_package.name])
    }

    resultListFor(_package: TestGroup) {
        return this.packages
            .map(ps => ps.find(p => p === _package))
            .filter(notNull())
            .withLatestFrom(
                this.testState.results,
                (p, r) => p.tests.map(t => r[t.name] || this.resultFromTest(t))
            )
            .withLatestFrom(
                this.testsOpen,
                (res, open) => open[_package.name] ? res : []
            )
    }

    getExecutionInfoForPackage(_package: TestGroup) {
        return this.testState.getExecutionInfo(new Test(_package.name));
    }

    getExecutionInfoForTest(test: Test) {
        return this.testState.getExecutionInfo(test);
    }

    ngOnInit() {
        this.results =
            Observable.combineLatest(
                this.selected.map(tg => (tg) ? tg.tests : [AllTests]),
                this.testState.results
            )
                .map(([tests, results]: Tupel<Test[], IdMap<TestResult>>) => tests.map(t => results[t.name] || this.resultFromTest(t)))
        this.packages = this.testState.packages;
        [
            this.loggingService.logOutput
                .combineLatest(this.selected)
                .subscribe(([event, selected]) => {
                    this.testActions.handleSocketEvent(selected ? new TestDetail(selected.name) : AllTests, event);
                }),
            this.loggingService.results
                .subscribe((result: TestResult) => {
                    this.testActions.resultSuccess(result);
                })
        ].forEach(s => this.subscription.add(s));
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    execute() {
        this.selected.take(1)
            .subscribe(selected => {
                if (selected) {
                    this.testActions.resetResults(new TestDetail(selected.name));
                    this.testActions.executeTestGroup(selected)
                } else {
                    this.testActions.resetResults(AllTests);
                    this.testActions.executeAll();
                }
            })
    }

    getPackageSuccess(_package: TestGroup) {
        return this.testState.results
            .map(rMap => _
                .filter(rMap, r => _package.name === 'all-tests' || r.test.packageName === _package.name)
                .reduce((success, r) => success && r.success, true)
            )
    }

    resetAll() {
        return this.packages.take(1).do(pcks => {
            [AllTests, ...pcks].forEach(pck => this.testActions.resetResults(new TestDetail(pck.name)))
        });
    }

    executeAll() {
        this.resetAll().subscribe(() => this.testActions.executeAll())
    }

    executeGroup(group: TestGroup) {
        this.resetAll().subscribe(() => this.testActions.executeTestGroup(group))
    }

    select(group ?: TestGroup) {
        this.selected.next(group);
    }

    open(test: Test) {
        this.store.dispatch(go(['/tests', 'detail', test.name, 'run']));
    }

    openConsole(_package?: TestGroup) {
        this.selected.next(_package);
        (jQuery('#dialog-console') as any).modal();
    }

    notifyError(error: any) {
        this.alertService.add(new Alert("danger", error, false));
    }
}

@Component({
    selector: 'execution-status-package',
    animations: [
        trigger('fade', [
                transition(':enter', [
                    style({opacity: 0}),
                    animate('350ms ease-in', style({opacity: 1}))
                ]),
                transition(':leave', [
                    style({opacity: 1}),
                    animate('350ms ease-in', style({opacity: 0}))
                ])
            ]
        )
    ],
    styles: [`
        .badge {
            display: flex;
        }

        .badge .badge--message {
            align-self: center;
            margin-right: 3px;
        }

        .clickable {
            cursor: pointer;
        }

        .info-container {
            transition: background .35s ease-in;
        }
    `],
    template: `
      <span *ngIf="executionInfo && executionInfo.touched">
        <img *ngIf="executionInfo.running" 
             class="ajax-loader" 
             src="assets/images/ajax-loader.gif"
             style="width: 1.3em;"/>
        <span *ngIf="success != undefined"
              [class]="'info-container ' + cssClass"
        >
          <span class="badge--message" [@fade]="message !== ''">{{message}}</span>
          <span
              title="Show logs"
              class="clickable"
              (click)="onLogs()"
              *ngIf="executionInfo.processOutput"
              [@fade]="executionInfo.processOutput !== ''"
          >
            <i class="fa fa-list"></i>
          </span>
        </span>
      </span>
    `
})
export class ExecutionStatusPackageComponent {
    @Input() executionInfo: TestExecutionInfo;
    @Input() success: boolean;
    @Output() logs = new EventEmitter();

    onLogs() {
        this.logs.next()
    }

    get message() {
        if (this.executionInfo.running) {
            return ''
        } else {
            return this.success ? 'SUCCESS' : 'FAILED'
        }
    }

    get cssClass() {
        if (this.executionInfo.running) {
            return ''
        } else {
            return `badge ${this.success ? 'badge-success' : 'badge-danger'}`
        }
    }
}

@Component({
    selector: 'execution-status-test',
    template: `
      <!--<img *ngIf="executionInfo.running && executionInfo.success == undefined" class="ajax-loader" src="assets/images/ajax-loader.gif" style="width: 1.3em;"/>-->
      <span *ngIf="result.success != undefined"
            [class]="(result.success == undefined) ? 'badge' : (result.success ? 'badge badge-success' : 'badge badge-danger')"
            [textContent]="result.success ? 'SUCCESS' : 'FAILED'"></span>
    `
})
export class ExecutionStatusTestComponent {
    @Input() result: TestResult
}