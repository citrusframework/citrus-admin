import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {go} from '@ngrx/router-store';
import {Store} from '@ngrx/store'
import {AppState} from "../state.module";

@Component({
    selector: 'sidebar',
    templateUrl: 'sidebar.html'
})
export class SidebarComponent {

    constructor(
        private router: Router,
        private store:Store<AppState>
    ) {}

    navigate(url: string) {
        this.store.dispatch(go([url]));
    }

    isActive(name: string) {
        return this.router.isActive(name, false);
    }
}