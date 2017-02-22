import {Component} from '@angular/core';
import {Router, NavigationStart} from '@angular/router';

@Component({
    templateUrl: 'settings.html'
})
export class SettingsComponent  {

    menuEntries = [
        {name: 'Project', link:['project']},
        {name: 'Connector', link:['connector']}
    ];

    constructor(private router:Router) {
        router.events
            .startWith(new NavigationStart(46, '/settings'))
            .filter(e => e instanceof NavigationStart)
            .filter((e:NavigationStart) => e.url === '/settings')
            .subscribe(e => {
                router.navigate(['settings/project'])
            })
    }

    isActive(name: string) {
        return this.router.isActive('settings/' + name, false);
    }
}