import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router, NavigationEnd, NavigationStart} from '@angular/router';

const MenuEntry = (name:string, link:string[]) => ({name, link});

@Component({
    templateUrl: 'config.html'
})
export class ConfigurationComponent {


    menuEntries = [
        MenuEntry('Endpoints', ['endpoints']),
        MenuEntry('Schema Definitions', ['schema-definition']),
        MenuEntry('Global Variables', ['global-variables']),
        MenuEntry('Functions', ['functions']),
        MenuEntry('Validation Matcher', ['validation-matcher']),
        MenuEntry('Data dictionaries', ['data-dictionaries']),
        MenuEntry('Namespaces', ['namespaces'])
    ]

    constructor(private route: ActivatedRoute,
        private router:Router
    ) {
        router.events
            .startWith(new NavigationStart(42, '/configuration'))
            .filter(e => e instanceof NavigationStart)
            .filter((e:NavigationStart) => e.url === '/configuration')
            .subscribe(e => {
                router.navigate(['configuration/endpoints'])
            })
    }

    isActive(name: string) {
        return this.router.isActive('configuration/' +name, false);
    }
}