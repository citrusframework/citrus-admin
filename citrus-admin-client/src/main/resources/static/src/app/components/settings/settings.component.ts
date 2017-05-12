import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router, NavigationStart} from '@angular/router';
import {Subscription} from "rxjs/Subscription";

@Component({
    templateUrl: 'settings.html'
})
export class SettingsComponent implements OnInit, OnDestroy {
    private subscription = new Subscription;
    menuEntries = [
        {name: 'Project', link:['project']},
        {name: 'Modules', link:['modules']},
        {name: 'Connector', link:['connector']}
    ];

    constructor(private router:Router) {}

    ngOnInit() {
        this.subscription.add(
            this.router.events
                .startWith(new NavigationStart(46, '/settings'))
                .filter(e => e instanceof NavigationStart)
                .filter((e:NavigationStart) => e.url === '/settings')
                .subscribe(e => {
                    this.router.navigate(['settings/project'])
                })
        )
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    isActive(name: string) {
        return this.router.isActive('settings/' + name, false);
    }
}