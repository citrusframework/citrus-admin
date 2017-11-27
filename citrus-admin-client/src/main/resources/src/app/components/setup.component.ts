import {Component} from '@angular/core';
import {Router} from "@angular/router";

@Component({
    selector: 'setup',
    templateUrl: 'setup.html'
})
export class SetupComponent {

    constructor(private router: Router) {}

    create() {
        this.router.navigate(['new']);
    }

    open() {
        this.router.navigate(['open']);
    }
}
