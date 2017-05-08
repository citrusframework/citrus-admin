import {Component} from '@angular/core';
import {Router, NavigationStart} from '@angular/router';
import {TestStateActions} from "./test.state";

@Component({
    templateUrl: 'tests.html'
})
export class TestsComponent {

    menuEntries = [
        {name: 'List', link:['list']},
        {name: 'Run', link:['run']},
        {name: 'Details', link:['detail']}
    ];

    constructor(private router:Router,
                private testActions:TestStateActions) {
        testActions.fetchPackages();
    }

    isActive(name: string) {
        return this.router.isActive('tests/' + name, false);
    }
}