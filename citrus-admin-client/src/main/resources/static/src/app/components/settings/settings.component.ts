import {Component} from '@angular/core';
import {Router} from '@angular/router';

@Component({
    templateUrl: 'settings.html'
})
export class SettingsComponent  {

    menuEntries = [
        {name: 'Project', link:['project']},
        {name: 'Modules', link:['modules']},
        {name: 'Connector', link:['connector']}
    ];

    constructor(private router:Router) {}

    isActive(name: string) {
        return this.router.isActive('settings/' + name, false);
    }
}