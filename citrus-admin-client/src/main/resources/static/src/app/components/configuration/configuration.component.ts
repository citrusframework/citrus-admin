import {Component, OnInit} from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
    templateUrl: 'config.html'
})
export class ConfigurationComponent {
    constructor(private route: ActivatedRoute) {
        let activeTabParam: string = route.snapshot.params['activeTab'];
        if (activeTabParam != null) {
            this.active = activeTabParam;
        }
    }

    active = 'endpoints';

    isActive(name: string) {
        return this.active === name;
    }
}