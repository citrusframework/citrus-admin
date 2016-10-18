import {Component} from 'angular2/core';
import {ROUTER_DIRECTIVES} from 'angular2/router';
import {AlertConsole} from "./alert.console";

@Component({
    selector: 'header',
    templateUrl: 'app/components/header.html',
    directives: [ROUTER_DIRECTIVES, AlertConsole]
})
export class HeaderComponent { }