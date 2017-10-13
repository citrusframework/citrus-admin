import {Component} from '@angular/core';
import {Router} from "@angular/router";

@Component({
    selector: 'sidebar',
    templateUrl: 'sidebar.html'
})
export class SidebarComponent {

    constructor(private router: Router) {
    }

    navigate(url: string) {
        this.router.navigate([url]);
    }

    isActive(name: string) {
        return this.router.isActive(name, false);
    }
}