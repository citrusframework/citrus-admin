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

        router.events
            .startWith(new NavigationStart(44, '/tests'))
            .filter(e => e instanceof NavigationStart)
            .filter((e:NavigationStart) => e.url === '/tests')
            .subscribe(e => {
                router.navigate(['tests/run'])
            });
    }

    isActive(name: string) {
        return this.router.isActive('tests/' + name, false);
    }
}