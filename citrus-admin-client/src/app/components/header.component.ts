import {Component} from 'angular2/core';
import {ROUTER_DIRECTIVES} from 'angular2/router';

@Component({
    selector: 'header',
    templateUrl: 'app/components/header.html',
    directives: [ROUTER_DIRECTIVES]
})
export class HeaderComponent { }