import {Component,  Input, Output, EventEmitter} from 'angular2/core';
import {TestAction} from "../../model/tests";

@Component({
    selector: "test-action",
    template: '<div class="test-action" (click)="select()">' +
    '<i class="fa icon-{{action.type}}"></i>' +
    '<span>{{action.type}}</span>' +
    '</div>'
})
export class TestActionComponent {

    @Input() action: TestAction;

    @Output() selected = new EventEmitter(true);

    constructor() {}

    select() {
        this.selected.emit(this.action);
    }

}