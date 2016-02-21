import {Component,  Input} from 'angular2/core';
import {TestAction} from "../../model/tests";

@Component({
    selector: "test-action",
    template: '<div class="test-action">' +
                '<i class="fa icon-{{action.type}}"></i>' +
                '<span>{{action.type}}</span>' +
            '</div>'
})
export class TestActionComponent {

    @Input() action: TestAction;

    constructor() {}

}