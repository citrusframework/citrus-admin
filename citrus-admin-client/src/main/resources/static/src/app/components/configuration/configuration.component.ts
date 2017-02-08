import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Params} from '@angular/router';

@Component({
    templateUrl: 'config.html'
})
export class ConfigurationComponent implements OnInit{
    constructor(private route: ActivatedRoute) {
    }

    active = 'endpoints';

    ngOnInit() {
        this.route.params
            .subscribe((params: Params) => this.active = params['activeTab']);
    }

    isActive(name: string) {
        return this.active === name;
    }
}