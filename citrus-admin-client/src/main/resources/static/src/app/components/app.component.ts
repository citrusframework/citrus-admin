import { Component } from '@angular/core';
import {Router} from "@angular/router";
@Component({
    selector: 'app',
    templateUrl: 'app.html',
})
export class AppComponent {
    constructor(private router: Router) {
    }

    isSetupMode() {
        return this.router.isActive('setup', true) ||
            this.router.isActive('open', true) ||
            this.router.isActive('about', true) ||
            this.router.isActive('new', true);
    }
}
