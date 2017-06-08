import {Component} from '@angular/core';
import {Router, NavigationStart} from '@angular/router';

const MenuEntry = (name:string, link:string[]) => ({name, link});

@Component({
    templateUrl: 'configuration.html'
})
export class ConfigurationComponent {

    menuEntries = [
        MenuEntry('Endpoints', ['endpoints']),
        MenuEntry('Spring Application Context', ['spring-context']),
        MenuEntry('Spring Beans', ['spring-beans']),
        MenuEntry('Schema Definitions', ['schema-repository']),
        MenuEntry('Global Variables', ['global-variables']),
        MenuEntry('Functions', ['function-library']),
        MenuEntry('Validation Matcher', ['validation-matcher']),
        MenuEntry('Data dictionaries', ['data-dictionary']),
        MenuEntry('Namespaces', ['namespace-context'])
    ];

    constructor(private router:Router) {
        router.events
            .startWith(new NavigationStart(42, '/configuration'))
            .filter(e => e instanceof NavigationStart)
            .filter((e:NavigationStart) => e.url === '/configuration')
            .subscribe(e => {
                router.navigate(['configuration/endpoints'])
            })
    }

    isActive(name: string) {
        return this.router.isActive('configuration/' + name, false);
    }
}