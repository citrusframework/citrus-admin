import {Component} from '@angular/core';
import {Router} from '@angular/router';

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

    constructor(private router:Router) {}

    isActive(name: string) {
        return this.router.isActive('configuration/' + name, false);
    }
}