import {Component,  Input} from '@angular/core';

@Component({
    selector: "test-transition",
    template: '<div class="test-transition" (click)="select()">' +
    '<i class="fa fa-arrow-down"></i>' +
    '</div>'
})
export class TestTransitionComponent {

    constructor() {}

}